import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import solver.ResolutionPolicy;
import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.ICF;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.variables.IntVar;
import solver.variables.Task;
import solver.variables.VariableFactory;


public class AircraftLanding {

	/**
	 * @param args
	 */
	
	Task[] activityPlanes;
	int[] setOfTypes = new int[]{1,2,3};
	Random r = new Random(42);
	IntVar[] duration, landing, takeOff; //for each plane
	IntVar[][] tracks; //binary variable
	int[] windowStart, windowDuration, windowEnd; //for each plane
	int[] typePlane; //1,2 ou 3 correspondant � la capacit� utilis�e
	int[] capacity; //of the track
	int nPlanes; //number of planes
	int nTracks; //number of tracks
	IntVar minBreak;
	
	//constructor for number of planes by hour and with given number of planes by type
	//We make the hypothesis that planes are landing in the hours and taking of in the same hour
	//Fa = [30,60]
	public AircraftLanding(String[] schedule, int[] capacity){
		
		ArrayList<int[]> planes = new ArrayList<int[]>();		
		for(String s : schedule){
			String[] temp = s.split(":");
			int[] planesByTF = new int[]{Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Integer.parseInt(temp[4])};
			for(int i = 0; i < planesByTF[2]; i++){
				planes.add(new int[]{planesByTF[0]*60, planesByTF[1]*60, setOfTypes[0]});
			}
			for(int i = 0; i < planesByTF[3]; i++){
				planes.add(new int[]{planesByTF[0]*60, planesByTF[1]*60, setOfTypes[1]});
			}
			for(int i = 0; i < planesByTF[4]; i++){
				planes.add(new int[]{planesByTF[0]*60, planesByTF[1]*60, setOfTypes[2]});
			}
		}
		
		this.setnTracks(capacity.length);
		this.setCapacity(capacity);
		this.setnPlanes(planes.size());
		
		this.windowStart = new int[this.getnPlanes()];
		this.windowDuration = new int[this.getnPlanes()];
		this.windowEnd = new int[this.getnPlanes()];
		this.typePlane = new int[this.getnPlanes()];
		
		for(int i = 0 ; i < this.getnPlanes(); i++){
			this.windowStart[i] = planes.get(i)[0];
			this.windowEnd[i] = planes.get(i)[1];
			this.windowDuration[i] = this.windowEnd[i] - this.windowStart[i];
			this.typePlane[i] = planes.get(i)[2];
		}
	
	}
	
	public void model(Solver s){
		
		activityPlanes = new Task[nPlanes];
		
		for(int i = 0; i < nPlanes; i++){
			landing[i] = VariableFactory.bounded("Landing " + i, windowStart[i], windowEnd[i], s);
			//Constraint on the minimal duration is here
			duration[i] = VariableFactory.bounded("Duration on airport " + i , 30, windowDuration[i], s);
			takeOff[i] = VariableFactory.bounded("Take off " + i, windowStart[i], windowEnd[i], s);
			
			activityPlanes[i] = VariableFactory.task(landing[i], duration[i], takeOff[i]);
		}
		
		//for each plane which track, it's on
		tracks = VariableFactory.enumeratedMatrix("track of plane", nTracks, nPlanes, 0, 1, s);

		this.contraintePrecedence(s);

		IntVar[][] heightInCumulatives = new IntVar[this.getnTracks()][this.getnPlanes()];
		for(int u = 0; u < this.getnTracks(); u++) {
			for(int v=0; v < this.getnPlanes(); v++) {
				heightInCumulatives[u][v] = VariableFactory.enumerated("heightInCumulative" + u + v, 1,3,s);
				s.post(IntConstraintFactory.times(VariableFactory.fixed(this.typePlane[v],s),this.tracks[u][v],heightInCumulatives[u][v]));
			}
			s.post(IntConstraintFactory.cumulative(activityPlanes, heightInCumulatives[u], VariableFactory.fixed(this.getCapacity()[u], s)));
		}
	}
	
	public void contraintePrecedence(Solver s){
		IntVar brokenConstraint[] = VariableFactory.boundedArray("broken constraint", nPlanes, 0, 1, s);
		for (int i = 0; i < nPlanes; i++) {
			for (int j = 0; j < nPlanes && i!=j; j++) {
				Constraint[] cons = new Constraint[]{IntConstraintFactory.arithm(landing[i], "<=", landing[j]), IntConstraintFactory.arithm(takeOff[i], ">=", takeOff[j])};
				LogicalConstraintFactory.ifThenElse(LogicalConstraintFactory.and(cons), IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(1, s)),
																						IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(0, s)));
			}
		}
		s.post(ICF.sum(brokenConstraint, minBreak));
		s.findOptimalSolution(ResolutionPolicy.MINIMIZE, minBreak);
	}
	
	public void prettyOutput(){
		for(int t = 0 ; t < this.getnTracks(); t++){
			//on place les avions dans l'ordre d'atterrissage
			ArrayList<Integer> ordedPlaneOnTheTrack = new ArrayList<Integer>();
			for(int plane = 0; plane < this.tracks[t].length; plane++){
				if(this.tracks[t][plane].getValue() == 1){
					ordedPlaneOnTheTrack.add(this.landing[plane].getValue(), plane);
				}
			}
			//On genere l'ensemble des pas de temps d'interet pour chaque piste
			HashMap<Integer, Integer> interrestingPoints = new HashMap<Integer, Integer>();
			for(int plane : ordedPlaneOnTheTrack){
				if(interrestingPoints.containsKey(this.landing[plane].getValue())){
					interrestingPoints.put(this.landing[plane].getValue(), this.typePlane[plane] + interrestingPoints.get(this.landing[plane].getValue()));
				}
				else{
					interrestingPoints.put(this.landing[plane].getValue(), this.typePlane[plane]);
				}
				if(interrestingPoints.containsKey(this.takeOff[plane].getValue())){
					interrestingPoints.put(this.takeOff[plane].getValue(), this.typePlane[plane] + interrestingPoints.get(this.landing[plane].getValue()));
				}
				else{
					interrestingPoints.put(this.takeOff[plane].getValue(), this.typePlane[plane]);
				}
			}
			System.out.println("load of track N� " + t + " : ");
			String s = "";
			for(int key : interrestingPoints.keySet()){
				s = s + " " + interrestingPoints.get(key);
			}
			System.out.println(s);
			System.out.println("load by plane");
			for(int plane : ordedPlaneOnTheTrack){
				String sPlane = "Plane N� " + plane + " : ";
				for(int key : interrestingPoints.keySet()){
					if(key > this.landing[plane].getValue())
						sPlane = sPlane + "   ";
					else if (key == this.landing[plane].getValue())
						sPlane = sPlane + " \\ ";
					else if(key <= this.landing[plane].getValue() && key < this.takeOff[plane].getValue())
						sPlane = sPlane + " _ ";
					else if(key == this.takeOff[plane].getValue())
						sPlane = sPlane + " // ";
					else
						sPlane = sPlane + "   ";					
				}
			}			
		}
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


	public static void main(String[] args) {
		

	}

}
