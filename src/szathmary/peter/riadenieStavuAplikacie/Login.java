package szathmary.peter.riadenieStavuAplikacie;

import szathmary.peter.databaza.DatabazaControl;

import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Sluzi na prihlasenie pouzivatela do aplikacie
 */
public class Login {

    /**
     * Vypita si od pouzivatela jeho meno a heslo, heslo da do ciselnej podoby a porovna ho s udajmi v databaze
     * ak sa najde zhoda, vrati objekt s udajmi o pouzivatelovi z databazi
     *
     * @param db objekt na komunikaciu s konkretnou databazou
     * @return ak sa najde zhoda s menom a heslo v databaze, vrati objekt s udajmi o pouzivatelovi, inac vrati null
     */
    public List<String> prihlasPouziatela(DatabazaControl db) {
        Scanner vstup = new Scanner(System.in);
        System.out.print("Zadaj svoje prihlasovacie meno: ");
        String meno = vstup.nextLine();
        System.out.print("Zadaj svoje heslo: ");
        String heslo = vstup.nextLine();

        int hesloZaHashovane = this.hashujHeslo(heslo);
        if (this.kontrolujLogin(meno, hesloZaHashovane, db)) {
            return this.getUdajeOPrihlasenomPouzivatelovi(db, meno, hesloZaHashovane);
        } else {
            System.out.println("Meno alebo heslo nie je spravne");
            return null;
        }
    }

    /**
     * Vrati udaje o uzivatelovi, ktore ziska z databazi
     *
     * @param db               Objekt na komunikaciu s konkretnou databazou
     * @param meno             Login pouzivatela
     * @param hesloZaHashovane Zahashovane heslo pouzivatela
     * @return List naplneny udajmi o pouzivatelovi
     */
    private List<String> getUdajeOPrihlasenomPouzivatelovi(DatabazaControl db, String meno, int hesloZaHashovane) {
        List<String> udajeOUcte = db.zistiDruhAInformacieOUcte(meno, hesloZaHashovane);

        System.out.printf("\nVitaj, %s %s!\n", udajeOUcte.get(1), udajeOUcte.get(2));
        udajeOUcte = Collections.unmodifiableList(udajeOUcte);
        return udajeOUcte;
    }


    /**
     * Porovnava prihlasovacie udaje s prihlasovacimi udajmi v databaze
     *
     * @param login            Prihlasovacie meno uzivatela
     * @param hesloZaHashovane Zahashovane heslo pouzivatela
     * @param db               Objekt na komunikaciu s konkretnou databazou
     * @return ak sa najde zhoda medzi udajmi, vrati TRUE, inac vrati FALSE
     */
    private boolean kontrolujLogin(String login, int hesloZaHashovane, DatabazaControl db) {
        return db.kontrolujLogin(login, hesloZaHashovane);
    }

    /**
     * Da heslo do ciselnej podoby
     *
     * @param heslo Heslo, ktore chceme dat do ciselnej podoby
     * @return vrati heslo v ciselnej podoby
     */
    private int hashujHeslo(String heslo) {
//        System.out.println(heslo.hashCode());
        return heslo.hashCode();
    }
}
