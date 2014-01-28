package display;

public class Representation {
	public static final String TRACK_SEPARATION =  "========================================================================"+System.getProperty("line.separator");
	public static final String DEPART_PETIT_AVION = " | \\_______________"+System.getProperty("line.separator")+"  ===(oooooooooo  O\\___"+
			System.getProperty("line.separator")+" (________<=====>______)->>";
	public static final String ARRIVEE_PETIT_AVION = "         _______________/ |"+System.getProperty("line.separator")+"     ___/O  oooooooooo)==="+
			System.getProperty("line.separator")+" <<-(______<=====>________)";
	public static final String DEPART_MOYEN_AVION = "_____"+System.getProperty("line.separator")+" \\ U \\_       ____"+System.getProperty("line.separator")+
			"  \\   \\/_______\\___\\_____________"+System.getProperty("line.separator")+"  < /_/   .....................  `-."+System.getProperty("line.separator")+
			"   `-----------,----,--------------'"+System.getProperty("line.separator")+"              /_____/";
	public static final String ARRIVEE_MOYEN_AVION = "                              _____"+System.getProperty("line.separator")+"                ____        _/ U /"+System.getProperty("line.separator")+
			"   ____________/___/_______\\/   /"+System.getProperty("line.separator")+".-'  .....................\\_\\ >"+System.getProperty("line.separator")+
			"'------------,----,-----------'"+System.getProperty("line.separator")+"             \\_____\\";
	public static final String DEPART_GRAND_AVION = "_______________"+System.getProperty("line.separator")+"|              \\\\"+System.getProperty("line.separator")+
			"|      USAF     \\\\___________________________________________________"+System.getProperty("line.separator")+" \\___________________________                                 \\__|___\\___"+
			System.getProperty("line.separator")+"  =  ______________________ |\\_________ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ '---__"+System.getProperty("line.separator")+
			"  =_________________________|/__________________________________________----";
	
	public static final String ARRIVEE_GRAND_AVION = "                                                                _______________"+System.getProperty("line.separator")+
			"                                                              //              |"+System.getProperty("line.separator")+"          ___________________________________________________//      USAF     |"+
			System.getProperty("line.separator")+"      ___/___|__/                                 ___________________________/"+System.getProperty("line.separator")+
			" __---' _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _______/| ______________________  ="+System.getProperty("line.separator")+"   ----__________________________________________\\|_________________________=";
	public static final String NO_PLANE = System.getProperty("line.separator")+System.getProperty("line.separator")+System.getProperty("line.separator");
	public static final String START = "###########################################"+System.getProperty("line.separator")+
			"||    __|__        __|__        __|__    ||"+System.getProperty("line.separator")+
			"|| *---o0o---*  *---o0o---*  *---o0o---* ||"+System.getProperty("line.separator")+
			"###########################################";
	
	
	public static void main(String[] args){
		System.out.println(Representation.DEPART_GRAND_AVION);
		System.out.println();
		System.out.println(Representation.ARRIVEE_GRAND_AVION);
	}
}
