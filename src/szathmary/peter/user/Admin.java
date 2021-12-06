package szathmary.peter.user;

import szathmary.peter.databaza.DatabazaControl;
import szathmary.peter.databaza.exceptions.KnihaJePozicanaException;
import szathmary.peter.databaza.exceptions.KnihaNeexistujeException;
import szathmary.peter.databaza.exceptions.KnihaNieJePozicanaException;
import szathmary.peter.databaza.exceptions.NemasPravomocException;
import szathmary.peter.databaza.exceptions.PouzivatelNeexistujeException;
import szathmary.peter.databaza.exceptions.PouzivatelNevratilVsetkyKnihyException;
import szathmary.peter.databaza.exceptions.PrekrocenyMaximalnyLimitPozicanychKnihException;
import szathmary.peter.databaza.exceptions.TakytoLoginUzExistujeException;
import szathmary.peter.databaza.search.HladajPodlaAutora;
import szathmary.peter.databaza.search.HladajPodlaDruhuUctu;
import szathmary.peter.databaza.search.HladajPodlaId;
import szathmary.peter.databaza.search.HladajPodlaMena;
import szathmary.peter.databaza.search.HladajPodlaVeku;
import szathmary.peter.databaza.search.HladajPodlaZanru;
import szathmary.peter.databaza.search.IHladajKnihu;
import szathmary.peter.databaza.search.IHladajPouzivatela;
import szathmary.peter.riadenieStavuAplikacie.STAVYAPLIKACIE;

import java.util.Scanner;

/**
 * Objekt pouzivatela s druhom uctu admin
 */
public class Admin extends DefaultPouzivatel {

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
    public Admin(String druhUctu, String meno, String priezvisko, String id, int vek, String login, int pozicaneKnihyPocet) {
        super(druhUctu, meno, priezvisko, id, vek, login, pozicaneKnihyPocet);
    }

