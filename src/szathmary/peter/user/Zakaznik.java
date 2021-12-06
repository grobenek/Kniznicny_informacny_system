package szathmary.peter.user;

import org.jetbrains.annotations.NotNull;
import szathmary.peter.databaza.DatabazaControl;
import szathmary.peter.databaza.exceptions.KnihaJePozicanaException;
import szathmary.peter.databaza.exceptions.KnihaNeexistujeException;
import szathmary.peter.databaza.exceptions.KnihaNieJePozicanaException;
import szathmary.peter.databaza.exceptions.PouzivatelNeexistujeException;
import szathmary.peter.databaza.exceptions.PrekrocenyMaximalnyLimitPozicanychKnihException;
import szathmary.peter.databaza.search.HladajPodlaAutora;
import szathmary.peter.databaza.search.HladajPodlaId;
import szathmary.peter.databaza.search.HladajPodlaMena;
import szathmary.peter.databaza.search.HladajPodlaZanru;
import szathmary.peter.databaza.search.IHladajKnihu;
import szathmary.peter.riadenieStavuAplikacie.STAVYAPLIKACIE;

import java.util.Scanner;

/**
 * Objekt pouzivatela s druhom uctu zakaznik
 */
public class Zakaznik extends DefaultPouzivatel {
    /**
     * Konstruktor admina s volnymi parametrami (nie v kontajneri)
     *
     * @param druhUctu           druh uctu pouzivatela
     * @param meno               meno pouzivatela
     * @param priezvisko         priezvisko pouzivatela
     * @param id                 id pouzivatela
     * @param vek                vek pouzivatela
     * @param login              prihlasovacie meno pouzivatela
     * @param pozicaneKnihyPocet pocet pozicanych knih pouzivatela
     */
    public Zakaznik(String druhUctu, String meno, String priezvisko, String id, int vek, String login, int pozicaneKnihyPocet) {
        super(druhUctu, meno, priezvisko, id, vek, login, pozicaneKnihyPocet);
    }

    /**
     * Konstruktor zakaznika s parametrami v kontajnerovej triede UdajeOUzivatelovi
     *
     * @param udajeOUzivatelovi Udaje o pouzivatelovi zabalene v triede UdajeOUzivatelovi
     */
    public Zakaznik(UdajeOUzivatelovi udajeOUzivatelovi) {
        super(udajeOUzivatelovi);
    }

    /**
     * Vypise moznosti na vykonanie a nacita cislo vybranej moznosti
     *
     * @return Cislo vybranej moznosti
     */
    @Override
    public String vypisANacitajMoznost() {
        Scanner vstup = new Scanner(System.in);

        System.out.println("\n-----------------------------------------------");
        System.out.println("1. Vypis dostupne knihy");
        System.out.println("2. Vypis pozicane knihy");
        System.out.println("3. Vyhladaj knihu");
        System.out.println("4. Pozicaj si knihu");
        System.out.println("5. Vrat knihu");
        System.out.println("6. Vypis knihy, ktore je potrebne coskoro vratit");
        System.out.println("7. Odhlasit sa");
        System.out.println("0. Ukonci aplikaciu");
        System.out.println("-----------------------------------------------");
        System.out.print("> ");
        String vybranaMoznost = vstup.nextLine();
        vybranaMoznost = vybranaMoznost.replaceAll(" ", "");
        return vybranaMoznost;
    }

