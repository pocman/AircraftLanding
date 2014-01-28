package display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ParseurEntree {
	
	private List<Avion> planes;
	private int nbPistes;
	
	public ParseurEntree(File fichier) throws NumberFormatException, IOException{
		planes = new ArrayList<Avion>();
		this.nbPistes = 0;
		BufferedReader fluxFichier = new BufferedReader(new InputStreamReader(
				new FileInputStream(fichier)));
		List<Integer> idPlanes = new ArrayList<Integer>();
		List<Integer> idTracks = new ArrayList<Integer>();
		List<Integer> arrivalTimes = new ArrayList<Integer>(); 
		List<Integer> departureTimes = new ArrayList<Integer>();
		List<Integer> capacities = new ArrayList<Integer>();
		String ligne = fluxFichier.readLine();
		while(!(ligne = fluxFichier.readLine()).startsWith("idPlane"));
		while((ligne = fluxFichier.readLine()) != null){
			String[] proprietesAvion = ligne.split("; ");
			idPlanes.add(Integer.parseInt(proprietesAvion[0]));
			idTracks.add(Integer.parseInt(proprietesAvion[1]));
			arrivalTimes.add(Integer.parseInt(proprietesAvion[2]));
			departureTimes.add(Integer.parseInt(proprietesAvion[3]));
			capacities.add(Integer.parseInt(proprietesAvion[5]));
			if(Integer.parseInt(proprietesAvion[1])+1>nbPistes) nbPistes = Integer.parseInt(proprietesAvion[1])+1;
		}
		for(int i=0; i<idPlanes.size(); i++){
			planes.add(new Avion(idPlanes.get(i), idTracks.get(i), arrivalTimes.get(i), departureTimes.get(i), capacities.get(i)));
		}
		fluxFichier.close();
	}

	public List<Avion> getPlanes() {
		return planes;
	}

	public int getNbPistes() {
		return nbPistes;
	}


}
