package display;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OutPut {

	private List<Avion> triDepart;
	private List<Avion> triArrivee;
	private int nbPistes;

	public OutPut(List<Avion> planes, int nbTracks){
		Collections.sort(planes, new TriAvionDepart());
		triDepart = new ArrayList<Avion>();
		triDepart.addAll(planes);

		Collections.sort(planes, new TriAvionsArrivee());
		triArrivee = new ArrayList<Avion>();
		triArrivee.addAll(planes);
		nbPistes = nbTracks;
	}

	public void joliOutput() throws InterruptedException{
		int j = 0;
		int heure;
		for(int i= 0; i<triDepart.size(); i++){
			while(j < triArrivee.size() && triDepart.get(i).getHeureDepart()>triArrivee.get(j).getHeureArrivee()){
				heure = triArrivee.get(j).getHeureArrivee();
				if(heure%60<10) System.out.println(heure/60+"h0"+heure%60);
				else System.out.println(heure/60+"h"+heure%60);
				for(int k = 0; k<nbPistes; k++){
					System.out.print("Piste "+k+" "+Representation.TRACK_SEPARATION);
					if(k == triArrivee.get(j).getIdPiste()){
						System.out.println("Arrivee avion "+triArrivee.get(j).getIdAvion());
						switch(triArrivee.get(j).getCapacite()){
						case(1) : System.out.println(Representation.ARRIVEE_PETIT_AVION); break;
						case(2) : System.out.println(Representation.ARRIVEE_MOYEN_AVION); break;
						case(3) : System.out.println(Representation.ARRIVEE_GRAND_AVION); break;
						}
					}
					else{
						System.out.println();
						System.out.println(Representation.NO_PLANE);
					}
				}
				j++;
				System.out.println("========"+Representation.TRACK_SEPARATION);
				System.out.println();
				System.out.println();
				Thread.sleep(1000);
			}
			heure = triDepart.get(i).getHeureDepart();
			if(heure%60<10) System.out.println(heure/60+"h0"+heure%60);
			else System.out.println(heure/60+"h"+heure%60);
			for(int k = 0; k<nbPistes; k++){
				System.out.print("Piste "+k+" "+Representation.TRACK_SEPARATION);
				if(k == triDepart.get(i).getIdPiste()){
					System.out.println("Depart avion "+triDepart.get(i).getIdAvion()+" ");
					switch(triDepart.get(i).getCapacite()){
					case(1) : System.out.println(Representation.DEPART_PETIT_AVION); break;
					case(2) : System.out.println(Representation.DEPART_MOYEN_AVION); break;
					case(3) : System.out.println(Representation.DEPART_GRAND_AVION); break;
					}
				}
				else{
					System.out.println();
					System.out.println(Representation.NO_PLANE);
				}
			}	
			System.out.println("========"+Representation.TRACK_SEPARATION);
			System.out.println();
			System.out.println();
			Thread.sleep(1000);
		}
	}


	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException{
		ParseurEntree parseur = new ParseurEntree(new File("test.csv"));
		OutPut out = new OutPut(parseur.getPlanes(),parseur.getNbPistes());
		out.joliOutput();
	}

}
