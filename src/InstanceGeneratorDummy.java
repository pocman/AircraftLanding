
public class InstanceGeneratorDummy {

	/**
	 * @param args
	 */
	public static AircraftLanding generator1(){
			
		int[] capacity = new int[]{6,5,3,2,1};
		
		String[] schedule = new String[]{
		"11:12:1:1:1",
	    "12:13:0:1:1",
	    "13:14:2:1:1",
	    "14:15:3:1:0",
	    "15:16:1:1:1",
	    "16:17:0:1:1",
	    "17:18:3:2:1",
	    "18:19:2:0:2",
	    "19:20:2:1:1",
	    "20:21:3:0:1",
	    "21:22:0:0:1",
	    "22:23:0:1:0"};
		
		return new AircraftLanding(schedule, capacity, true);		
	}
	
	public static AircraftLanding generator2(){
		
		int[] capacity = new int[]{6};
		
		String[] schedule = new String[]{
		"11:12:10:1",
		"11:12:10:1",
		"11:12:10:1",
		"11:12:10:1"};
		
		return new AircraftLanding(schedule, capacity, true);		
	}

}
