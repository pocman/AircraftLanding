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

import com.sun.security.jgss.InquireType;

import display.OutPut;
import display.ParseurEntree;
import display.Representation;


public class AircraftLanding {

	Random r = new Random(42);
	final static int MinutesVersPasTemps = 1;
	/*
	 * L'instance du solver
	 */
	Solver s;

	/*
	 * Une tache contient la date d'arriveeee, la date de deeepart 
	 * et la dureeee de stationnement de l'avion sur la piste
	 */
	Task[] taskPlanes;
	IntVar[] duration, landing, takeOff; //for each plane
	
	/*
	 * Une matrice 0/1 de taille nTracks * nPlanes
	 * qui donne la position des avions sur les pistes
	 */
	IntVar[][] tracks;
	
	/*
	 * Les ressources occupeeees par chaque avion sur chaque piste
	 * Matrice de taille nPlanes * nTracks
	 */
	IntVar[][] heightInCumulatives;
	
	/*
	 * Le nombre d'avions par piste
	 */
	IntVar[] sumByTracks;
	
	/*
	 * Le numeero de la piste sur laquelle est chaque avion
	 */
	IntVar[] tracksByPlane;
	
	/*
	 * Si l'avion viole une contrainte de preeeceeedence ou non
	 */
	IntVar[] brokenConstraint;
	
	/*
	 * Somme des avions qui violent une contrainte de preeeceeedence
	 */
	IntVar minBreak;
	
	/*
	 * Variable repreeesentant la capaciteee d'une piste
	 */
	IntVar[] vCapacities;
	
	/*
	 * Boolean speeecifiant si on utilise plusieurs contriantes cumulatives 
	 * ou la contrainte cumulative multi d'Arnauld Letort
	 */
	boolean utiliseMultiCumulative;


	int[] setOfTypes = new int[]{1, 2, 3};//la valeur d'occupation pour chaque type d'avion
	int[] windowStart, minDuration, windowEnd, maxDuration; //valeur en entreeee pour la fenetre de chaque avion
	int[] typePlane; //le type de chaque avion
	int[] capacity; //la capaciteee de chaque piste
	int nPlanes; //le nombre d'avion dans l'instance
	int nTracks; //le nombre de piste dans l'instance
	String[] schedule; //les donneeees en entreeee seeepareeees avec des ':'