    /**
     * Na zaklade cisla vybranej moznosti vykona danu akciu
     *
     * @param vyber Cislo vybranej moznosti
     * @param db    Objekt na komunikaciu s konkretnou databazou
     * @return vrati STAVAPIKACIE na zaklade vykonanej akcie
     */
    @Override
    public STAVYAPLIKACIE vykonajVybranuMoznost(@NotNull String vyber, DatabazaControl db) {
        MoznostNaVyberVMenu idKnihyVstup;

        String idKnihy;
        switch (vyber) {
            case "0" -> {
                System.out.println("Aplikacia ukoncena!");
                System.exit(0);
            }
            case "1" -> {
                db.vypisDostupneKnihy();
                return STAVYAPLIKACIE.MENU;
            }
            case "2" -> {
                db.vypisPozicaneKnihyPouzivatela(this.getId());
                return STAVYAPLIKACIE.MENU;
            }
            case "3" -> {
                System.out.println("Podla coho chces vyhladat knihu?");
                String vybranaMoznost;
                int pocitadloPoctuKolNacitania = 0;
                do {
                    if (pocitadloPoctuKolNacitania > 0) {
                        System.out.println("Zadal si nespravnu moznost, skus to znovu!");
                    }
                    vybranaMoznost = this.vypisANacitajMoznostVyhladavania();
                    pocitadloPoctuKolNacitania++;
                } while (!(vybranaMoznost.equals("0") || vybranaMoznost.equals("1") || vybranaMoznost.equals("2") ||
                        vybranaMoznost.equals("3") || vybranaMoznost.equals("4")));
                this.hladaj(vybranaMoznost, db);
                return STAVYAPLIKACIE.MENU;
            }
            case "4" -> {
                do {
                    idKnihyVstup = new MoznostNaVyberVMenu("Zadaj ID knihy:");
                    idKnihy = idKnihyVstup.getVyberMoznostiCislo();
                } while (idKnihy == null);
                if (this.skontrolujUkoncenieMetody(idKnihy)) {
                    return STAVYAPLIKACIE.MENU;
                }
                try {
                    db.pozicajKnihu(idKnihy, this, null);
                } catch (PrekrocenyMaximalnyLimitPozicanychKnihException e) {
                    System.out.println("Prekrocil si limit 15 pozicanych knih!");
                    System.out.println("Tuto knihu si nemozes pozicat!");
                } catch (KnihaJePozicanaException exception) {
                    System.out.println("Tato kniha uz je pozicana. Vyber si inu.");
                } catch (KnihaNeexistujeException exception) {
                    System.out.println("Tato kniha neexistuje!");
                } catch (PouzivatelNeexistujeException exception) {
                    System.out.println("Pouzivatel s tymto ID neexistuje!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "5" -> {
                do {
                    idKnihyVstup = new MoznostNaVyberVMenu("Zadaj ID knihy:");
                    idKnihy = idKnihyVstup.getVyberMoznostiCislo();
                } while (idKnihy == null);
                if (this.skontrolujUkoncenieMetody(idKnihy)) {
                    return STAVYAPLIKACIE.MENU;
                }
                try {
                    db.vratKnihu(idKnihy, this, null);
                } catch (KnihaNieJePozicanaException e) {
                    System.out.println("Tuto knihu nemas pozicanu, nemozes ju vratit!");
                } catch (KnihaNeexistujeException e) {
                    System.out.println("Tato kniha neexistuje!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "6" -> {
                db.vypisKnihyKtoreTrebaOchviluVratit(this.getId());
                return STAVYAPLIKACIE.MENU;
            }
            case "7" -> {
                System.out.println("Odhlasenie uspesne!\n");
                return STAVYAPLIKACIE.LOGIN;
            }
            default -> {
                System.out.println("Nerozumiem ti.");
                return STAVYAPLIKACIE.MENU;
            }
        }
        return null;
    }

    /**
     * Skontroluje, ci si pouzivatel nevybral cislo 0 - cize ukoncenie aktualnej akcie a vratenie sa do menu
     *
     * @param kontrolovanyVstup Vybrane cislo na kontrolu
     */
    private boolean skontrolujUkoncenieMetody(String kontrolovanyVstup) {
        return kontrolovanyVstup.equals("0");
    }

    /**
     * Vypise a nacita cislo objektu, ktory chce pouzivatel hladat
     *
     * @return Cislo vybraneho objektu na hladanie
     */
    protected String vypisANacitajMoznostVyhladavania() {
        Scanner vstup = new Scanner(System.in);

        System.out.println("\n-------------------------");
        System.out.println("1. Hladaj podla ID");
        System.out.println("2. Hladaj podla nazvu");
        System.out.println("3. Hladaj podla autora");
        System.out.println("4. Hladaj podla zanru");
        System.out.println("0. Vrat sa do menu");
        System.out.println("--------------------------");
        System.out.print("> ");
        return vstup.nextLine();
    }

    /**
     * Na zaklade vybranej moznosti na hladanie vyhlada a vypise informacie o najdenych objektoch
     *
     * @param vybranaMoznost Cislo vybranej moznosti
     * @param db             Objekt na komunikaciu s konkretnou databazou
     */
    protected void hladaj(String vybranaMoznost, DatabazaControl db) {
        IHladajKnihu hladaj;

        MoznostNaVyberVMenu idKnihyVstup;
        MoznostNaVyberVMenu nazovKnihyVstup;
        MoznostNaVyberVMenu menoAutoraVstup;
        MoznostNaVyberVMenu nazovZanruVstup;
        String nazovKnihy;
        String idKnihy;
        String menoAutora;
        String nazovZanru;
        switch (vybranaMoznost) {
            case "0":
                break;
            case "1":
                hladaj = new HladajPodlaId();

                do {
                    idKnihyVstup = new MoznostNaVyberVMenu("Zadaj ID knihy:");
                    idKnihy = idKnihyVstup.getVyberMoznostiCislo();
                } while (idKnihy == null);
                if (this.skontrolujUkoncenieMetody(idKnihy)) {
                    break;
                }

                hladaj.hladajKnihu(idKnihy);
                break;
            case "2":
                hladaj = new HladajPodlaMena();

                nazovKnihyVstup = new MoznostNaVyberVMenu("Zadaj nazov knihy: ");
                nazovKnihy = nazovKnihyVstup.getVyberMoznosti();
                if (this.skontrolujUkoncenieMetody(nazovKnihy)) {
                    break;
                }

                hladaj.hladajKnihu(nazovKnihy);
                break;
            case "3":
                hladaj = new HladajPodlaAutora();

                menoAutoraVstup = new MoznostNaVyberVMenu("Zadaj meno Autora knihy: ");
                menoAutora = menoAutoraVstup.getVyberMoznosti();
                if (this.skontrolujUkoncenieMetody(menoAutora)) {
                    break;
                }

                hladaj.hladajKnihu(menoAutora);
                break;
            case "4":
                hladaj = new HladajPodlaZanru();

                nazovZanruVstup = new MoznostNaVyberVMenu("Zadaj nazov zanra: ");
                nazovZanru = nazovZanruVstup.getVyberMoznosti();
                if (this.skontrolujUkoncenieMetody(nazovZanru)) {
                    break;
                }

                hladaj.hladajKnihu(nazovZanru);
                break;
            default:
                System.out.println("Tuto moznost nepoznam, zadal si ju spravne?");
                break;
        }
    }
}
