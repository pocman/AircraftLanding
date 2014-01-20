import solver.ResolutionPolicy;
import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.ICF;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.search.loop.monitors.SMF;
import solver.search.strategy.ISF;
import solver.search.strategy.IntStrategyFactory;
import solver.search.strategy.strategy.StrategiesSequencer;
import solver.variables.IntVar;
import solver.variables.Task;
import solver.variables.VF;
import solver.variables.VariableFactory;
import util.tools.ArrayUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


public class AircraftLanding {

	/**
	 * @param args
	 */

	Task[] activityPlanes;
	int[] setOfTypes = new int[]{1, 2, 3};
	Random r = new Random(42);
	IntVar[] duration, landing, takeOff; //for each plane
	IntVar[][] tracks; //binary variable
	int[] windowStart, windowDuration, windowEnd; //for each plane
	int[] typePlane; //1,2 ou 3 correspondant a la capacite utilisee
	int[] capacity; //of the track
	int nPlanes; //number of planes
	int nTracks; //number of tracks
	IntVar minBreak;
	IntVar[] tracksByPlane;
	int MAX_TIME = 60*24;
	String[] schedule;
	
	public AircraftLanding(String[] schedule, int[] capacity, boolean fenetreFixe){
		this.schedule=schedule;
		ArrayList<int[]> planes = new ArrayList<int[]>();
		if(!fenetreFixe) {
			for(String s : schedule){
				String[] temp = s.split(":");
				int[] planesByTF = new int[]{Integer.parseInt(temp[0]), Integer.parseInt(temp[1])};
				planes.add(planesByTF);
			}
		
			this.setnTracks(capacity.length);
			this.setCapacity(capacity);
			this.setnPlanes(planes.size());
			this.setTypePlane(new int[this.getnPlanes()]);
		
			this.windowStart = new int[this.getnPlanes()];
			this.windowDuration = new int[this.getnPlanes()];
			this.windowEnd = new int[this.getnPlanes()];
		
			for(int i = 0 ; i < this.getnPlanes(); i++){
				this.windowDuration[i] = planes.get(i)[0];
				this.typePlane[i] = planes.get(i)[1];
			}
		}
		else {
			for(String s : schedule) {
				String[] temp = s.split(":");
				int[] planesByTF = new int[]{Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3])};
				planes.add(planesByTF);
			}
			this.setnTracks(capacity.length);
			this.setCapacity(capacity);
			this.setnPlanes(planes.size());
			this.setTypePlane(new int[this.getnPlanes()]);
		
			this.windowStart = new int[this.getnPlanes()];
			this.windowDuration = new int[this.getnPlanes()];
			this.windowEnd = new int[this.getnPlanes()];
		
