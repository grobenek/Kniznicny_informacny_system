package szathmary.peter.user;

import szathmary.peter.databaza.DatabazaControl;
import szathmary.peter.riadenieStavuAplikacie.STAVYAPLIKACIE;
import szathmary.peter.user.exceptions.VstupNieJeCisloException;
import szathmary.peter.user.exceptions.ZleVybranaMoznostException;

/**
 * Abstraktna trieda pre pouzivatela
 */
public abstract class DefaultPouzivatel {
    private final String druhUctu;
    private final String meno;
    private final String priezvisko;
    private final String id;
    private final int vek;
    private final String login;
    private int pozicaneKnihyPocet;

    /**
     * Konstruktor admina s volnymi parametrami (nie v kontajneri)
     *
     * @param druhUctu            druh uctu pouzivatela
     * @param meno                meno pouzivatela
     * @param priezvisko          priezvisko pouzivatela
     * @param id                  id pouzivatela
     * @param vek                 vek pouzivatela
     * @param login               prihlasovacie meno pouzivatela
     * @param pocetPozicanychKnih pocet pozicanych knih pouzivatela
     */
    public DefaultPouzivatel(String druhUctu, String meno, String priezvisko, String id, int vek, String login, int pocetPozicanychKnih) {
        this.druhUctu = druhUctu;
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.id = id;
        this.vek = vek;
        this.login = login;
        this.pozicaneKnihyPocet = pocetPozicanychKnih;
    }

    /**
     * Konstruktor admina s parametrami v kontajnerovej triede UdajeOUzivatelovi
     *
     * @param udajeOUzivatelovi Udaje o pouzivatelovi zabalene v triede UdajeOUzivatelovi
     */
    public DefaultPouzivatel(UdajeOUzivatelovi udajeOUzivatelovi) {
        this.druhUctu = udajeOUzivatelovi.getDruhUctu();
        this.meno = udajeOUzivatelovi.getMeno();
        this.priezvisko = udajeOUzivatelovi.getPriezvisko();
        this.id = udajeOUzivatelovi.getId();
        this.vek = udajeOUzivatelovi.getVek();
        this.pozicaneKnihyPocet = udajeOUzivatelovi.getPocetPozicanychKnih();
        this.login = udajeOUzivatelovi.getLogin();
    }

    public String getDruhUctu() {
        return this.druhUctu;
    }

    public String getMeno() {
        return this.meno;
    }

    public String getPriezvisko() {
        return this.priezvisko;
    }

    public String getId() {
        return this.id;
    }

    public int getVek() {
        return this.vek;
    }

    public int getPozicaneKnihyPocet() {
        return this.pozicaneKnihyPocet;
    }

    public String getLogin() {
        return this.login;
    }

    /**
     * Abstraktna metoda pre vypisanie a nacitanie moznosti na vykonanie
     *
     * @return vrati Cislo nacitanej moznosti
     */
    public abstract String vypisANacitajMoznost();

    /**
     * Abstraktna metoda pre vykonanie vybranej moznosti
     *
     * @param vstup Cislo vybranej moznosti
     * @param db    Objekt na komunikaciu s konkretnou databazou
     * @return vrati STAVAPIKACIE na zaklade vykonanej akcie
     */
    public abstract STAVYAPLIKACIE vykonajVybranuMoznost(String vstup, DatabazaControl db);

    /**
     * Zmeni pocet pozicanych knih pouzivatela
     *
     * @param kolko O kolko sa ma zmenit pocet pozicanych knih
     */
    public void zmenPocetPozicanychKnih(int kolko) {
        this.pozicaneKnihyPocet += kolko;
    }

    /**
     * Vypise a nacita cislo moznosti pre vyhladavanie
     *
     * @return Cislo nacitanej moznosti
     */
    protected abstract String vypisANacitajMoznostVyhladavania();

    /**
     * Na zaklade vybranej moznosti na hladanie vyhlada a vypise informacie o najdenych objektoch
     *
     * @param vybranaMoznost Cislo vybranej moznosti
     * @param db             Objekt na komunikaciu s konkretnou databazou
     * @throws ZleVybranaMoznostException nastane, ak si pouzivatel vyberie neznamu moznost
     * @throws VstupNieJeCisloException   nastaane, ak si pouzivatel nevyberie cislo
     */
    protected abstract void hladaj(String vybranaMoznost, DatabazaControl db) throws ZleVybranaMoznostException, VstupNieJeCisloException;
}
