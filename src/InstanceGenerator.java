import java.util.Arrays;
import java.util.Random;

import solver.Solver;
import util.ESat;

public class InstanceGenerator {

	/**
	 * generer une instance Si fenetreFixe est a true alors on fixe pour chaque
	 * avion une fourchette pour d�coller et atterir sinon on fixe juste une
	 * dur�e de stationnement
	 * 
	 * @param args
	 * 
	 */
	public enum TAILLE_AEROPORT {
		PETIT, MOYEN, GRAND, DEFAULT;
	}

	public static AircraftLanding generator(TAILLE_AEROPORT tailleAeroport,
			int alea, boolean fenetreFixe, boolean multiCumulative) {

		String[] schedule;
		int[] capacity;
		Random r = new Random(alea);
		// form
		if (fenetreFixe) {
			switch (tailleAeroport) {

			//bonne petite instance 
			//int nbTracksSmall = 4 + r.nextInt(2);
			//int nbPlanesSmall = 55 + r.nextInt(10);
			//seed 21
			case PETIT:
				int nbTracksSmall = 4 + r.nextInt(2);
				capacity = new int[nbTracksSmall];
				for (int i = 0; i < nbTracksSmall; i++) {
					capacity[i] = 3 + r.nextInt(3);
				}

				//int nbPlanesSmall = 35 + r.nextInt(10); really really long
				int nbPlanesSmall = 70 + r.nextInt(10);//cool size 55

				schedule = new String[nbPlanesSmall];
				int random = r.nextInt(5);
				int nbType2 = 0;
				for (int i = 0; i < nbPlanesSmall; i++) {
					int minDuration = 30+r.nextInt(30); // duree de stationnement mini entre 30m et 1h
					int maxDuration = minDuration+r.nextInt(35)+5;
					int type;
					if (nbType2 < 2 * nbPlanesSmall / 5) {
						type = 1 + r.nextInt(2);
					} else {
						type = 1;
					}
					schedule[i] = "";
					if (i < nbPlanesSmall / 30) {
						schedule[i] += "6:8:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < nbPlanesSmall / 15) {
						schedule[i] += "7:9:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 2 * nbPlanesSmall / 15) {
						schedule[i] += "8:10:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 3 * nbPlanesSmall / 15) {
						schedule[i] += "9:11:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 4 * nbPlanesSmall / 15) {
						schedule[i] += "10:12:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 5 * nbPlanesSmall / 15) {
						schedule[i] += "11:13:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 7 * nbPlanesSmall / 15) {
						schedule[i] += "12:14:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 8 * nbPlanesSmall / 15) {
						schedule[i] += "13:15:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 10 * nbPlanesSmall / 15) {
						schedule[i] += "14:16:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 11 * nbPlanesSmall / 15) {
						schedule[i] += "15:17:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 12 * nbPlanesSmall / 15) {
						schedule[i] += "16:18:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 25 * nbPlanesSmall / 30) {
						schedule[i] += "17:19:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 13 * nbPlanesSmall / 15) {
						schedule[i] += "18:20:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 27 * nbPlanesSmall / 30) {
						schedule[i] += "19:21:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 14 * nbPlanesSmall / 15) {
						schedule[i] += "20:22:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 29 * nbPlanesSmall / 15) {
						schedule[i] += "21:23:";
						schedule[i] += String.valueOf(minDuration);
					} else {
						schedule[i] += "22:24:";
						schedule[i] += String.valueOf(minDuration);
					}
					schedule[i] += ":"+String.valueOf(maxDuration);
					schedule[i] += ":";
					schedule[i] += String.valueOf(type);
					if (type == 2) {
						nbType2++;
					}
				}
				break;

			case MOYEN:
				int nbTracksAverage = 10 + r.nextInt(3);
				capacity = new int[nbTracksAverage];
				for (int i = 0; i < nbTracksAverage; i++) {
					capacity[i] = 3 + r.nextInt(4);
				}

				int nbPlanesAverage = 120 + r.nextInt(20);


				schedule = new String[nbPlanesAverage];
				int random2 = r.nextInt(24);
				int random3 = r.nextInt(14);
				int nbType3 = 0;
				for (int i = 0; i < nbPlanesAverage; i++) {
					int minDuration = 30+r.nextInt(30); 
					int maxDuration = minDuration+r.nextInt(45)+5;
					int type;
					if (nbType3 < nbPlanesAverage / 2) {
						type = 1 + r.nextInt(3);
					} else {
						type = 1 + r.nextInt(2);
					}
					schedule[i] = "";
					if (i < nbPlanesAverage / 30) {
						schedule[i] += "6:8:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < nbPlanesAverage / 15) {
						schedule[i] += "7:9:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 2 * nbPlanesAverage / 15) {
						schedule[i] += "8:10:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 3 * nbPlanesAverage / 15) {
						schedule[i] += "9:11:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 4 * nbPlanesAverage / 15) {
						schedule[i] += "10:12:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 5 * nbPlanesAverage / 15) {
						schedule[i] += "11:13:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 7 * nbPlanesAverage / 15) {
						schedule[i] += "12:14:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 8 * nbPlanesAverage / 15) {
						schedule[i] += "13:15:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 10 * nbPlanesAverage / 15) {
						schedule[i] += "14:16:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 11 * nbPlanesAverage / 15) {
						schedule[i] += "15:17:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 12 * nbPlanesAverage / 15) {
						schedule[i] += "16:18:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 25 * nbPlanesAverage / 30) {
						schedule[i] += "17:19:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 13 * nbPlanesAverage / 15) {
						schedule[i] += "18:20:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 27 * nbPlanesAverage / 30) {
						schedule[i] += "19:21:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 14 * nbPlanesAverage / 15) {
						schedule[i] += "20:22:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 29 * nbPlanesAverage / 15) {
						schedule[i] += "21:23:";
						schedule[i] += String.valueOf(minDuration);
					} else {
						schedule[i] += "22:24:";
						schedule[i] += String.valueOf(minDuration);
					}
					schedule[i] += ":"+String.valueOf(maxDuration);
					schedule[i] += ":";
					schedule[i] += String.valueOf(type);
					if (type == 3) {
						nbType3++;
					}
				}
				break;

			case GRAND:

				int nbTracksLarge = 10 + r.nextInt(20);

				capacity = new int[nbTracksLarge];
				for (int i = 0; i < nbTracksLarge; i++) {
					capacity[i] = 40 + r.nextInt(5);
				}
				int nbPlanesLarge = 400;

				schedule = new String[nbPlanesLarge];
				int random4 = r.nextInt(80);
				int nbType3bis = 0;
				for (int i = 0; i < nbPlanesLarge; i++) {
					int minDuration = 30+r.nextInt(30); 
					int maxDuration = minDuration+r.nextInt(45)+5;					
					int type;
					if (nbType3bis < 2 * nbPlanesLarge / 3) {
						type = 1 + r.nextInt(3);
					} else {
						type = 1 + r.nextInt(2);
					}
					schedule[i] = "";
					if (i < nbPlanesLarge / 30) {
						schedule[i] += "6:8:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < nbPlanesLarge / 15) {
						schedule[i] += "7:9:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 2 * nbPlanesLarge / 15) {
						schedule[i] += "8:10:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 3 * nbPlanesLarge / 15) {
						schedule[i] += "9:11:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 4 * nbPlanesLarge / 15) {
						schedule[i] += "10:12:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 5 * nbPlanesLarge / 15) {
						schedule[i] += "11:13:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 7 * nbPlanesLarge / 15) {
						schedule[i] += "12:14:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 8 * nbPlanesLarge / 15) {
						schedule[i] += "13:15:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 10 * nbPlanesLarge / 15) {
						schedule[i] += "14:16:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 11 * nbPlanesLarge / 15) {
						schedule[i] += "15:17:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 12 * nbPlanesLarge / 15) {
						schedule[i] += "16:18:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 25 * nbPlanesLarge / 30) {
						schedule[i] += "17:19:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 13 * nbPlanesLarge / 15) {
						schedule[i] += "18:20:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 27 * nbPlanesLarge / 30) {
						schedule[i] += "19:21:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 14 * nbPlanesLarge / 15) {
						schedule[i] += "20:22:";
						schedule[i] += String.valueOf(minDuration);
					} else if (i < 29 * nbPlanesLarge / 15) {
						schedule[i] += "21:23:";
						schedule[i] += String.valueOf(minDuration);
					} else {
						schedule[i] += "22:24:";
						schedule[i] += String.valueOf(minDuration);
					}
					schedule[i] += ":"+String.valueOf(maxDuration);
					schedule[i] += ":";
					schedule[i] += String.valueOf(type);
					if (type == 3) {
						nbType3bis++;
					}
				}
				break;

			default:
				capacity = new int[0];
				schedule = new String[0];
				break;
			}
		}else{
			switch (tailleAeroport) {
			case PETIT:
				int nbTracksSmall = 1 + r.nextInt(2);
				capacity = new int[nbTracksSmall];
				for (int i = 0; i < nbTracksSmall; i++) {
					capacity[i] = 3 + r.nextInt(3);
				}
				int nbPlanesSmall = 20 + r.nextInt(10);

				schedule = new String[nbPlanesSmall];
				int random = r.nextInt(5);
				for (int i = 0; i < nbPlanesSmall; i++) {
					int minDuration=30 + r.nextInt(90);
					String startWindow = String.valueOf(minDuration);
					String endWindow = String.valueOf(minDuration+r.nextInt(150));															// landing
																			
					if (i < 2 * nbPlanesSmall / 3 - 2 + random) {
						schedule[i] = startWindow + ":" + endWindow + ":1";
					} else {
						schedule[i] = startWindow + ":"+ endWindow + ":2";
					}
				}
				break;

			case MOYEN:
				int nbTracksAverage = 3 + r.nextInt(3);
				capacity = new int[nbTracksAverage];
				for (int i = 0; i < nbTracksAverage; i++) {
					capacity[i] = 3 + r.nextInt(4);
				}
				int nbPlanesAverage = 30 + r.nextInt(20);

				schedule = new String[nbPlanesAverage];
				int random2 = r.nextInt(24);
				int random3 = r.nextInt(14);
				for (int i = 0; i < nbPlanesAverage; i++) {
					int minDuration=30 + r.nextInt(90);
					String startWindow = String.valueOf(minDuration);
					String endWindow = String.valueOf(minDuration+r.nextInt(150));
					
					if (i < nbPlanesAverage / 2 - 10 + random2) {
						schedule[i] = startWindow + ":"+ endWindow + ":1";
					} else if (i < 3 * nbPlanesAverage / 4 - 5 + random3) {
						schedule[i] = startWindow + ":"+ endWindow + ":2";
					} else {
						schedule[i] = startWindow + ":"+ endWindow + ":3";
					}
				}
				break;

			case GRAND:
				int nbTracksLarge = 5 + r.nextInt(4);
				capacity = new int[nbTracksLarge];
				for (int i = 0; i < nbTracksLarge; i++) {
					capacity[i] = 5 + r.nextInt(5);
				}
				int nbPlanesLarge = 300 + r.nextInt(300);

				schedule = new String[nbPlanesLarge];
				int random4 = r.nextInt(80);
				for (int i = 0; i < nbPlanesLarge; i++) {
					int minDuration=30 + r.nextInt(90);
					String startWindow = String.valueOf(minDuration);
					String endWindow = String.valueOf(minDuration+r.nextInt(150));
					
					if (i < nbPlanesLarge / 3 - 50 + random4) {
						schedule[i] = startWindow + ":" + endWindow + ":1";
					} else if (i < 2 * nbPlanesLarge / 3 - 50 + random4) {
						schedule[i] = startWindow + ":"+ endWindow + ":2";
					} else {
						schedule[i] = startWindow + ":"+ endWindow + ":3";
					}
				}
				break;

			default:
				capacity = new int[0];
				schedule = new String[0];
				break;
			}
		}
		
		
		int[] dummyCapacity = new int[]{0};
		for(int capa : capacity){
			dummyCapacity[0] += capa;
		}
		AircraftLanding alDummy = new AircraftLanding(schedule, dummyCapacity, true, false);
		Solver sDummy = new Solver("aircraftLanding_dummy");
		alDummy.model(sDummy, false);
		alDummy.chooseStrategy();
		alDummy.solve(35000);
		alDummy.prettyOutput();
		
		
		if(alDummy.getSolver().isFeasible() == ESat.TRUE)			
			return new AircraftLanding(schedule, capacity, fenetreFixe, multiCumulative);
		else{
			System.out.println("pas de solution");
			return null;
		}
	}

