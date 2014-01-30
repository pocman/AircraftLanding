package display;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List; 

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.ScriptStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class OutPut {

	private List<Avion> triDepart;
	private List<Avion> triArrivee;
	private List<Piste> pistes;

	public OutPut(List<Avion> planes, List<Piste> pistes){
		Collections.sort(planes, new TriAvionDepart());
		triDepart = new ArrayList<Avion>();
		triDepart.addAll(planes);

		Collections.sort(planes, new TriAvionsArrivee());
		triArrivee = new ArrayList<Avion>();
		triArrivee.addAll(planes);
		this.pistes = pistes;
	}

	public void joliOutput() throws InterruptedException{
		int j = 0;
		int heure;
		for(int i= 0; i<triDepart.size(); i++){
			while(j < triArrivee.size() && triDepart.get(i).getHeureDepart()>triArrivee.get(j).getHeureArrivee()){
				heure = triArrivee.get(j).getHeureArrivee();
				if(heure%60<10) System.out.println(heure/60+"h0"+heure%60);
				else System.out.println(heure/60+"h"+heure%60);
				for(int k = 0; k<pistes.size(); k++){
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
			for(int k = 0; k<pistes.size(); k++){
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

	public void outputCSV() throws IOException{

		String separation = "; ";
		int nbLignes = 2;
		for(int i=0;i<pistes.size(); i++){
			nbLignes = nbLignes+1+pistes.get(i).getCapacite();
		}
		String[] lignes = new String[nbLignes];
		for(int i = 0; i<lignes.length;i++){
			lignes[i] = "";
		}
		int heure;
		int j = 0;
		int indexLigne = 1;
		for(int i= 0; i<triDepart.size(); i++){
			while(j < triArrivee.size() && triDepart.get(i).getHeureDepart()>triArrivee.get(j).getHeureArrivee()){
				indexLigne = 1;
				heure = triArrivee.get(j).getHeureArrivee();
				int capacite = 0;
				if((heure%60)<10) lignes[0] += heure/60+"h0"+heure%60+separation;
				else lignes[0] += heure/60+"h"+heure%60+separation;
				for(int k = 0; k<pistes.size(); k++){
					lignes[indexLigne] += "/////////////"+separation;
					indexLigne++;
					capacite = 0; 
					for(Avion avion : pistes.get(k).getPresents()){
						switch(avion.getCapacite()){
						case(1) : lignes[indexLigne+capacite] += avion.getIdAvion()+" waiting"+separation; capacite++; break;
						case(2) : lignes[indexLigne+capacite] += avion.getIdAvion()+" waiting"+separation; capacite++; 
						lignes[indexLigne+capacite] += avion.getIdAvion()+" waiting"+separation; capacite++;  break;
						case(3) : lignes[indexLigne+capacite] += avion.getIdAvion()+" waiting"+separation; capacite++; 
						lignes[indexLigne+ capacite] += avion.getIdAvion()+" waiting"+separation; capacite++; 
						lignes[indexLigne+ capacite] += avion.getIdAvion()+" waiting"+separation; capacite++;  break;
						}
					}
					if(k == triArrivee.get(j).getIdPiste()){
						switch(triArrivee.get(j).getCapacite()){
						case(1) : lignes[indexLigne+capacite] += triArrivee.get(j).getIdAvion()+" landing"+separation; capacite++; break;
						case(2) : lignes[indexLigne+capacite] += triArrivee.get(j).getIdAvion()+" landing"+separation; capacite++; 
						lignes[indexLigne+capacite] += triArrivee.get(j).getIdAvion()+" landing"+separation; capacite++;  break;
						case(3) : lignes[indexLigne+capacite] += triArrivee.get(j).getIdAvion()+" landing"+separation; capacite++; 
						lignes[indexLigne+capacite] += triArrivee.get(j).getIdAvion()+" landing"+separation; capacite++; 
						lignes[indexLigne+capacite] += triArrivee.get(j).getIdAvion()+" landing"+separation; capacite++;  break;
						}
						pistes.get(k).getPresents().add(triArrivee.get(j));
					}
					while(capacite<pistes.get(k).getCapacite()){
						lignes[indexLigne + capacite] += ""+separation;
						capacite++;
					}
					indexLigne+=pistes.get(k).getCapacite();
				}
				lignes[indexLigne] += "/////////////"+separation;
				j++;
			}
			indexLigne = 1;
			heure = triDepart.get(i).getHeureDepart();
			if((int)(heure%60)<10) lignes[0] += heure/60+"h0"+heure%60+separation;
			else lignes[0] += heure/60+"h"+heure%60+separation;
			for(int k = 0; k<pistes.size(); k++){
				lignes[indexLigne] += "/////////////"+separation;
				indexLigne++;
				if(k == triDepart.get(i).getIdPiste()){
					switch(triDepart.get(i).getCapacite()){
					case(1) : lignes[indexLigne] += triDepart.get(i).getIdAvion()+" taking off"+separation; break;
					case(2) : lignes[indexLigne] += triDepart.get(i).getIdAvion()+" taking off"+separation;
					lignes[indexLigne+1] += triDepart.get(i).getIdAvion()+" taking off"+separation; break;
					case(3) : lignes[indexLigne] += triDepart.get(i).getIdAvion()+" taking off"+separation;
					lignes[indexLigne+1] += triDepart.get(i).getIdAvion()+" taking off"+separation;
					lignes[indexLigne+2] += triDepart.get(i).getIdAvion()+" taking off"+separation; break;
					}
					pistes.get(k).getPresents().remove(triDepart.get(i));
				}
				indexLigne+=pistes.get(k).getCapacite();
			}
			lignes[indexLigne] += "/////////////"+separation;

		}
		try { 
			WritableWorkbook workbook = Workbook.createWorkbook(new File("aeroport.xls")); 
			WritableSheet sheet = workbook.createSheet("Premier classeur", 0); 
			WritableFont landing = new WritableFont(WritableFont.ARIAL, 10,WritableFont.NO_BOLD, true, UnderlineStyle.NO_UNDERLINE,Colour.GREEN, ScriptStyle.NORMAL_SCRIPT); 
			WritableFont waiting = new WritableFont(WritableFont.ARIAL, 10,WritableFont.NO_BOLD, true, UnderlineStyle.NO_UNDERLINE,Colour.RED, ScriptStyle.NORMAL_SCRIPT); 
			WritableFont takingOff = new WritableFont(WritableFont.ARIAL, 10,WritableFont.NO_BOLD, true, UnderlineStyle.NO_UNDERLINE,Colour.BLUE, ScriptStyle.NORMAL_SCRIPT); 

			WritableCellFormat landingFormat = new WritableCellFormat(landing); 
			WritableCellFormat waitingFormat = new WritableCellFormat(waiting); 
			WritableCellFormat takingOffFormat = new WritableCellFormat(takingOff); 

			for(int i = 0; i<lignes.length; i++){
				String[] cellules = lignes[i].split(separation);
				for(int k = 0; k<cellules.length; k++){
					String cellule = cellules[k];
					Label  label; 
					if(cellule.contains("landing"))  label = new Label(k, i, cellule, landingFormat);
					else if(cellule.contains("waiting")) label = new Label(k, i, cellule, waitingFormat);
					else if(cellule.contains("taking off"))label = new Label(k, i, cellule, takingOffFormat);
					else label = new Label(k,i,cellule);
					sheet.addCell(label); 
				}
			}
			workbook.write(); 
			workbook.close(); 
		} catch (RowsExceededException e1) { 
			e1.printStackTrace(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} catch (WriteException e) {
			e.printStackTrace();
		}finally{ 
			System.out.println("output cree"); 
		}
	}


	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException{
		ParseurEntree parseur = new ParseurEntree(new File("test.csv"));
		OutPut out = new OutPut(parseur.getPlanes(),parseur.getPistes());
		out.outputCSV();
	}

}
