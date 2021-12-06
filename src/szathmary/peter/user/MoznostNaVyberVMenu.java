package szathmary.peter.user;

import java.util.Scanner;

/**
 * Sluzi na vypisanie vsetkych moznosti a nacitanie vstupu pouzivatela a kontrolu, ci si pouzivatel vybral cislo, alebo vobec nieco
 */
public class MoznostNaVyberVMenu {
    /**
     * Vypise vsetky moznosti na zaklade vstupnych parametrov + 0. moznost na vratenie sa do menu
     *
     * @param parameters Vsetky moznosti, ktore sa maju vypisat
     */
    public MoznostNaVyberVMenu(String... parameters) {
        System.out.println("\n0. Vrat sa do menu");
        for (String parameter : parameters) {
            System.out.println(parameter);
        }
    }

    /**
     * Nacitava od pouzivatelia moznost, pokial pouzivatel nejaku nezada
     *
     * @return Nacitana vybrana moznost
     */
    public String getVyberMoznosti() {
        Scanner vstup = new Scanner(System.in);
        String vystup;
        System.out.print("> ");
        vystup = vstup.nextLine();

        while (vystup.equals("")) {
            System.out.println("Musis nieco zadat!");
            System.out.print("> ");
            vystup = vstup.nextLine();
        }
        return vystup;
    }

    /**
     * Nacitava od pouzivatela cislo vybranej moznosti, kontroluje ci pouzivatel nieco zadal, a kontroluje, ci zadal LEN cislo
     *
     * @return Ak je nacitany vstup cislo, vrati ho, inak vrati null
     */
    public String getVyberMoznostiCislo() {
        Scanner vstup = new Scanner(System.in);

        System.out.print("> ");
        String vyber = vstup.nextLine();

        while (vyber.equals("")) {
            System.out.println("Musis nieco zadat!");
            System.out.print("> ");
            vyber = vstup.nextLine();
        }

        if (this.kontrolujCiJeCislo(vyber)) {
            return vyber;
        }
        return null;
    }


    /**
     * Kontroluje, ci je vstup cislo
     *
     * @param vstup Vstup, ktory sa ma skontrolovat
     * @return ak je vstup LEN cislo, vrati TRUE, inac FALSE
     */
    private boolean kontrolujCiJeCislo(String vstup) {
        for (int i = 0; i < vstup.length(); i++) {
            char aktualnePismeno = vstup.charAt(i);
            boolean kontrola = Character.isAlphabetic(aktualnePismeno);
            if (kontrola) {
                System.out.println("Musis zadat cislo!");
                return false;
            }
        }
        return true;
    }
}