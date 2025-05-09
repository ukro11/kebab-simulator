package KAGO_framework.control;

import KAGO_framework.Config;

/**
 * Diese Klasse enthält die main-Methode. Von ihr wird als erstes ein Objekt innerhalb der main-Methode erzeugt,
 * die zu Programmstart aufgerufen wird und kein Objekt benötigt, da sie statisch ist.
 * Die erste Methode, die also nach der main-Methode aufgerufen wird, ist der Konstruktor dieser Klasse. Aus ihm
 * wird alles weitere erzeugt.
 * Vorgegebene Klasse des Frameworks. Modifikation auf eigene Gefahr.
 */
public class MainController {

    /**
     * Diese Methode startet das gesamte Framework und erzeugt am Ende dieses Prozesses ein Objekt der Klasse
     * ProgramController aus dem Paket "my_project > control"
     */
    public static void startFramework(){
        if ( Config.INFO_MESSAGES) System.out.println("***** PROGRAMMSTART ("+" Framework: "+Config.VERSION+") *****.");
        if ( Config.INFO_MESSAGES) System.out.println("** Supported Java-Versions: "+ Config.JAVA_SUPPORTED);
        if ( Config.INFO_MESSAGES) System.out.println("");
        if ( Config.INFO_MESSAGES) System.out.println("** Ablauf der Framework-Initialisierung: **");
        if ( Config.INFO_MESSAGES) System.out.println("  > MainController: Ich wurde erzeugt. Erstelle ein ViewController-Objekt zur Steuerung der View...");
        new ViewController();
    }
}
