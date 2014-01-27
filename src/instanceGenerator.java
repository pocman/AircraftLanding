import java.util.Random;

public class InstanceGenerator {

	/**
	 * générer une instance Si fenetreFixe est a true alors on fixe pour chaque
	 * avion une fourchette pour décoller et atterir sinon on fixe juste une
	 * durée de stationnement
	 * 
	 * @param args
	 * 
	 */
	public enum TAILLE_AEROPORT {
		PETIT, MOYEN, GRAND;
	}

	public static AircraftLanding generator(TAILLE_AEROPORT tailleAeroport,
			int alea, boolean fenetreFixe, boolean multiCumulative) {

		String[] schedule;
		int[] capacity;
		Random r = new Random(alea);
		// form
		if (fenetreFixe) {
			switch (tailleAeroport) {

			case PETIT:
				int nbTracksSmall = 1 + r.nextInt(2);
				capacity = new int[nbTracksSmall];
				for (int i = 0; i < nbTracksSmall; i++) {
					capacity[i] = 3 + r.nextInt(3);
				}
				//int nbPlanesSmall = 10 + r.nextInt(10);
				int nbPlanesSmall = 20 + r.nextInt(10);

				schedule = new String[nbPlanesSmall];
				int random = r.nextInt(5);
				int nbType2 = 0;
				for (int i = 0; i < nbPlanesSmall; i++) {
					int fourchette = r.nextInt(40); // on prevoit une fenetre de
													// 40 min max
					int type;
					if (nbType2 < 2 * nbPlanesSmall / 5) {
						type = 1 + r.nextInt(2);
					} else {
						type = 1;
					}
					schedule[i] = "";
					if (i < nbPlanesSmall / 15) {
						schedule[i] += "6:8:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 2 * nbPlanesSmall / 15) {
						schedule[i] += "8:10:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 4 * nbPlanesSmall / 15) {
						schedule[i] += "10:12:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 7 * nbPlanesSmall / 15) {
						schedule[i] += "12:14:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 10 * nbPlanesSmall / 15) {
						schedule[i] += "14:16:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 12 * nbPlanesSmall / 15) {
						schedule[i] += "16:18:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 13 * nbPlanesSmall / 15) {
						schedule[i] += "18:20:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 14 * nbPlanesSmall / 15) {
						schedule[i] += "20:22:";
						schedule[i] += String.valueOf(fourchette);
					} else {
						schedule[i] += "22:24:";
						schedule[i] += String.valueOf(fourchette);
					}
					schedule[i] += ":";
					schedule[i] += String.valueOf(type);
					if (type == 2) {
						nbType2++;
					}
				}
				break;

			case MOYEN:
				int nbTracksAverage = 4 + r.nextInt(3);
				capacity = new int[nbTracksAverage];
				for (int i = 0; i < nbTracksAverage; i++) {
					capacity[i] = 5 + r.nextInt(4);
				}
				//int nbPlanesAverage = 80 + r.nextInt(50);
				int nbPlanesAverage = 120; // + r.nextInt(50);

				schedule = new String[nbPlanesAverage];
				int random2 = r.nextInt(24);
				int random3 = r.nextInt(14);
				int nbType3 = 0;
				for (int i = 0; i < nbPlanesAverage; i++) {
					int fourchette = r.nextInt(40); // on prevoit une fenetre de
													// 40 min max
					int type;
					if (nbType3 < nbPlanesAverage / 2) {
						type = 1 + r.nextInt(3);
					} else {
						type = 1 + r.nextInt(2);
					}
					schedule[i] = "";
					if (i < nbPlanesAverage / 15) {
						schedule[i] += "6:8:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 2 * nbPlanesAverage / 15) {
						schedule[i] += "8:10:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 4 * nbPlanesAverage / 15) {
						schedule[i] += "10:12:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 7 * nbPlanesAverage / 15) {
						schedule[i] += "12:14:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 10 * nbPlanesAverage / 15) {
						schedule[i] += "14:16:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 12 * nbPlanesAverage / 15) {
						schedule[i] += "16:18:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 13 * nbPlanesAverage / 15) {
						schedule[i] += "18:20:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 14 * nbPlanesAverage / 15) {
						schedule[i] += "20:22:";
						schedule[i] += String.valueOf(fourchette);
					} else {
						schedule[i] += "22:24:";
						schedule[i] += String.valueOf(fourchette);
					}
					schedule[i] += ":";
					schedule[i] += String.valueOf(type);
					if (type == 3) {
						nbType3++;
					}
				}
				break;

			case GRAND:
				//int nbTracksLarge = 5 + r.nextInt(3);
				int nbTracksLarge = 6 + r.nextInt(3);
				capacity = new int[nbTracksLarge];
				for (int i = 0; i < nbTracksLarge; i++) {
					capacity[i] = 5 + r.nextInt(5);
				}
				//int nbPlanesLarge = 300 + r.nextInt(100);
				int nbPlanesLarge = 150;

				schedule = new String[nbPlanesLarge];
				int random4 = r.nextInt(80);
				int nbType3bis = 0;
				for (int i = 0; i < nbPlanesLarge; i++) {
					int fourchette = r.nextInt(40); // on prevoit une fenetre de
													// 40 min max
					int type;
					if (nbType3bis < 2 * nbPlanesLarge / 3) {
						type = 1 + r.nextInt(3);
					} else {
						type = 1 + r.nextInt(2);
					}
					schedule[i] = "";
					if (i < nbPlanesLarge / 15) {
						schedule[i] += "6:8:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 2 * nbPlanesLarge / 15) {
						schedule[i] += "8:10:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 4 * nbPlanesLarge / 15) {
						schedule[i] += "10:12:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 7 * nbPlanesLarge / 15) {
						schedule[i] += "12:14:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 10 * nbPlanesLarge / 15) {
						schedule[i] += "14:16:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 12 * nbPlanesLarge / 15) {
						schedule[i] += "16:18:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 13 * nbPlanesLarge / 15) {
						schedule[i] += "18:20:";
						schedule[i] += String.valueOf(fourchette);
					} else if (i < 14 * nbPlanesLarge / 15) {
						schedule[i] += "20:22:";
						schedule[i] += String.valueOf(fourchette);
					} else {
						schedule[i] += "22:24:";
						schedule[i] += String.valueOf(fourchette);
					}
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
					String timeWindow = String.valueOf(30 + r.nextInt(240)); // max
																				// landing
																				// duration
					if (i < 2 * nbPlanesSmall / 3 - 2 + random) {
						schedule[i] = timeWindow + ":1";
					} else {
						schedule[i] = timeWindow + ":2";
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
					String timeWindow = String.valueOf(30 + r.nextInt(240)); // max
																				// landing
																				// duration
					if (i < nbPlanesAverage / 2 - 10 + random2) {
						schedule[i] = timeWindow + ":1";
					} else if (i < 3 * nbPlanesAverage / 4 - 5 + random3) {
						schedule[i] = timeWindow + ":2";
					} else {
						schedule[i] = timeWindow + ":3";
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
					String timeWindow = String.valueOf(30 + r.nextInt(240)); // max
																				// landing
																				// duration
					if (i < nbPlanesLarge / 3 - 50 + random4) {
						schedule[i] = timeWindow + ":1";
					} else if (i < 2 * nbPlanesLarge / 3 - 50 + random4) {
						schedule[i] = timeWindow + ":2";
					} else {
						schedule[i] = timeWindow + ":3";
					}
				}
				break;

			default:
				capacity = new int[0];
				schedule = new String[0];
				break;
			}
		}
		return new AircraftLanding(schedule, capacity, fenetreFixe, multiCumulative);
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
			System.out.println("Capacité : " + capacity[i]);
			System.out.println("");
		}
	}
}
