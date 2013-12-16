import solver.Solver;
import solver.variables.IntVar;
import solver.variables.Task;
import solver.variables.VariableFactory;


public class AircraftLanding {

	/**
	 * @param args
	 */
	
	Task[] activityPlanes;
	IntVar[] duration, landing, takeOff, track; //for each plane
	int[] windowStart, windowDuration; //for each plane
	int[] typePlane; //1,2 ou 3 correspondant à la capacité utilisée
	int[] capacity; //of the track
	int nPlanes; //number of planes
	int nTracks; //number of tracks
	
	public void model(Solver s){
		
		activityPlanes = new Task[nPlanes];
		
		for(int i = 0; i < nPlanes; i++){
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
