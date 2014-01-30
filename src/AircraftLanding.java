import solver.ResolutionPolicy;
import solver.Solver;
import solver.constraints.Constraint;
import solver.constraints.ICF;
import solver.constraints.IntConstraintFactory;
import solver.constraints.LogicalConstraintFactory;
import solver.search.loop.monitors.SMF;
import solver.search.strategy.IntStrategyFactory;
import solver.search.strategy.strategy.StrategiesSequencer;
import solver.variables.*;
import util.tools.ArrayUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import display.Representation;


public class AircraftLanding {

	Random r = new Random(42);
	final static int MinutesVersPasTemps = 1;
	/*
	 * L'instance du solver
	 */
	Solver s;

	/*
	 * Une tache contient la date d'arrivï¿½e, la date de dï¿½part 
	 * et la durï¿½e de stationnement de l'avion sur la piste
	 */
	Task[] taskPlanes;
	IntVar[] duration, landing, takeOff; //for each plane
	
	/*
	 * Une matrice 0/1 de taille nTracks * nPlanes
	 * qui donne la position des avions sur les pistes
	 */
	IntVar[][] tracks;
	
	/*
	 * Les ressources occupï¿½es par chaque avion sur chaque piste
	 * Matrice de taille nPlanes * nTracks
	 */
	IntVar[][] heightInCumulatives;
	
	/*
	 * Le nombre d'avions par piste
	 */
	IntVar[] sumByTracks;
	
	/*
	 * Le numï¿½ro de la piste sur laquelle est chaque avion
	 */
	IntVar[] tracksByPlane;
	
	/*
	 * Si l'avion viole une contrainte de prï¿½cï¿½dence ou non
	 */
	IntVar[] brokenConstraint;
	
	/*
	 * Somme des avions qui violent une contrainte de prï¿½cï¿½dence
	 */
	IntVar minBreak;
	
	/*
	 * Variable reprï¿½sentant la capacitï¿½ d'une piste
	 */
	IntVar[] vCapacities;
	
	/*
	 * Boolean spï¿½cifiant si on utilise plusieurs contriantes cumulatives 
	 * ou la contrainte cumulative multi d'Arnauld Letort
	 */
	boolean utiliseMultiCumulative;


	int[] setOfTypes = new int[]{1, 2, 3};//la valeur d'occupation pour chaque type d'avion
	int[] windowStart, minDuration, windowEnd, maxDuration; //valeur en entrï¿½e pour la fenetre de chaque avion
	int[] typePlane; //le type de chaque avion
	int[] capacity; //la capacitï¿½ de chaque piste
	int nPlanes; //le nombre d'avion dans l'instance
	int nTracks; //le nombre de piste dans l'instance
	String[] schedule; //les donnï¿½es en entrï¿½e sï¿½parï¿½es avec des ':'



	/**Le constructeur de notre solver
	 * 
	 * @param schedule Chaque avion est reprï¿½sentï¿½ par une string avec les durï¿½es des intervalles en minutes et son type d'avion espacï¿½ par des ':'
	 * @param capacity La capacitï¿½ de chaque piste
	 * @param fenetreFixe Si fourni les fenetres en entrï¿½e
	 * @param multiCumulative Si on utilise la contrainte cumulativeMultiple
	 */
	public AircraftLanding(String[] schedule, int[] capacity, boolean fenetreFixe, boolean multiCumulative) {
		this.schedule = schedule;
		this.utiliseMultiCumulative = multiCumulative;
		ArrayList<int[]> planes = new ArrayList<int[]>();

		if(!fenetreFixe) {
			//on parse notre entrée
			for(String s : schedule){
				String[] temp = s.split(":");
				int[] planesByTF = new int[]{Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2])};
				planes.add(planesByTF);
			}

			this.setnTracks(capacity.length);
			this.setCapacity(capacity);
			this.setnPlanes(planes.size());
			this.setTypePlane(new int[this.getnPlanes()]);