    /**
     * Konstruktor admina s parametrami v kontajnerovej triede UdajeOUzivatelovi
     *
     * @param udajeOUzivatelovi Udaje o pouzivatelovi zabalene v triede UdajeOUzivatelovi
     */
    public Admin(UdajeOUzivatelovi udajeOUzivatelovi) {
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

        System.out.println("\n-------------------------------------------------");
        System.out.println("1. Vypis vsetky knihy");
        System.out.println("2. Pozicaj si/niekomu knihu");
        System.out.println("3. Hladaj knihu/pouzivatela");
        System.out.println("4. Vrat svoju/niekoho knihu");
        System.out.println("5. Pridaj knihu do systému");
        System.out.println("6. Odstráň knihu zo systému");
        System.out.println("7. Vypis knihy, ktore je potrebne coskoro vratit");
        System.out.println("8. Pridaj používateľa do systému");
        System.out.println("9. Odstráň používateľa zo systému");
        System.out.println("10. Vypíš všetkých používateľov");
        System.out.println("11. Odhlasit sa");
        System.out.println("0. Ukonci aplikaciu");
        System.out.println("-------------------------------------------------");
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
    public STAVYAPLIKACIE vykonajVybranuMoznost(String vyber, DatabazaControl db) {
        String druhUctuPouzivatela;
        String menoPouzivatela;
        String priezviskoPouzivatela;
        String vekPouzivatela;
        String loginPouzivatela = null;
        String hesloPouzivatela;

        MoznostNaVyberVMenu vekPouzivatelaVstup;
        MoznostNaVyberVMenu druhUctuPouzivatelaVstup;
        MoznostNaVyberVMenu menoPouzivatelaVstup;
        MoznostNaVyberVMenu priezviskoPouzivatelaVstup;
        MoznostNaVyberVMenu loginPouzivatelaVstup;
        MoznostNaVyberVMenu hesloPouzivatelaVstup;

        String nazovKnihy;
        String autorKnihy;
        String idKnihy;
        String pocetStranKnihy;
        String zanerKnihy;

        MoznostNaVyberVMenu nazokKnihyVstup;
        MoznostNaVyberVMenu autorKnihyVstup;
        MoznostNaVyberVMenu zanerKnihyVstup;
        MoznostNaVyberVMenu idKnihyVstup;
        MoznostNaVyberVMenu pocetStranKnihyVstup;

        switch (vyber) {
            case "0" -> {
                System.out.println("Aplikacia ukoncena!");
                System.exit(0);
            }
            case "1" -> {
                try {
                    db.vypisVsetkyKnihy();
                } catch (NemasPravomocException e) {
                    System.out.println("Na tuto akciu nemas pravomoci!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "2" -> {
                do {
                    idKnihyVstup = new MoznostNaVyberVMenu("Zadaj ID knihy:");
                    idKnihy = idKnihyVstup.getVyberMoznostiCislo();
                } while (idKnihy == null);
                if (this.skontrolujUkoncenieMetody(idKnihy)) {
                    return STAVYAPLIKACIE.MENU;
                }
                do {
                    vekPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj ID pouzivatela:");
                    vekPouzivatela = vekPouzivatelaVstup.getVyberMoznostiCislo();
                } while (vekPouzivatela == null);
                if (this.skontrolujUkoncenieMetody(vekPouzivatela)) {
                    return STAVYAPLIKACIE.MENU;
                }
                try {
                    db.pozicajKnihu(idKnihy, null, vekPouzivatela);
                } catch (PrekrocenyMaximalnyLimitPozicanychKnihException e) {
                    System.out.println("Prekrocil si limit 15 pozicanych knih!");
                    System.out.println("Tuto knihu si zadany pouzivatel nemoze pozicat!");
                } catch (KnihaJePozicanaException exception) {
                    System.out.println("Tato kniha uz je pozicana. Vyber si inu.");
                } catch (KnihaNeexistujeException exception) {
                    System.out.println("Tato kniha neexistuje!");
                } catch (PouzivatelNeexistujeException e) {
                    System.out.println("Pouzivatel s tymto ID neexistuje!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "3" -> {
                System.out.println("Co chces vyhladat?");
                String vybranaMoznost;
                int pocitadloKolNacitanejMoznosti = 0;
                do {
                    if (pocitadloKolNacitanejMoznosti > 0) {
                        System.out.println("Zadal si nespravnu moznost, skus to znovu!");
                    }
                    vybranaMoznost = this.vypisANacitajMoznostVyhladavania();
                    pocitadloKolNacitanejMoznosti++;
                } while (!(vybranaMoznost.equals("1") || vybranaMoznost.equals("2") || vybranaMoznost.equals("0")));
                if (this.skontrolujUkoncenieMetody(vybranaMoznost)) {
                    return STAVYAPLIKACIE.MENU;
                }
                String vybranaMonznostHladanehoObjektu;
                String[] rozdelenyString;
                int pocitadloPoctuKolHladanehoObjektu = 0;
                do {
                    if (pocitadloPoctuKolHladanehoObjektu > 0) {
                        System.out.println("Zadal si nespravnu moznost, skus to znovu!");
                    }
                    vybranaMonznostHladanehoObjektu = this.vypisANacitajMoznostKVybranemuObjektuVyhladavania(vybranaMoznost);
                    rozdelenyString = vybranaMonznostHladanehoObjektu.split(" ");
                    pocitadloPoctuKolHladanehoObjektu++;
                } while (!(rozdelenyString[1].equals("0") || rozdelenyString[1].equals("1") ||
                        rozdelenyString[1].equals("2") || rozdelenyString[1].equals("3") ||
                        rozdelenyString[1].equals("4")));
                this.hladaj(vybranaMonznostHladanehoObjektu, db);
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
                do {
                    vekPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj ID pouzivatela:");
                    vekPouzivatela = vekPouzivatelaVstup.getVyberMoznostiCislo();
                } while (vekPouzivatela == null);
                if (this.skontrolujUkoncenieMetody(vekPouzivatela)) {
                    return STAVYAPLIKACIE.MENU;
                }
                try {
                    db.vratKnihu(idKnihy, null, vekPouzivatela);
                } catch (KnihaNieJePozicanaException e) {
                    System.out.println("Tuto knihu nema zadany pouzivatel pozicanu, nemoze ju vratit!");
                } catch (KnihaNeexistujeException e) {
                    System.out.println("Tato kniha neexistuje!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "5" -> {
                nazokKnihyVstup = new MoznostNaVyberVMenu("Zadaj nazov knihy");
                nazovKnihy = nazokKnihyVstup.getVyberMoznosti();
                if (this.skontrolujUkoncenieMetody(nazovKnihy)) {
                    return STAVYAPLIKACIE.MENU;
                }
                autorKnihyVstup = new MoznostNaVyberVMenu("Zadaj autora knihy");
                autorKnihy = autorKnihyVstup.getVyberMoznosti();
                if (this.skontrolujUkoncenieMetody(autorKnihy)) {
                    return STAVYAPLIKACIE.MENU;
                }
                zanerKnihyVstup = new MoznostNaVyberVMenu("Zadaj zaner knihy");
                zanerKnihy = zanerKnihyVstup.getVyberMoznosti();
                if (this.skontrolujUkoncenieMetody(zanerKnihy)) {
                    return STAVYAPLIKACIE.MENU;
                }
                do {
                    pocetStranKnihyVstup = new MoznostNaVyberVMenu("Zadaj pocet stran knihy:");
                    pocetStranKnihy = pocetStranKnihyVstup.getVyberMoznostiCislo();
                } while (pocetStranKnihy == null);
                if (this.skontrolujUkoncenieMetody(pocetStranKnihy)) {
                    return STAVYAPLIKACIE.MENU;
                }
                try {
                    db.pridajKnihu(nazovKnihy, autorKnihy, zanerKnihy, Integer.parseInt(pocetStranKnihy), 0);
                } catch (NemasPravomocException e) {
                    System.out.println("Na tuto akciu nemas pravomoci!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "6" -> {
                do {
                    idKnihyVstup = new MoznostNaVyberVMenu("Zadaj ID knihy:");
                    idKnihy = idKnihyVstup.getVyberMoznostiCislo();
                } while (idKnihy == null);
                if (this.skontrolujUkoncenieMetody(idKnihy)) {
                    return STAVYAPLIKACIE.MENU;
                }
                try {
                    db.odstranKnihu(idKnihy);
                } catch (KnihaNeexistujeException e) {
                    System.out.println("Tato kniha neexistuje!");
                    System.out.println("Skus to znova!");
                } catch (KnihaJePozicanaException exception) {
                    System.out.println("Kniha je pozicana!");
                    System.out.println("Nemozes ju odstranit zo systemu!");
                } catch (NemasPravomocException exception) {
                    System.out.println("Na tuto akciu nemas pravomoci!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "7" -> {
                db.vypisKnihyKtoreTrebaOchviluVratit(this.getId());
                return STAVYAPLIKACIE.MENU;
            }
            case "8" -> {
                int pocetKolZadavaniaDruhuUctu = 0;
                do {
                    if (pocetKolZadavaniaDruhuUctu != 0) {
                        System.out.println("Musis zadat bud 'admin' alebo 'zakaznik'");
                    }
                    druhUctuPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj druh uctu pouzivatela - admin / zakaznik");
                    druhUctuPouzivatela = druhUctuPouzivatelaVstup.getVyberMoznosti();
                    pocetKolZadavaniaDruhuUctu++;
                } while (!(druhUctuPouzivatela.equals("admin") || druhUctuPouzivatela.equals("zakaznik") || druhUctuPouzivatela.equals("0")));
                if (this.skontrolujUkoncenieMetody(druhUctuPouzivatela)) {
                    return STAVYAPLIKACIE.MENU;
                }
                menoPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj meno pouzivatela");
                menoPouzivatela = menoPouzivatelaVstup.getVyberMoznosti();
                if (this.skontrolujUkoncenieMetody(menoPouzivatela)) {
                    return STAVYAPLIKACIE.MENU;
                }
                priezviskoPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj priezvisko pouzivatela");
                priezviskoPouzivatela = priezviskoPouzivatelaVstup.getVyberMoznosti();
                if (this.skontrolujUkoncenieMetody(priezviskoPouzivatela)) {
                    return STAVYAPLIKACIE.MENU;
                }
                do {
                    vekPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj vek pouzivatela:");
                    vekPouzivatela = vekPouzivatelaVstup.getVyberMoznostiCislo();
                } while (vekPouzivatela == null);
                if (this.skontrolujUkoncenieMetody(vekPouzivatela)) {
                    return STAVYAPLIKACIE.MENU;
                }
                boolean kontrola = false;
                while (!kontrola) {
                    try {
                        loginPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj prihlasovacie meno pouzivatela");
                        loginPouzivatela = loginPouzivatelaVstup.getVyberMoznosti();
                        if (this.skontrolujUkoncenieMetody(loginPouzivatela)) {
                            return STAVYAPLIKACIE.MENU;
                        }
                        db.skontrolujCiExistujeTakyLogin(loginPouzivatela);
                        kontrola = true;
                    } catch (TakytoLoginUzExistujeException e) {
                        System.out.println("Pouzivatel s takymto loginom uz existuje!");
                        System.out.println("Zadaj iny login!");
                    }
                }
                hesloPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj heslo pouzivatela: ");
                hesloPouzivatela = hesloPouzivatelaVstup.getVyberMoznosti();
                if (this.skontrolujUkoncenieMetody(hesloPouzivatela)) {
                    return STAVYAPLIKACIE.MENU;
                }
                int hesloPouzivatelaVHashi = hesloPouzivatela.hashCode();
                UdajeOUzivatelovi udajeOUzivatelovi = new UdajeOUzivatelovi(druhUctuPouzivatela,
                        menoPouzivatela, priezviskoPouzivatela, "0", Integer.parseInt(vekPouzivatela), 0,
                        loginPouzivatela,
                        String.valueOf(hesloPouzivatelaVHashi));
                try {
                    db.pridajUzivatela(udajeOUzivatelovi);
                } catch (NemasPravomocException exception) {
                    System.out.println("Na tuto akciu nemas pravomoci!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "9" -> {
                do {
                    vekPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj ID pouzivatela:");
                    vekPouzivatela = vekPouzivatelaVstup.getVyberMoznostiCislo();
                } while (vekPouzivatela == null);
                if (this.skontrolujUkoncenieMetody(vekPouzivatela)) {
                    return STAVYAPLIKACIE.MENU;
                }
                try {
                    db.odstranPouzivatela(vekPouzivatela);
                } catch (PouzivatelNeexistujeException exception) {
                    System.out.println("Pouzivatel s tymto ID neexistuje!");
                } catch (PouzivatelNevratilVsetkyKnihyException exception) {
                    System.out.println("Pouzivatel este nevratil vsetky knihy, nemozes ho odstranit zo systemu!");
                    System.out.println("Skus to znovu neskor!");
                } catch (NemasPravomocException exception) {
                    System.out.println("Na tuto akciu nemas pravomoci!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "10" -> {
                try {
                    db.vypisPouzivatelov();
                } catch (NemasPravomocException exception) {
                    System.out.println("Na tuto akciu nemas pravomoci!");
                }
                return STAVYAPLIKACIE.MENU;
            }
            case "11" -> {
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
     * Vypise a nacita cislo vybranej moznosti pre vyhladavanie
     *
     * @param vstup Cislo vybraneho objektu pre vyhladavanie
     * @return Cislo vybranej moznosti
     */
    private String vypisANacitajMoznostKVybranemuObjektuVyhladavania(String vstup) {
        Scanner sc = new Scanner(System.in);
        switch (vstup) {
            case "1" -> {
                System.out.println("\n----------------------------");
                System.out.println("1. Hladaj podla ID");
                System.out.println("2. Hladaj podla priezviska");
                System.out.println("3. Hladaj podla veku");
                System.out.println("4. Hladaj podla druhu uctu");
                System.out.println("0. Vrat sa do menu");
                System.out.println("------------------------------");
                System.out.print("> ");
                return "pouzivatel " + sc.nextLine();
            }
            case "2" -> {
                System.out.println("\n-------------------------");
                System.out.println("1. Hladaj podla ID");
                System.out.println("2. Hladaj podla nazvu");
                System.out.println("3. Hladaj podla autora");
                System.out.println("4. Hladaj podla zanru");
                System.out.println("0. Vrat sa do menu");
                System.out.println("--------------------------");
                System.out.print("> ");
                return "kniha " + sc.nextLine();
            }
            default -> {
                System.out.println("Zadana nespravna moznost!");
                System.out.println("Skus to znova!");
                return "zla moznost";
            }
        }
    }

    /**
     * Vypise a nacita cislo objektu, ktory chce pouzivatel hladat
     *
     * @return Cislo vybraneho objektu na hladanie
     */
    @Override
    protected String vypisANacitajMoznostVyhladavania() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n----------------");
        System.out.println("1. Pouzivatel");
        System.out.println("2. Kniha");
        System.out.println("0. Vrat sa do menu");
        System.out.println("----------------");

        return sc.nextLine();
    }

    /**
     * Na zaklade vybranej moznosti na hladanie vyhlada a vypise informacie o najdenych objektoch
     *
     * @param vybranaMoznost Cislo vybranej moznosti
     * @param db             Objekt na komunikaciu s konkretnou databazou
     */
    @Override
    protected void hladaj(String vybranaMoznost, DatabazaControl db) {
        String[] vybranaMoznostRozdelena = vybranaMoznost.split(" ");

        IHladajKnihu hladajKnihu;
        IHladajPouzivatela hladajPouzivatela;

        MoznostNaVyberVMenu idPouzivatelaVstup;
        MoznostNaVyberVMenu priezviskoPouzivatelaVstup;
        MoznostNaVyberVMenu vekPouzivatelaVstup;
        MoznostNaVyberVMenu druhUctuPouzivatelaVstup;
        MoznostNaVyberVMenu idKnihyVstup;
        MoznostNaVyberVMenu nazovKnihyVstup;
        MoznostNaVyberVMenu menoAutoraVstup;
        MoznostNaVyberVMenu nazovZanruVstup;

        String idPouzivatela;
        String priezviskoPouzivatela;
        String vekPouzivatela;
        String druhUctuPouzivatela;
        String idKnihy;
        String nazovKnihy;
        String menoAutora;
        String nazovZanru;

        switch (vybranaMoznostRozdelena[0]) {
            case "pouzivatel":
                switch (vybranaMoznostRozdelena[1]) {
                    case "1" -> {
                        hladajPouzivatela = new HladajPodlaId();
                        do {
                            idPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj ID pouzivatela: ");
                            idPouzivatela = idPouzivatelaVstup.getVyberMoznostiCislo();
                        } while (idPouzivatela == null);
                        if (this.skontrolujUkoncenieMetody(idPouzivatela)) {
                            break;
                        }
                        hladajPouzivatela.hladajPouzivatela(idPouzivatela);
                    }
                    case "2" -> {
                        hladajPouzivatela = new HladajPodlaMena();
                        priezviskoPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj priezvisko pouzivatela: ");
                        priezviskoPouzivatela = priezviskoPouzivatelaVstup.getVyberMoznosti();
                        if (this.skontrolujUkoncenieMetody(priezviskoPouzivatela)) {
                            break;
                        }
                        hladajPouzivatela.hladajPouzivatela(priezviskoPouzivatela);
                    }
                    case "3" -> {
                        hladajPouzivatela = new HladajPodlaVeku();
                        do {
                            vekPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj vek pouzivatela: ");
                            vekPouzivatela = vekPouzivatelaVstup.getVyberMoznostiCislo();
                        } while (vekPouzivatela == null);
                        if (this.skontrolujUkoncenieMetody(vekPouzivatela)) {
                            break;
                        }
                        hladajPouzivatela.hladajPouzivatela(vekPouzivatela);
                    }
                    case "4" -> {
                        hladajPouzivatela = new HladajPodlaDruhuUctu();
                        druhUctuPouzivatelaVstup = new MoznostNaVyberVMenu("Zadaj druh uctu pouzivatela: admin / zakaznik ");
                        druhUctuPouzivatela = druhUctuPouzivatelaVstup.getVyberMoznosti();
                        if (this.skontrolujUkoncenieMetody(druhUctuPouzivatela)) {
                            break;
                        }
                        hladajPouzivatela.hladajPouzivatela(druhUctuPouzivatela);
                    }
                    default -> System.out.println("Tuto moznost nepoznam, zadal si ju spravne?");
                }
                break;
            case "kniha":
                switch (vybranaMoznostRozdelena[1]) {
                    case "1" -> {
                        hladajKnihu = new HladajPodlaId();
                        do {
                            idKnihyVstup = new MoznostNaVyberVMenu("Zadaj ID knihy:");
                            idKnihy = idKnihyVstup.getVyberMoznostiCislo();
                        } while (idKnihy == null);
                        if (this.skontrolujUkoncenieMetody(idKnihy)) {
                            break;
                        }
                        hladajKnihu.hladajKnihu(idKnihy);
                    }
                    case "2" -> {
                        hladajKnihu = new HladajPodlaMena();
                        nazovKnihyVstup = new MoznostNaVyberVMenu("Zadaj nazov knihy: ");
                        nazovKnihy = nazovKnihyVstup.getVyberMoznosti();
                        if (this.skontrolujUkoncenieMetody(nazovKnihy)) {
                            break;
                        }
                        hladajKnihu.hladajKnihu(nazovKnihy);
                    }
                    case "3" -> {
                        hladajKnihu = new HladajPodlaAutora();
                        menoAutoraVstup = new MoznostNaVyberVMenu("Zadaj meno Autora knihy: ");
                        menoAutora = menoAutoraVstup.getVyberMoznosti();
                        if (this.skontrolujUkoncenieMetody(menoAutora)) {
                            break;
                        }
                        hladajKnihu.hladajKnihu(menoAutora);
                    }
                    case "4" -> {
                        hladajKnihu = new HladajPodlaZanru();
                        nazovZanruVstup = new MoznostNaVyberVMenu("Zadaj nazov zanra: ");
                        nazovZanru = nazovZanruVstup.getVyberMoznosti();
                        if (this.skontrolujUkoncenieMetody(nazovZanru)) {
                            break;
                        }
                        hladajKnihu.hladajKnihu(nazovZanru);
                    }
                }
                break;
            case "0":
                break;

        }
    }

    /**
     * Skontroluje, ci si pouzivatel nevybral cislo 0 - cize ukoncenie aktualnej akcie a vratenie sa do menu
     *
     * @param kontrolovanyVstup Vybrane cislo na kontrolu
     * @return vrati True, ak je vybrane cislo 0, inac vrati False
     */
    private boolean skontrolujUkoncenieMetody(String kontrolovanyVstup) {
        return kontrolovanyVstup.equals("0");
    }
}