	/**Le constructeur de notre solver
	 * 
	 * @param schedule Chaque avion est repreeesenteee par une string avec les dureeees des intervalles en minutes et son type d'avion espaceee par des ':'
	 * @param capacity La capaciteee de chaque piste
	 * @param fenetreFixe Si fourni les fenetres en entreeee
	 * @param multiCumulative Si on utilise la contrainte cumulativeMultiple
	 */
	public AircraftLanding(String[] schedule, int[] capacity, boolean fenetreFixe, boolean multiCumulative) {
		this.schedule = schedule;
		this.utiliseMultiCumulative = multiCumulative;
		ArrayList<int[]> planes = new ArrayList<int[]>();

		if(!fenetreFixe) {
			//on parse notre entree
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
		//notre heuristique de tri des avions pour ensuite utiliser une strategie input_order
		this.sortCapacity();
		this.sortPlanes();
	}

	/**Creation des variables et appelle des heuristiques
	 * 
	 * @param s L'instance de notre solveur
	 * @param precedence On utilise une classe de precedence
	 */
	public void model(Solver s, Boolean precedence) {

		System.out.println();
		System.out.println("----------Model--------" + s.getName());
		
		this.s = s;
		taskPlanes = new Task[nPlanes];
		landing = new IntVar[nPlanes];
		duration = new IntVar[nPlanes];
		takeOff = new IntVar[nPlanes];
		tracksByPlane = new IntVar[nPlanes];

		for (int i = 0; i < nPlanes; i++) {
			//Variable d'atterissage tronquee du temps minimal sur tarmac
			landing[i] = VariableFactory.bounded("Landing " + i, windowStart[i], windowEnd[i]-minDuration[i], s);
			duration[i] = VariableFactory.bounded("Duration on airport " + i, minDuration[i], maxDuration[i], s);
			//Variable de decollage tronquee du temps minimal sur tarmac
			takeOff[i] = VariableFactory.bounded("Take off " + i, windowStart[i]+minDuration[i], windowEnd[i], s);
			//Un avion peut etre sur toutes les pistes
			tracksByPlane[i] = VariableFactory.bounded("Tracks for plane " + i, 0, this.getnTracks() - 1, s);
			//creation des taches qui assurent le respect des contraintes 
			taskPlanes[i] = VariableFactory.task(landing[i], duration[i], takeOff[i]);
			System.out.print("type :" + typePlane[i]);
			System.out.print(" " + this.landing[i]);
			System.out.print(" " + this.duration[i]);
			System.out.println(" " + this.takeOff[i]);
		}


		//for each plane which track, it's on
		tracks = VariableFactory.enumeratedMatrix("track of plane", nTracks, nPlanes, 0, 1, s);

		//Cassage de symetrie
		System.out.println("Utilise un cassage de symetries");
		this.symetrieBreaking(s);

		
		//Un avion ne peut etre que sur une seule piste
		System.out.println("Unicite de positionnement de l'avion sur une piste");
		for (int plane = 0; plane < nPlanes; plane++) {
			s.post(ICF.count(1, ArrayUtils.getColumn(tracks, plane), VF.fixed(1, s)));
		}

		//Pour chaque avion, on a son numero de piste
		for (int i = 0; i < nTracks; i++) {
			for (int j = 0; j < nPlanes; j++) {
				s.post(LogicalConstraintFactory.ifThen(IntConstraintFactory.arithm(tracks[i][j], "=", 1), IntConstraintFactory.arithm(tracksByPlane[j], "=", i)));
			}
		}

		
		//On ne peut pas avoir d'avions qui decollent ou atterissent la meme minute
		//Si il y a trop d'avion, cette contrainte ne peu plus tenir, il faut changer de pas de temps ou mettre un alldiff par piste
		System.out.println("Contrainte sur l'utilisation de la piste");
		this.operationUniqueUniteTemps();
		


		//Contrainte souple de precedence entre les avions
		this.contraintePrecedence(s, precedence);

		if (utiliseMultiCumulative) {
			//Utilisation de la contrainte d'Arnault Letort
			System.out.println("Utilise la contrainte multidimensionnelle");
			this.multiCumulative(s);

		} else {
			//Utilisation d'une contrainte cumulative par piste
			System.out.println("N'utiliser PAS la contrainte multidimensionnelle");
			this.simpleCumulative(s);
		}

	}
	


	public void solve(int timeOut) {
		SMF.log(s, true, false);
		SMF.limitTime(s, timeOut);
		//s.findSolution();
		s.findOptimalSolution(ResolutionPolicy.MINIMIZE, minBreak);
	}

	/**
	 * Permet de choisir l'heuristique
	 */
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
			System.out.println("Veuillez choisir la taille de l'aeroport (default, petit, moyen ou grand)");
			reponse = sc.next();
		}
		while (!(reponse.equals("petit") || reponse.equals("moyen") || reponse.equals("grand") || reponse.equals("default")));
		InstanceGenerator.TAILLE_AEROPORT taille;
		if (reponse.equals("petit")) taille = InstanceGenerator.TAILLE_AEROPORT.PETIT;
		else if (reponse.equals("moyen")) taille = InstanceGenerator.TAILLE_AEROPORT.MOYEN;
		else if (reponse.equals("grand")) taille = InstanceGenerator.TAILLE_AEROPORT.GRAND;
		else taille = InstanceGenerator.TAILLE_AEROPORT.DEFAULT;
		boolean isPositifNumber;
		do {
			System.out.println("Veuillez entrer un entier (Afin d'assurer l'unicite de l'aeroport)");
			reponse = sc.nextLine();
			isPositifNumber = false;
			if(sc.hasNextLine())
				reponse = sc.nextLine();
			try {
				isPositifNumber = Integer.parseInt(reponse) >= 0;
			}
			catch (NumberFormatException e) {
				isPositifNumber = false;
			}			
		}
		while (!isPositifNumber);
		int alea = Integer.parseInt(reponse);
		//		do {
		//		System.out.println("Voulez-vous utiliser des fenetres de temps de stationnement fixes? (y/n)");
		//		reponse = sc.next();
		//	}
		//	while (!(reponse.equals("y") || reponse.equals("n")));
		//	boolean fenetresFixes;
		//	if (reponse.equals("y")) fenetresFixes = true;
		//	else fenetresFixes = false;
		boolean fenetresFixes = true;
		do {
			System.out.println("Voulez-vous utiliser la contrainte multiCumulative? (y/N)");
			reponse = sc.nextLine();
		}
		while (!(reponse.equals("y") || reponse.equals("n") || reponse.equals("") ));
		boolean multiCumulative = reponse.equals("y");
		