			this.windowStart = new int[this.getnPlanes()];
			this.minDuration = new int[this.getnPlanes()];
			this.maxDuration = new int[this.getnPlanes()];
			this.windowEnd = new int[this.getnPlanes()];


			for(int i = 0 ; i < this.getnPlanes(); i++){
				this.minDuration[i] = planes.get(i)[0];
				this.maxDuration[i] = planes.get(i)[1];
				this.typePlane[i] = planes.get(i)[2];
			}
		} else {
			for (String s : schedule) {
				String[] temp = s.split(":");
				int[] planesByTF = new int[]{Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Integer.parseInt(temp[4])};
				planes.add(planesByTF);
			}
			this.setnTracks(capacity.length);
			this.setCapacity(capacity);
			this.setnPlanes(planes.size());
			this.setTypePlane(new int[this.getnPlanes()]);

			this.windowStart = new int[this.getnPlanes()];
			this.minDuration = new int[this.getnPlanes()];
			this.maxDuration = new int[this.getnPlanes()];
			this.windowEnd = new int[this.getnPlanes()];

			for (int i = 0; i < this.getnPlanes(); i++) {
				this.windowStart[i] = planes.get(i)[0] * 60* AircraftLanding.MinutesVersPasTemps; //le creneau est en heures, on le tranforme en minutes
				this.windowEnd[i] = planes.get(i)[1] * 60* AircraftLanding.MinutesVersPasTemps; //le creneau est en heures, on le tranforme en minutes
				this.minDuration[i] = planes.get(i)[2]*AircraftLanding.MinutesVersPasTemps; //en minutes
				this.maxDuration[i] = planes.get(i)[3]*AircraftLanding.MinutesVersPasTemps; //en minutes
				this.typePlane[i] = planes.get(i)[4];
			}
		}
		//notre heuristique de tri des avions pour ensuite utiliser une stratégie input_order
		this.sortCapacity();
		this.sortPlanes();
	}

	/**Création des variables et appelle des heuristiques
	 * 
	 * @param s L'instance de notre solveur
	 * @param precedence On utilise une classe de précédence
	 */
	public void model(Solver s, Boolean precedence) {

		System.out.println();
		System.out.println("----------Model--------");
		
		this.s = s;
		taskPlanes = new Task[nPlanes];
		landing = new IntVar[nPlanes];
		duration = new IntVar[nPlanes];
		takeOff = new IntVar[nPlanes];
		tracksByPlane = new IntVar[nPlanes];

		for (int i = 0; i < nPlanes; i++) {
			//Variable d'attérissage tronquée du temps minimal sur tarmac
			landing[i] = VariableFactory.bounded("Landing " + i, windowStart[i], windowEnd[i]-minDuration[i], s);
			duration[i] = VariableFactory.bounded("Duration on airport " + i, minDuration[i], maxDuration[i], s);
			//Variable de décollage tronquée du temps minimal sur tarmac
			takeOff[i] = VariableFactory.bounded("Take off " + i, windowStart[i]+minDuration[i], windowEnd[i], s);
			//Un avion peut être sur toutes les pistes
			tracksByPlane[i] = VariableFactory.bounded("Tracks for plane " + i, 0, this.getnTracks() - 1, s);
			//creation des taches qui assurent le respect des contraintes 
			taskPlanes[i] = VariableFactory.task(landing[i], duration[i], takeOff[i]);
			System.out.println("type of plane " + i + " :" + typePlane[i]);
			System.out.println(this.landing[i]);
			System.out.println(this.duration[i]);
		}


		//for each plane which track, it's on
		tracks = VariableFactory.enumeratedMatrix("track of plane", nTracks, nPlanes, 0, 1, s);

		//Cassage de symetrie
		System.out.println("Utiliser un cassage de symetrie");
		this.symetrieBreaking(s);

		System.out.println("Unicitï¿½ de positionnement de l'avion sur une piste");
		//Un avion ne peut etre que sur une seule piste
		for (int plane = 0; plane < nPlanes; plane++) {
			s.post(ICF.count(1, ArrayUtils.getColumn(tracks, plane), VF.fixed(1, s)));
		}

		//Pour chaque avion, on a son numero de piste
		for (int i = 0; i < nTracks; i++) {
			for (int j = 0; j < nPlanes; j++) {
				s.post(LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(tracks[i][j], "=", 1), IntConstraintFactory.arithm(tracksByPlane[j], "=", i)));
			}
		}

		System.out.println("Contrainte sur l'utilisation de la piste");
		//On ne peut pas avoir d'avions qui decollent ou atterissent la meme minute.
		this.operationUniqueUniteTemps();
		


		//contrainte souple de precedence entre les avions
		this.contraintePrecedence(s, precedence);

		if (utiliseMultiCumulative) {
			System.out.println("Utilise la contrainte multidimensionnelle");
			this.multiCumulative(s);

		} else {
			System.out.println("N'utiliser PAS la contrainte multidimensionnelle");
			this.simpleCumulative(s);
		}

	}
	
	private void operationUniqueUniteTemps() {
		if(this.nPlanes < 220)
			s.post(IntConstraintFactory.alldifferent(ArrayUtils.append(this.landing, this.takeOff), "BC"));
		else
			System.out.println("Le pas de temps est trop gros, passer en Seconde et non Minutes");
		
	}

	public void solve() {
		SMF.log(s, true, false);
		//SMF.limitTime(s, 35000);
		//s.findSolution();
		s.findOptimalSolution(ResolutionPolicy.MINIMIZE, minBreak);
	}

	public void chooseStrategy() {
		s.set(new StrategiesSequencer(IntStrategyFactory.inputOrder_InDomainMin(new IntVar[]{minBreak}),
				IntStrategyFactory.inputOrder_InDomainMin(this.takeOff),
				IntStrategyFactory.inputOrder_InDomainMin(this.duration),
				IntStrategyFactory.inputOrder_InDomainMin(this.landing),				
				IntStrategyFactory.inputOrder_InDomainMin(this.tracksByPlane)
		));
	}

	public static void main(String[] args) {
		System.out.println(Representation.AIR_FORCE_ONE);
		System.out.println();
		System.out.println(Representation.START);
		System.out.println();
		System.out.println("Bienvenue dans le solveur de l'equipe Air Force One");
		System.out.println("A l'aide de cet outil, vous pourrez resoudre les problemes de gestion d'aeroport");
		System.out.println();
		Scanner sc = new Scanner(System.in);
		String reponse;
		do {
			System.out.println("Veuillez choisir la taille de l'aeroport (petit, moyen ou grand)");
			reponse = sc.next();
		}
		while (!(reponse.equals("petit") || reponse.equals("moyen") || reponse.equals("grand")));
		InstanceGenerator.TAILLE_AEROPORT taille;
		if (reponse.equals("petit")) taille = InstanceGenerator.TAILLE_AEROPORT.PETIT;
		else if (reponse.equals("moyen")) taille = InstanceGenerator.TAILLE_AEROPORT.MOYEN;
		else taille = InstanceGenerator.TAILLE_AEROPORT.GRAND;
		boolean isPositifNumber;
		do {
			System.out.println("Veuillez entrer un entier (Afin d'assurer l'unicite de l'aeroport)");
			reponse = sc.next();
			isPositifNumber = false;
			try {
				isPositifNumber = Integer.parseInt(reponse) >= 0;
			}
			catch (NumberFormatException e) {
				isPositifNumber = false;
			}			
		}
		while (!isPositifNumber);
		int alea = Integer.parseInt(reponse);
		do {
			System.out.println("Voulez-vous utiliser des fenetres de temps de stationnement fixes? (y/n)");
			reponse = sc.next();
		}
		while (!(reponse.equals("y") || reponse.equals("n")));
		boolean fenetresFixes;
		if (reponse.equals("y")) fenetresFixes = true;
		else fenetresFixes = false;
		do {
			System.out.println("Voulez-vous utiliser la contrainte multiCumulative? (y/n)");
			reponse = sc.next();
		}
		while (!(reponse.equals("y") || reponse.equals("n")));
		boolean multiCumulative;
		if (reponse.equals("y")) multiCumulative = true;
		else multiCumulative = false;
		
		do {
			System.out.println("Voulez-vous utiliser la contrainte de prï¿½cï¿½dence? (y/n)");
			reponse = sc.next();
		}
		while (!(reponse.equals("y") || reponse.equals("n")));
		boolean precedence;
		if (reponse.equals("y")) precedence = true;
		else precedence = false;


		AircraftLanding al = InstanceGenerator.generator(taille, alea, fenetresFixes, multiCumulative);
		Solver s = new Solver("aircraftLanding");
		if(al == null) {
			System.out.println("No solution found !");
			System.exit(0);
		}
		al.model(s, precedence);
		al.chooseStrategy();
		al.solve();
		al.prettyOutput();
		try {
			al.csvOutput("test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int violationPrecedence(){
		int violation = 0;
		for (int i = 0; i < nPlanes; i++) {
			for (int j = 0; j < nPlanes; j++) {
				if(this.landing[i].getValue() <= this.landing[j].getValue() && this.takeOff[i].getValue()>this.takeOff[j].getValue())
					violation++;
			}
		}
		return violation;
	}
	
	private void contraintePrecedence(Solver s, boolean precedence) {
		if(precedence){
		System.out.println("Utilise d'une contrainte de precedence");
		brokenConstraint = VariableFactory.boundedArray("broken constraint", nPlanes, 0, 1, s);
		minBreak = VF.enumerated("breaker", 0, nPlanes*nPlanes, s);
		int compteur = 0;
		for (int i = 0; i < nPlanes-1; i++) {
			for (int j = i+1; j < nPlanes; j++) {
				//version uniquement pour les fenetre fixes
				if(!this.noOverLapping(landing[i],this.takeOff[i], landing[j], takeOff[j])){
					compteur++;
					Constraint[] cons = new Constraint[]{IntConstraintFactory.arithm(landing[i], "<=", landing[j]), IntConstraintFactory.arithm(takeOff[i], ">=", takeOff[j])};
					s.post(LogicalConstraintFactory.ifThenElse(LogicalConstraintFactory.and(cons), IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(1, s)), IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(0, s))));
					cons = new Constraint[]{IntConstraintFactory.arithm(landing[j], "<=", landing[i]), IntConstraintFactory.arithm(takeOff[j], ">=", takeOff[i])};
					s.post(LogicalConstraintFactory.ifThenElse(LogicalConstraintFactory.and(cons), IntConstraintFactory.arithm(brokenConstraint[j], "=", VariableFactory.fixed(1, s)), IntConstraintFactory.arithm(brokenConstraint[j], "=", VariableFactory.fixed(0, s))));
					
				}		
//				if (!(this.landing[i].getLB() > this.takeOff[j].getUB() || this.landing[j].getLB() > this.takeOff[i].getUB())) {
//					BoolVar bv = VariableFactory.bool("oui", this.s);
//					s.post(LogicalConstraintFactory.reification(bv, LogicalConstraintFactory.and(IntConstraintFactory.arithm(landing[i], "<=", landing[j]), IntConstraintFactory.arithm(takeOff[i], ">=", takeOff[j]))));
//					s.post(LogicalConstraintFactory.ifThenElse(bv, IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(1, s)),
//					IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(0, s))));
//					s.post(LogicalConstraintFactory.ifThen(bv, IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(1, s))));
//					s.post(LogicalConstraintFactory.ifThen(bv.not(), IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(0, s))));
//				}

			}
		}
		System.out.println(compteur);
		s.post(ICF.sum(brokenConstraint, minBreak));
		}
		else{
			System.out.println("N'utilise PAS une contrainte de precedence");
			brokenConstraint = VariableFactory.boundedArray("broken constraint", nPlanes, 0, 1, s);
			minBreak = VF.fixed(0, s);
		}
	}
	
	private boolean noOverLapping(IntVar landing, IntVar takeoff, IntVar landing2, IntVar takeoff2){
		return landing.getLB() > takeoff2.getUB() || landing2.getLB() > takeoff.getUB();	
	}

	private void simpleCumulative(Solver s) {
		heightInCumulatives = VF.enumeratedMatrix("heightInCumulative",
				this.getnPlanes(), this.getnTracks(), 0,
				this.setOfTypes[this.setOfTypes.length - 1], s);
		for (int u = 0; u < this.getnTracks(); u++) {
			for (int v = 0; v < this.getnPlanes(); v++) {
				s.post(IntConstraintFactory.times(
						VariableFactory.fixed(this.typePlane[v], s),
						this.tracks[u][v], heightInCumulatives[v][u]));
			}
			//System.out.println();
			s.post(IntConstraintFactory.cumulative(taskPlanes,
					ArrayUtils.getColumn(heightInCumulatives, u),
					VariableFactory.fixed(this.getCapacity()[u], s)));
		}
	}

	private void symetrieBreaking(Solver s) {
		sumByTracks = VF.boundedArray("sum planes by track", nTracks, 0, nPlanes, s);
		for (int t = 0; t < nTracks; t++)
			s.post(ICF.sum(tracks[t], sumByTracks[t]));

		for (int t1 = 0; t1 < nTracks - 1; t1++) {
			for (int t2 = t1 + 1; t2 < nTracks; t2++) {
				if (this.capacity[t1] == this.capacity[t2]) {
					//System.out.println(this.capacity[t1] + " == " + this.capacity[t2]);
					s.post(ICF.arithm(this.sumByTracks[t1], ">=", this.sumByTracks[t2]));
					//System.out.println(this.sumByTracks[t1].toString() + " >= " + this.sumByTracks[t2].toString() );
				}
			}
		}
	}

	private void multiCumulative(Solver s) {
		heightInCumulatives = VF.enumeratedMatrix("heightInCumulative",
				this.getnPlanes(), this.getnTracks(), 0,
				this.setOfTypes[this.setOfTypes.length - 1], s);
		for (int u = 0; u < this.getnTracks(); u++) {
			for (int v = 0; v < this.getnPlanes(); v++) {
				s.post(IntConstraintFactory.times(
						VariableFactory.fixed(this.typePlane[v], s),
						this.tracks[u][v], heightInCumulatives[v][u]));
				// System.out.print(heightInCumulatives[v][u] + " ,");
			}
		}

		int[][] successors = new int[this.taskPlanes.length][0];

		vCapacities = new IntVar[this.capacity.length];
		for (int c = 0; c < this.capacity.length; c++) {
			vCapacities[c] = VF.fixed(this.capacity[c], s);
		}

		int nbTasks = this.taskPlanes.length;
		int nbResources = vCapacities.length;

		int[] resourceType = new int[nbResources];
		for (int i = 0; i < this.nTracks; i++) {
			resourceType[i] = 0;
		}

		int[] interestingTimePoints = new int[0];
		int[] interestingResources = new int[0];

		int nbInterestingTimePoints = interestingTimePoints.length;
		int nbInterestingResources = interestingResources.length;

		IntVar[][] vLoads = new IntVar[nbInterestingTimePoints][nbInterestingResources];
		for (int i = 0; i < nbInterestingTimePoints; i++) {
			for (int j = 0; j < nbInterestingResources; j++) {
				vLoads[i][j] = VariableFactory.bounded("load_"
						+ interestingTimePoints[i], 0,
						this.capacity[interestingResources[j]], s);
			}
		}

		int hIdx = 3 * nbTasks;
		IntVar[] allVars = new IntVar[hIdx + nbTasks * nbResources
				+ nbResources + nbInterestingTimePoints
				* nbInterestingResources];
		for (int t = 0; t < nbTasks; t++) {
			allVars[t] = this.landing[t];
			allVars[t + nbTasks] = this.duration[t];
			allVars[t + 2 * nbTasks] = this.takeOff[t];
			for (int r = 0; r < nbResources; r++) {
				allVars[hIdx + t * nbResources + r] = heightInCumulatives[t][r];
			}
		}
		int cIdx = hIdx + nbTasks * nbResources;
		for (int r = 0; r < nbResources; r++) {
			allVars[cIdx + r] = vCapacities[r];
		}
		int lIdx = cIdx + nbResources;

		for (int i = 0; i < nbInterestingTimePoints; i++) {
			for (int j = 0; j < nbInterestingResources; j++) {
				allVars[lIdx + i * nbInterestingResources + j] = vLoads[i][j];
			}
		}

		Constraint c = new Constraint(allVars, s);
		c.addPropagators(new PropTTPCDynamicSweepLoads(allVars, nbTasks,
				nbResources, this.capacity, successors, resourceType,
				interestingTimePoints, interestingResources));
		s.post(c);
	}

	private void sortCapacity() {
		Arrays.sort(capacity);
		ArrayUtils.reverse(capacity);
	}

	private void sortPlanes() {
		int[] planes = new int[this.getnPlanes()];
		int k = 0;
		for (int s = 0; s < ( 60 * 24 ) - 30; s++) {
			for (int i = 0; i < this.getnPlanes(); i++) {
				if (this.windowStart[i] == s) {
					planes[k] = i;
					k++;
				}
			}
		}
		this.setNewOrder(planes);
	}

	private void setNewOrder(int[] planes) {
		int[] oldWindowStart = this.windowStart;
		int[] oldMinDuration = this.minDuration;
		int[] oldMaxDuration = this.maxDuration;
		int[] oldWindowEnd = this.windowEnd;
		int[] oldTypePlane = this.typePlane;
		this.windowStart = new int[this.windowStart.length];
		this.minDuration = new int[this.minDuration.length];
		this.maxDuration = new int[this.maxDuration.length];
		this.windowEnd = new int[this.windowEnd.length];
		this.typePlane = new int[this.typePlane.length];
		for (int i = 0; i < this.getnPlanes(); i++) {
			this.windowStart[i] = oldWindowStart[planes[i]];
			this.minDuration[i] = oldMinDuration[planes[i]];
			this.maxDuration[i]=oldMaxDuration[planes[i]];
			this.windowEnd[i] = oldWindowEnd[planes[i]];
			this.typePlane[i] = oldTypePlane[planes[i]];
		}
	}

	public void prettyOutput() {

		System.out.println("Nombre de violations : " + this.violationPrecedence());
		
		for (int t = 0; t < this.getnTracks(); t++) {
			//on place les avions dans l'ordre d'atterrissage
			HashMap<Integer, Integer> ordedPlaneOnTheTrack = new HashMap<Integer, Integer>(60*24);
			for (int plane = 0; plane < nPlanes; plane++) {
				if (this.tracks[t][plane].getValue() == 1) {
					ordedPlaneOnTheTrack.put(this.landing[plane].getValue(), plane);
				}
			}
			//On genere l'ensemble des pas de temps d'interet pour chaque piste
			HashMap<Integer, Integer> interrestingPoints = new HashMap<Integer, Integer>();
			for (int keyPlane : ordedPlaneOnTheTrack.keySet()) {
				int plane = ordedPlaneOnTheTrack.get(keyPlane);
				if (!interrestingPoints.containsKey(this.landing[plane].getValue())) {
					interrestingPoints.put(this.landing[plane].getValue(), 0);
				}
				if (!interrestingPoints.containsKey(this.takeOff[plane].getValue())) {
					interrestingPoints.put(this.takeOff[plane].getValue(), 0);
				}

			}

			//On calcul la charge pour chaque point d'interet
			int previousValue = 0;
			int justLanded;
			int justTookOff;
			for (int keyPoints : asSortedList(interrestingPoints.keySet())) {
				justLanded = 0;
				justTookOff = 0;
				for (int keyPlane : ordedPlaneOnTheTrack.keySet()) {
					int plane = ordedPlaneOnTheTrack.get(keyPlane);
					if (this.landing[plane].getValue() == keyPoints) {
						justLanded += this.typePlane[plane];
						//System.out.println("time : " + keyPoints + " landed : " + justLanded);
					}
					if (this.takeOff[plane].getValue() == keyPoints) {
						justTookOff += this.typePlane[plane];
						//System.out.println("time : " + keyPoints + " tookOff : " + justTookOff);
					}

				}
				previousValue = previousValue + justLanded - justTookOff;
				System.out.print(" keyPoints : " + keyPoints + " previous value : " + previousValue);
				interrestingPoints.put(keyPoints, previousValue);
			}
			

			System.out.println("");
			System.out.print("load of track Number " + t + " of size " + this.capacity[t] + " : ");
			String s = "";
			for (int key : asSortedList(interrestingPoints.keySet())) {
				s = s + " " + interrestingPoints.get(key);
			}
			System.out.println(s);
		}
	}

	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		List<T> list = new ArrayList<T>(c);
		java.util.Collections.sort(list);
		return list;
	}

	/**
	 * idPlane;idtrack;arrivalTime;departureTime;duration;capacity
	 *
	 * @param nameFile
	 * @throws IOException
	 */
	public void csvOutput(String nameFile) throws IOException {
		System.out.println("Creating CSV File");
		File file = new File(nameFile + ".csv");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		//Les pistes
		bw.write("idtrack; capacity" + "\n");
		for (int track = 0; track < this.getnTracks(); track++) {
			bw.write(track + "; " + this.capacity[track] + " \n");
		}

		//Les avions		
		bw.write("idPlane; idtrack; arrivalTime; departureTime; duration; capacity" + "\n");
		for (int plane = 0; plane < nPlanes; plane++) {
			String content = "";
			content += plane + "; ";
			int track = 0;
			for (int t = 0; t < nTracks; t++) {
				if (tracks[t][plane].getValue() == 1)
					track = t;
			}
			content += track + "; " + landing[plane].getValue() + "; " + takeOff[plane].getValue() + "; " + duration[plane].getValue() + "; " + typePlane[plane] + "\n";
			bw.write(content);
		}
		bw.close();
	}

	public int[] getWindowEnd() {
		return windowEnd;
	}

	public void setWindowEnd(int[] windowEnd) {
		this.windowEnd = windowEnd;
	}

	public int[] getWindowStart() {
		return windowStart;
	}


	public void setWindowStart(int[] windowStart) {
		this.windowStart = windowStart;
	}


	public int[] getMinDuration() {
		return minDuration;
	}


	public void setWindowDuration(int[] minDuration) {
		this.minDuration = minDuration;
	}


	public int[] getTypePlane() {
		return typePlane;
	}


	public void setTypePlane(int[] typePlane) {
		this.typePlane = typePlane;
	}


	public int[] getCapacity() {
		return capacity;
	}


	public void setCapacity(int[] capacity) {
		this.capacity = capacity;
	}


	public int getnPlanes() {
		return nPlanes;
	}


	public void setnPlanes(int nPlanes) {
		this.nPlanes = nPlanes;
	}


	public int getnTracks() {
		return nTracks;
	}


	public void setnTracks(int nTracks) {
		this.nTracks = nTracks;
	}

	public String[] getSchedule() {
		return schedule;
	}

	public Solver getSolver() {
		return this.s;
	}


}
