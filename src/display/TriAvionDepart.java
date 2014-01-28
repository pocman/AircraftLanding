package display;

import java.util.Comparator;

public class TriAvionDepart implements Comparator<Avion> {

	public int compare(Avion o1, Avion o2) {
		return o1.getHeureDepart()<o2.getHeureDepart() ? -1 : 1;
	}

}
