import java.util.ArrayList;
import java.util.Random;

import solver.Solver;
import solver.constraints.IntConstraintFactory;
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
	int[] typePlane; //1,2 ou 3 correspondant à la capacité utilisée
	int[] capacity; //of the track
	int nPlanes; //number of planes
	int nTracks; //number of tracks
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
				this.windowStart[i] = planes.get(i)[0];
				this.windowEnd[i] = planes.get(i)[1]+planes.get(i)[2];
				this.windowDuration[i] = this.windowEnd[i]-this.windowStart[i];
				this.typePlane[i] = planes.get(i)[3];
			}
		}
	}
	

	public void model(Solver s){
		
		activityPlanes = new Task[nPlanes];
		
		for(int i = 0; i < nPlanes; i++){
			landing[i] = VariableFactory.bounded("landing " + i, windowStart[i], windowEnd[i], s);
			//Constraint on the minimal duration is here
			duration[i] = VariableFactory.bounded("duration on airport " + i , 30, windowDuration[i], s);
			takeOff[i] = VariableFactory.bounded("landing " + i, windowStart[i], windowEnd[i], s);
			
			activityPlanes[i] = VariableFactory.task(landing[i], duration[i], takeOff[i]);
		}
		
		//for each plane which track, it's on
		tracks = VariableFactory.enumeratedMatrix("track of plane", nTracks, nPlanes, 0, 1, s);

		//contrainte souple de précédence entre les avions		
		for(int i = 0 ; i < this.getnTracks(); i++){
			
		}		
		//s.post(IntConstraintFactory.cumulative(activityPlanes, typePlane, capacity));

		
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

	public static void main(String[] args) {
		

	}

}