			for(int i = 0 ; i < this.getnPlanes(); i++){
				this.windowStart[i] = planes.get(i)[0]*60;
				this.windowEnd[i] = planes.get(i)[1]*60+planes.get(i)[2];
				this.windowDuration[i] = this.windowEnd[i]-this.windowStart[i];
				this.typePlane[i] = planes.get(i)[3];
			}
		}
	}

	public void model(Solver s) {

		activityPlanes = new Task[nPlanes];
		landing = new IntVar[nPlanes];
		duration = new IntVar[nPlanes];
		takeOff = new IntVar[nPlanes];
		tracksByPlane = new IntVar[nPlanes];

		for (int i = 0; i < nPlanes; i++) {
			landing[i] = VariableFactory.bounded("Landing " + i, windowStart[i], windowEnd[i], s);
			//Constraint on the minimal duration is here
			duration[i] = VariableFactory.bounded("Duration on airport " + i, 30, windowDuration[i], s);
			takeOff[i] = VariableFactory.bounded("Take off " + i, windowStart[i], windowEnd[i], s);
			tracksByPlane[i] = VariableFactory.bounded("Tracks for plane " + i, 0, this.getnTracks(), s);
			activityPlanes[i] = VariableFactory.task(landing[i], duration[i], takeOff[i]);
		}

		//for each plane which track, it's on
		tracks = VariableFactory.enumeratedMatrix("track of plane", nTracks, nPlanes, 0, 1, s);

		for (int i = 0; i < nTracks; i++) {
			for (int j = 0; j < nPlanes; j++) {
				LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(tracks[i][j], "=", 1), IntConstraintFactory.arithm(tracksByPlane[j], "=", i));
			}
		}

		s.post(IntConstraintFactory.alldifferent(ArrayUtils.append(this.landing, this.takeOff), "BC"));
		//Un avion ne peut etre que sur une seule piste
		for (int plane = 0; plane < nPlanes; plane++) {
			s.post(ICF.count(1, ArrayUtils.getColumn(tracks, plane), VF.fixed(1, s)));
		}

		//contrainte souple de precedence entre les avions
		this.contraintePrecedence(s);

		//contrainte cumulative
		IntVar[][] heightInCumulatives = new IntVar[this.getnTracks()][this.getnPlanes()];
		for (int u = 0; u < this.getnTracks(); u++) {
			for (int v = 0; v < this.getnPlanes(); v++) {
				heightInCumulatives[u][v] = VariableFactory.enumerated("heightInCumulative" + u + v, 1, 3, s);
				s.post(IntConstraintFactory.times(VariableFactory.fixed(this.typePlane[v], s), this.tracks[u][v], heightInCumulatives[u][v]));
			}
			s.post(IntConstraintFactory.cumulative(activityPlanes, heightInCumulatives[u], VariableFactory.fixed(this.getCapacity()[u], s)));
		}
	}

	public void contraintePrecedence(Solver s) {
		IntVar[] brokenConstraint = VariableFactory.boundedArray("broken constraint", nPlanes, 0, 1, s);
		minBreak = VF.enumerated("breaker", 0, nPlanes * nPlanes, s);
		for (int i = 0; i < nPlanes; i++) {
			for (int j = 0; j < nPlanes; j++) {
				Constraint[] cons = new Constraint[]{IntConstraintFactory.arithm(landing[i], "<=", landing[j]), IntConstraintFactory.arithm(takeOff[i], ">=", takeOff[j])};
				LogicalConstraintFactory.ifThenElse(LogicalConstraintFactory.and(cons), IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(1, s)),
						IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(0, s)));
			}
		}
		s.post(ICF.sum(brokenConstraint, minBreak));
	}

	public void solve(Solver s) {
		SMF.log(s, true, false);
		s.set(new StrategiesSequencer(IntStrategyFactory.inputOrder_InDomainMin(new IntVar[]{minBreak}),
				ISF.inputOrder_InDomainMax(ArrayUtils.flatten(tracks)),
				ISF.inputOrder_InDomainMin(landing),
				ISF.inputOrder_InDomainMax(takeOff),
				ISF.inputOrder_InDomainMax(duration)));
		s.findOptimalSolution(ResolutionPolicy.MINIMIZE, minBreak);
	}

	private void sortCapacity() {
		Arrays.sort(capacity);
		ArrayUtils.reverse(capacity);
	}

	private void sortPlanes() {
		int[] planes = new int[this.getnPlanes()];
		int k = 0;
		for (int s = 3; s > 0; s--) {
			for (int i = 0; i < this.getnPlanes(); i++) {
				if (this.typePlane[i] == s) {
					planes[k] = i;
					k++;
				}
			}
		}
		this.setNewOrder(planes);
	}

	private void setNewOrder(int[] planes) {
		int[] oldWindowStart = this.windowStart;
		int[] oldWindowDuration = this.windowDuration;
		int[] oldWindowEnd = this.windowEnd;
		int[] oldTypePlane = this.typePlane;
		this.windowStart = new int[this.windowStart.length];
		this.windowDuration = new int[this.windowDuration.length];
		this.windowEnd = new int[this.windowEnd.length];
		this.typePlane = new int[this.typePlane.length];
		for (int i = 0; i < this.getnPlanes(); i++) {
			this.windowStart[i] = oldWindowStart[planes[i]];
			this.windowDuration[i] = oldWindowDuration[planes[i]];
			this.windowEnd[i] = oldWindowEnd[planes[i]];
			this.typePlane[i] = oldTypePlane[planes[i]];
		}
	}

	public void prettyOutput() {
		for (int t = 0; t < this.getnTracks(); t++) {
			//on place les avions dans l'ordre d'atterrissage
			HashMap<Integer, Integer> ordedPlaneOnTheTrack = new HashMap<Integer, Integer>(MAX_TIME);
			for (int plane = 0; plane < nPlanes; plane++) {
				if (this.tracks[t][plane].getValue() == 1) {
					ordedPlaneOnTheTrack.put(this.landing[plane].getValue(), plane);
				}
			}
			//On genere l'ensemble des pas de temps d'interet pour chaque piste
			HashMap<Integer, Integer> interrestingPoints = new HashMap<Integer, Integer>();
			for (int keyPlane : ordedPlaneOnTheTrack.keySet()) {
				int plane = ordedPlaneOnTheTrack.get(keyPlane);
				if (interrestingPoints.containsKey(this.landing[plane].getValue())) {
					interrestingPoints.put(this.landing[plane].getValue(), this.typePlane[plane] + interrestingPoints.get(this.landing[plane].getValue()));
				} else {
					interrestingPoints.put(this.landing[plane].getValue(), this.typePlane[plane]);
				}
				if (interrestingPoints.containsKey(this.takeOff[plane].getValue())) {
					interrestingPoints.put(this.takeOff[plane].getValue(), this.typePlane[plane] + interrestingPoints.get(this.landing[plane].getValue()));
				} else {
					interrestingPoints.put(this.takeOff[plane].getValue(), this.typePlane[plane]);
				}
			}
			System.out.print("load of track N " + t + " : ");
			String s = "";
			for (int key : interrestingPoints.keySet()) {
				s = s + " " + interrestingPoints.get(key);
			}
			System.out.println(s);
			System.out.print("load by plane");
			for (int keyPlane : ordedPlaneOnTheTrack.keySet()) {
				int plane = ordedPlaneOnTheTrack.get(keyPlane);
				String sPlane = "Plane N " + plane + " : ";
				System.out.print(".");
				for (int key : interrestingPoints.keySet()) {
					if (key > this.landing[plane].getValue())
						sPlane = sPlane + " . ";
					else if (key == this.landing[plane].getValue())
						sPlane = sPlane + " \\ ";
					else if (key <= this.landing[plane].getValue() && key < this.takeOff[plane].getValue())
						sPlane = sPlane + " _ ";
					else if (key == this.takeOff[plane].getValue())
						sPlane = sPlane + " // ";
					else
						sPlane = sPlane + " . ";
				}
			}
		}
	}

	/**
	 * idPlane;idtrack;arrivalTime;departureTime;duration;capacity
	 *
	 * @param nameFile
	 * @throws IOException
	 */
	public void csvOutput(String nameFile) throws IOException {
		System.out.println("Creating CSV File");
		File file = new File(nameFile + ".csv");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("idPlane; idtrack; arrivalTime; departureTime; duration; capacity" + "\n");
		for (int plane = 0; plane < nPlanes; plane++) {
			String content = "";
			content += plane + "; ";
			int track = 0;
			for (int t = 0; t < nTracks; t++) {
				if (tracks[t][plane].getValue() == 1)
					track = t;
			}
			content += track + "; " + landing[plane].getValue() + "; " + takeOff[plane].getValue() + "; " + duration[plane].getValue() + "; " + typePlane[plane] + "\n";
			bw.write(content);
		}
		bw.close();
	}

	public int[] getWindowEnd() {
		return windowEnd;
	}

	public void setWindowEnd(int[] windowEnd) {
		this.windowEnd = windowEnd;
	}

	public int[] getWindowStart() {
		return windowStart;
	}


	public void setWindowStart(int[] windowStart) {
		this.windowStart = windowStart;
	}


	public int[] getWindowDuration() {
		return windowDuration;
	}


	public void setWindowDuration(int[] windowDuration) {
		this.windowDuration = windowDuration;
	}


	public int[] getTypePlane() {
		return typePlane;
	}


	public void setTypePlane(int[] typePlane) {
		this.typePlane = typePlane;
	}


	public int[] getCapacity() {
		return capacity;
	}


	public void setCapacity(int[] capacity) {
		this.capacity = capacity;
	}


	public int getnPlanes() {
		return nPlanes;
	}


	public void setnPlanes(int nPlanes) {
		this.nPlanes = nPlanes;
	}


	public int getnTracks() {
		return nTracks;
	}


	public void setnTracks(int nTracks) {
		this.nTracks = nTracks;
	}

	public String[] getSchedule() {
		return schedule;
	}

	public void chooseStrategy(Solver s) {
		s.set(new StrategiesSequencer(IntStrategyFactory.inputOrder_InDomainMin(this.takeOff), IntStrategyFactory.inputOrder_InDomainMin(this.landing), IntStrategyFactory.inputOrder_InDomainMin(this.duration), IntStrategyFactory.inputOrder_InDomainMin(this.tracksByPlane)));
	}


	public static void main(String[] args) {
		AircraftLanding al = InstanceGeneratorDummy.generator2();
		Solver s = new Solver("aircraftLanding");
		al.model(s);
		al.chooseStrategy(s);
		al.solve(s);
		al.prettyOutput();
		try {
			al.csvOutput("test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
