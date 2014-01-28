import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class AircraftLandingTest {
	
	AircraftLanding al;

	@Before
	public void setUp() throws Exception {
		int[] capacity = new int[]{6,5,3,2,1};
		
		String[] schedule = new String[]{
		"11:12:3",
	    "12:13:2",
	    "13:14:4",
	    "14:15:4",
	    "15:16:3",
	    "16:17:2",
	    "17:18:6",
	    "18:19:4",
	    "19:20:4",
	    "20:21:4",
	    "21:22:1",
	    "22:23:1"};
		
		al = new AircraftLanding(schedule, capacity);
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(al.getnPlanes() == al.getWindowStart().length);
		
		for(int duration : al.getWindowDuration()){
			assertTrue(duration >= 30);
			assertTrue(duration < 270);
		}
		
		for(int type : al.getTypePlane()){
			assertTrue(type == 1 || type == 2 || type == 3);
		}
		
		for(int start : al.getWindowStart()){
			assertTrue(start >= 6 * 60 && start < 24 * 60);
		}
		
		for(int end : al.getWindowEnd()){
			assertTrue(end > 6 * 60 && end <= 24 * 60);
		}
		
		
	}

}
