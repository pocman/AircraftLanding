import java.util.ArrayList;
import java.util.Random;

import solver.Solver;
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
	IntVar[] duration, landing, takeOff, track; //for each plane
	int[] windowStart, windowDuration, windowEnd; //for each plane
	int[] typePlane; //1,2 ou 3 correspondant à la capacité utilisée
	int[] capacity; //of the track
	int nPlanes; //number of planes
	int nTracks; //number of tracks
	
	public AircraftLanding(String[] schedule, int nTracks){
		
		ArrayList<int[]> planes = new ArrayList<int[]>();
		
		for(String s : schedule){
			String[] temp = s.split(":");
			int[] planesByTF = new int[]{Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2])};
			for(int i = 0; i < planesByTF[2]; i++){
				planes.add(new int[]{planesByTF[0]*60, planesByTF[1]*60, });
			}
		}
		
		this.setnTracks(nTracks);
		this.setnPlanes(wStart.length);
		this.set
	}
	
	public void model(Solver s){
		
		activityPlanes = new Task[nPlanes];
		
		for(int i = 0; i < nPlanes; i++){
			landing[i] = VariableFactory.bounded("landing " + i, windowStart[i], windowEnd[i], s);
			duration[i] = VariableFactory.bounded("duration on airport " + i , 30, windowDuration[i], s);
			takeOff[i] = VariableFactory.bounded("landing " + i, windowStart[i], windowEnd[i], s);
			
			activityPlanes[i] = VariableFactory.task(landing[i], duration[i], takeOff[i]);
		}
		
		//for each plane which track, it's on
		track = VariableFactory.enumeratedArray("track of plane", nPlanes, 0,  nTracks, s);
		
		
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
