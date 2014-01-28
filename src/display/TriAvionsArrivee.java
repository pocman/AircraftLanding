package display;

import java.util.Comparator;

public class TriAvionsArrivee implements Comparator<Avion>{

	public int compare(Avion o1, Avion o2) {
		return o1.getHeureArrivee() < o2.getHeureArrivee() ? -1 : 1;
	}

}