	public static AircraftLanding defaultGenerator(boolean multiCumulative) {
		Random r=new Random(39);
		int[] capacity=new int[]{6,5,3,2,7,6,4,1,1};
		int nbPlanes=124;
		String[] schedule=new String[nbPlanes];
		for(int i=0; i<nbPlanes; i++) {
			if (i < nbPlanes / 30) {
				schedule[i] = "6:8:30:45";
			} else if (i < nbPlanes / 15) {
				schedule[i] = "7:9:36:46";
			} else if (i < 2 * nbPlanes / 15) {
				schedule[i] = "8:10:34:56";
			} else if (i < 3 * nbPlanes / 15) {
				schedule[i] = "9:11:39:50";
			} else if (i < 4 * nbPlanes / 15) {
				schedule[i] = "10:12:42:53";
			} else if (i < 5 * nbPlanes / 15) {
				schedule[i] = "11:13:40:49";
			} else if (i < 7 * nbPlanes / 15) {
				schedule[i] = "12:14:32:61";
			} else if (i < 8 * nbPlanes / 15) {
				schedule[i] = "13:15:45:72";
			} else if (i < 10 * nbPlanes / 15) {
				schedule[i] = "14:16:41:74";
			} else if (i < 11 * nbPlanes / 15) {
				schedule[i] = "15:17:32:60";
			} else if (i < 12 * nbPlanes / 15) {
				schedule[i] = "16:18:40:54";
			} else if (i < 25 * nbPlanes / 30) {
				schedule[i] = "17:19:38:47";
			} else if (i < 13 * nbPlanes / 15) {
				schedule[i] = "18:20:35:46";
			} else if (i < 27 * nbPlanes / 30) {
				schedule[i] = "19:21:34:59";
			} else if (i < 14 * nbPlanes / 15) {
				schedule[i] = "20:22:47:68";
			} else if (i < 29 * nbPlanes / 30) {
				schedule[i] = "21:23:30:41";
			} else {
				schedule[i] = "22:24:42:60";
			}
		}
		for(int i=0; i<nbPlanes; i++) {
			int type=1+r.nextInt(3);
			schedule[i]+=":"+String.valueOf(type);
		}
		return new AircraftLanding(schedule, capacity, true, multiCumulative);
	}
	
	public static void main(String[] args) {
		AircraftLanding instance = generator(TAILLE_AEROPORT.MOYEN, 12, true, true);
		String[] schedule = instance.getSchedule();
		System.out
				.println("Instance pour un aeroport de taille moyenne et fenetre de temps fixees");
		System.out.println("");
		for (int i = 0; i < schedule.length; i++) {
			System.out.println("Avion " + i);
			System.out.println(schedule[i]);
			System.out.println("");
		}
		int[] capacity = instance.getCapacity();
		for (int i = 0; i < capacity.length; i++) {
			System.out.println("Piste " + i);
			System.out.println("Capacite : " + capacity[i]);
			System.out.println("");
		}
	}
}
