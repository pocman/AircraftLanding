import solver.Solver;
import solver.variables.IntVar;
import solver.variables.Task;
import solver.variables.VariableFactory;


public class AircraftLanding {

	/**
	 * @param args
	 */
	
	Task[] planes;
	IntVar[] duration, landing, takeOff, window, track; //for each plane
	int[] typePlane; //1,2 ou 3 correspondant à la capacité utilisée
	int[] capacity; //of the track
	
	
	public void model(Solver s){

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
