package display;

import java.util.ArrayList;
import java.util.List;

public class Piste {
	
	private int capacite;
	private int id;
	private List<Avion> presents;
	
	public Piste(int capacity, int id){
		this.capacite = capacity;
		this.id = id;
		this.presents = new ArrayList<Avion>();
	}

	public List<Avion> getPresents() {
		return presents;
	}

	public int getId() {
		return id;
	}

	public int getCapacite() {
		return capacite;
	}

}
