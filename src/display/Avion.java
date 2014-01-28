package display;

public class Avion {

	private int idAvion;
	private int idPiste;
	private int heureArrivee;
	private int heureDepart;
	private int capacite;
	
	public Avion(int idPlane, int idTrack, int arrivalTime, int departureTime, int capacity){
		this.idAvion = idPlane;
		this.idPiste = idTrack;
		this.heureArrivee = arrivalTime;
		this.heureDepart = departureTime;
		this.capacite = capacity;
	}

	public int getIdAvion() {
		return idAvion;
	}

	public int getIdPiste() {
		return idPiste;
	}

	public int getHeureArrivee() {
		return heureArrivee;
	}

	public int getHeureDepart() {
		return heureDepart;
	}

	public int getCapacite() {
		return capacite;
	}
}
