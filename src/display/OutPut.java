package display;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List; 

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
				Thread.sleep(100);
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
			Thread.sleep(100);
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
				if(heure%60<10) lignes[0] += heure/60+"h0"+heure%60+separation;
				else lignes[0] += heure/60+"h"+heure%60+separation;
				for(int k = 0; k<pistes.size(); k++){
					lignes[indexLigne] += "///////////"+separation;
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
						lignes[indexLigne + capacite] += "   "+separation;
						capacite++;
					}
					indexLigne+=pistes.get(k).getCapacite();
				}
				lignes[indexLigne] += "///////////"+separation;
				j++;
			}
			indexLigne = 1;
			heure = triDepart.get(i).getHeureDepart();
			if((int)(heure%60)<10) lignes[0] += heure/60+"h0"+heure%60+separation;
			else lignes[0] += heure/60+"h"+heure%60+separation;
			for(int k = 0; k<pistes.size(); k++){
				lignes[indexLigne] += "///////////"+separation;
				indexLigne++;
				int capacite = 0; 

				if(k == triDepart.get(i).getIdPiste()){
					switch(triDepart.get(i).getCapacite()){
					case(1) : lignes[indexLigne+capacite] += triDepart.get(i).getIdAvion()+" taking off"+separation; capacite++; break;
					case(2) : lignes[indexLigne+capacite] += triDepart.get(i).getIdAvion()+" taking off"+separation; capacite++;
					lignes[indexLigne+capacite] += triDepart.get(i).getIdAvion()+" taking off"+separation; capacite++; break;
					case(3) : lignes[indexLigne+capacite] += triDepart.get(i).getIdAvion()+" taking off"+separation; capacite++;
					lignes[indexLigne+capacite] += triDepart.get(i).getIdAvion()+" taking off"+separation; capacite++;
					lignes[indexLigne+capacite] += triDepart.get(i).getIdAvion()+" taking off"+separation; capacite++; break;
					}
					pistes.get(k).getPresents().remove(triDepart.get(i));
				}
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
				while(capacite<pistes.get(k).getCapacite()){
					lignes[indexLigne + capacite] += "   "+separation;
					capacite++;
				}
				indexLigne+=pistes.get(k).getCapacite();
			}
			lignes[indexLigne] += "///////////"+separation;

		}

		String excelFileName = "aeroport.xlsx";//name of excel file

		String sheetName = "Airport";//name of sheet

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName) ;
		
		XSSFCellStyle landingStyle = wb.createCellStyle();
		XSSFCellStyle waitingingStyle = wb.createCellStyle();
		XSSFCellStyle takingOffStyle = wb.createCellStyle();
		
		Font landing = wb.createFont();
		landing.setColor(IndexedColors.GREEN.getIndex());
		landingStyle.setFont(landing);
		
		Font waiting = wb.createFont();
		waiting.setColor(IndexedColors.RED.getIndex());
		waitingingStyle.setFont(waiting);
		
		Font takingOff = wb.createFont();
		takingOff.setColor(IndexedColors.BLUE.getIndex());
		takingOffStyle.setFont(takingOff);

		for(int i = 0; i<lignes.length; i++){
			XSSFRow row = sheet.createRow(i);
			String[] cellules = lignes[i].split(separation);
			for(int k = 0; k<cellules.length; k++){
				String cellule = cellules[k];
				XSSFCell cell = row.createCell(k);
				cell.setCellValue(cellule);
				if(cellule.contains("waiting")){
					cell.setCellStyle(waitingingStyle);
				}
				else {
					if(cellule.contains("taking off")){
						cell.setCellStyle(takingOffStyle);
					}
					else {
						if(cellule.contains("landing")){
							cell.setCellStyle(landingStyle);
						}
					}
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(excelFileName);

		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();

	}

	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException{
		ParseurEntree parseur = new ParseurEntree(new File("test.csv"));
		OutPut out = new OutPut(parseur.getPlanes(),parseur.getPistes());
		out.outputCSV();
	}
}