		do {
			System.out.println("Voulez-vous utiliser la contrainte de precedence? (Y/n)");
			reponse = sc.nextLine();
		}
		while (!(reponse.equals("y") || reponse.equals("n") || reponse.equals("")));
		boolean precedence = !reponse.equals("n");
		int timeOut = 35000;
		do {
			System.out.println("Quelle valeur utiliser comme timeOut? (default 35sec)");
			reponse = sc.nextLine();
			isPositifNumber = false;
			if(reponse.equals(""))
				break;
			else{
				try {
					isPositifNumber = Integer.parseInt(reponse) >= 0;
				}
				catch (NumberFormatException e) {
					isPositifNumber = false;
				}	
			}
		}
		while (!isPositifNumber);
		if(!reponse.equals(""))
			timeOut = Integer.parseInt(reponse)*1000;

		AircraftLanding al;
		if(taille == InstanceGenerator.TAILLE_AEROPORT.DEFAULT)
			al = InstanceGenerator.defaultGenerator(multiCumulative);
		else
			al = InstanceGenerator.generator(taille, alea, fenetresFixes, multiCumulative);
		Solver s = new Solver("AircraftLanding");
		if(al == null) {
			System.out.println("No solution found !");
			System.exit(0);
		}
		al.model(s, precedence);
		al.chooseStrategy();
		al.solve(timeOut);
		al.prettyOutput();
		try {
			al.csvOutput("Aerport");
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParseurEntree parseur;
		try {
			parseur = new ParseurEntree(new File("test.csv"));
			OutPut out = new OutPut(parseur.getPlanes(),parseur.getPistes());
			out.outputCSV();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Post la alldiff globale sur l'unicite des actions par pas de temps
	 */
	private void operationUniqueUniteTemps() {
		if(this.nPlanes < 300)
			s.post(IntConstraintFactory.alldifferent(ArrayUtils.append(this.landing, this.takeOff), "BC"));
		else{
			s.post(IntConstraintFactory.alldifferent(this.landing, "BC"));
			System.out.println("Le pas de temps est trop gros, passer en Seconde et non Minutes");
		}
		
	}

	/**
	 * Retourne le nombre de violation de la contrainte de precedence pour le prettyOutput
	 * @return
	 */
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
	
	/**
	 * Post la contrainte de precedence
	 * Pour le moment on ne compte que les avions qui violent une precedence et pas le nombre exacte de precedence violees
	 * @param s
	 * @param precedence
	 */
	private void contraintePrecedence(Solver s, boolean precedence) {
		if(precedence){
		System.out.println("Utilise d'une contrainte de precedence");
		brokenConstraint = VariableFactory.boundedArray("broken constraint", nPlanes, 0, 1, s);
		minBreak = VF.enumerated("breaker", 0, nPlanes*nPlanes, s);
		for (int i = 0; i < nPlanes-1; i++) {
			for (int j = i+1; j < nPlanes; j++) {
				if(!this.noOverLapping(landing[i],this.takeOff[i], landing[j], takeOff[j])){
					Constraint[] cons = new Constraint[]{IntConstraintFactory.arithm(landing[i], "<=", landing[j]), IntConstraintFactory.arithm(takeOff[i], ">=", takeOff[j])};
					s.post(LogicalConstraintFactory.ifThenElse(LogicalConstraintFactory.and(cons), IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(1, s)), IntConstraintFactory.arithm(brokenConstraint[i], "=", VariableFactory.fixed(0, s))));
					cons = new Constraint[]{IntConstraintFactory.arithm(landing[j], "<=", landing[i]), IntConstraintFactory.arithm(takeOff[j], ">=", takeOff[i])};
					s.post(LogicalConstraintFactory.ifThenElse(LogicalConstraintFactory.and(cons), IntConstraintFactory.arithm(brokenConstraint[j], "=", VariableFactory.fixed(1, s)), IntConstraintFactory.arithm(brokenConstraint[j], "=", VariableFactory.fixed(0, s))));
				}		
			}
		}
		s.post(ICF.sum(brokenConstraint, minBreak));
		}
		else{
			System.out.println("N'utilise PAS une contrainte de precedence");
			brokenConstraint = VariableFactory.boundedArray("broken constraint", nPlanes, 0, 1, s);
			minBreak = VF.fixed(0, s);
		}
	}
	
	/**
	 * Retourne si deux avions peuvent creer une violation de precedence
	 * @param landing fenetre d'atterrisage de l'avion 1
	 * @param takeoff fenetre decollage de l'avion 1
	 * @param landing2 fenetre d'atterrisage de l'avion 2
	 * @param takeoff2 fenetre decollage de l'avion 1
	 * @return
	 */
	private boolean noOverLapping(IntVar landing, IntVar takeoff, IntVar landing2, IntVar takeoff2){
		return landing.getLB() > takeoff2.getUB() || landing2.getLB() > takeoff.getUB();	
	}

	/**
	 * Post les contraintes cumulative pour chaque piste
	 * @param s
	 */
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
			s.post(IntConstraintFactory.cumulative(taskPlanes,
					ArrayUtils.getColumn(heightInCumulatives, u),
					VariableFactory.fixed(this.getCapacity()[u], s)));
		}
	}

	/**
	 * Post les surcontraintes permettant de casser les symetries pour les recherches de non solutions.
	 * @param s
	 */
	private void symetrieBreaking(Solver s) {
		sumByTracks = VF.boundedArray("sum planes by track", nTracks, 0, nPlanes, s);
		for (int t = 0; t < nTracks; t++)
			s.post(ICF.sum(tracks[t], sumByTracks[t]));

		for (int t1 = 0; t1 < nTracks - 1; t1++) {
			for (int t2 = t1 + 1; t2 < nTracks; t2++) {
				if (this.capacity[t1] == this.capacity[t2]) {
					s.post(ICF.arithm(this.sumByTracks[t1], ">=", this.sumByTracks[t2]));
				}
			}
		}
	}

	/**
	 * Post la contrainte cumulative multiple d'arnauld letort
	 * @param s
	 */
	private void multiCumulative(Solver s) {
		heightInCumulatives = VF.enumeratedMatrix("heightInCumulative",
				this.getnPlanes(), this.getnTracks(), 0,
				this.setOfTypes[this.setOfTypes.length - 1], s);
		for (int u = 0; u < this.getnTracks(); u++) {
			for (int v = 0; v < this.getnPlanes(); v++) {
				s.post(IntConstraintFactory.times(
						VariableFactory.fixed(this.typePlane[v], s),
						this.tracks[u][v], heightInCumulatives[v][u]));
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

	/**
	 * Tri les pistes par taille
	 */
	private void sortCapacity() {
		Arrays.sort(capacity);
		ArrayUtils.reverse(capacity);
	}

	/**
	 * tri les avions pour l'heuristique sur les firstinput
	 */
	private void sortPlanes() {
		int[] planes = new int[this.getnPlanes()];
		int k = 0;
		for (int s = 0; s < ( 60 * 24 * AircraftLanding.MinutesVersPasTemps ) - 30 * AircraftLanding.MinutesVersPasTemps ; s++) {
			for (int i = 0; i < this.getnPlanes(); i++) {
				if (this.windowStart[i] == s) {
					planes[k] = i;
					k++;
				}
			}
		}
		this.setNewOrder(planes);
	}

	/**
	 * Tri des autres tableaux 
	 * @param planes
	 */
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

	/**
	 * premiere ihm en console pour montrer les points d'interets et les capacites par pistes
	 */
	public void prettyOutput() {

		ArrayList<String> toPrint = new ArrayList<String>();
		toPrint.add("Nombre de violations : " + this.violationPrecedence());
		
		System.out.println("Nombre de violations : " + this.violationPrecedence());
		
		//on place les avions dans l'ordre d'atterrissage
		HashMap<Integer, ArrayList<Integer>> ordedPlaneOnTheTrack = new HashMap<Integer, ArrayList<Integer>>(60*24);
		for (int t = 0; t < this.getnTracks(); t++) {
		for (int plane = 0; plane < nPlanes; plane++) {
			if (this.tracks[t][plane].getValue() == 1) {
				if(!ordedPlaneOnTheTrack.containsKey(this.landing[plane].getValue()))
					ordedPlaneOnTheTrack.put(this.landing[plane].getValue(), new ArrayList<Integer>());
				ordedPlaneOnTheTrack.get(this.landing[plane].getValue()).add(plane);					
				}
			}
		}
		
		
		//On genere l'ensemble des pas de temps d'interet pour chaque piste
		HashMap<Integer, Integer> interrestingPoints = new HashMap<Integer, Integer>();
		for (int keyPlane : ordedPlaneOnTheTrack.keySet()) {
			ArrayList<Integer> planes = ordedPlaneOnTheTrack.get(keyPlane);
			for(int plane : planes){
				if (!interrestingPoints.containsKey(this.landing[plane].getValue())) {
					interrestingPoints.put(this.landing[plane].getValue(), 0);
				}
				if (!interrestingPoints.containsKey(this.takeOff[plane].getValue())) {
					interrestingPoints.put(this.takeOff[plane].getValue(), 0);
				}
			}
		}
		
		for (int t = 0; t < this.getnTracks(); t++) {
			//on place les avions dans l'ordre d'atterrissage
			ordedPlaneOnTheTrack = new HashMap<Integer, ArrayList<Integer>>(60*24);
			for (int plane = 0; plane < nPlanes; plane++) {
				if (this.tracks[t][plane].getValue() == 1) {
					if(!ordedPlaneOnTheTrack.containsKey(this.landing[plane].getValue()))
						ordedPlaneOnTheTrack.put(this.landing[plane].getValue(), new ArrayList<Integer>());
					ordedPlaneOnTheTrack.get(this.landing[plane].getValue()).add(plane);	
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
					ArrayList<Integer> planes = ordedPlaneOnTheTrack.get(keyPlane);
					for(int plane : planes){
					if (this.landing[plane].getValue() == keyPoints) {
						justLanded += this.typePlane[plane];

					}
					if (this.takeOff[plane].getValue() == keyPoints) {
						justTookOff += this.typePlane[plane];
					}
					}

				}
				previousValue = previousValue + justLanded - justTookOff;
				interrestingPoints.put(keyPoints, previousValue);
			}
			
			toPrint.add("");
			System.out.println("");
			toPrint.add("load of track Number " + t + " of size " + this.capacity[t] + " : ");
			System.out.print("load of track Number " + t + " of size " + this.capacity[t] + " : ");
			System.out.println();
			String s = "";
			for (int key : asSortedList(interrestingPoints.keySet())) {
				if(interrestingPoints.get(key) == 0)
					s = s + "  ";
				else
					s = s + " " + interrestingPoints.get(key);
			}
			toPrint.add(s);
			s.replace("  ", " ");
			System.out.println(s);
		}
		
		try {
			this.printSimpleIhm("simpleIhm", toPrint);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * retourne une liste triee e partir d'une collection
	 * @param c
	 * @return
	 */
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
	
	public void printSimpleIhm(String nameFile, ArrayList<String> toPrint) throws IOException{
		System.out.println("Creating SimpleIhm File");
		File file = new File(nameFile + ".txt");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		for (String s : toPrint) {
			bw.write(s + "\n");
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
