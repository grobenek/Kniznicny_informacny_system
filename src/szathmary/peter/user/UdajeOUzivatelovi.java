package szathmary.peter.user;

import java.util.List;

/**
 * Kontajnerova trieda na udaje o pouzivatelovi
 */
public class UdajeOUzivatelovi {
    private final String druhUctu;
    private final String meno;
    private final String priezvisko;
    private final String id;
    private final int vek;
    private final int pocetPozicanychKnih;
    private final String login;
    private final String heslo;

    /**
     * Konstruktor triedy s volnymi parametrami (nie v kontajneri)
     *
     * @param druhUctu            druh uctu pouzivatela
     * @param meno                meno pouzivatela
     * @param priezvisko          priezvisko pouzivatela
     * @param id                  id pouzivatela
     * @param vek                 vek pouzivatela
     * @param pocetPozicanychKnih pocet pozicanych knih pouzivatela
     * @param login               prihlasovacie meno pouzivatela
     * @param heslo               heslo pouzivatela
     */
    public UdajeOUzivatelovi(String druhUctu, String meno, String priezvisko, String id, int vek, int pocetPozicanychKnih,
                             String login, String heslo) {
        this.druhUctu = druhUctu;
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.id = id;
        this.vek = vek;
        this.pocetPozicanychKnih = pocetPozicanychKnih;
        this.login = login;
        this.heslo = heslo;
    }

    /**
     * Konstruktor admina s parametrami v Liste
     *
     * @param parametre List naplneny udajmi, ktore chceme uchovat v tejto triede
     */
    public UdajeOUzivatelovi(List<String> parametre) {
        this.druhUctu = parametre.get(0);
        this.meno = parametre.get(1);
        this.priezvisko = parametre.get(2);
        this.id = parametre.get(3);
        this.vek = Integer.parseInt(parametre.get(4));
        this.pocetPozicanychKnih = Integer.parseInt(parametre.get(5));
        this.login = parametre.get(6);
        this.heslo = parametre.get(7);
    }

    public String getLogin() {
        return this.login;
    }

    public String getHeslo() {
        return this.heslo;
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

    public int getPocetPozicanychKnih() {
        return this.pocetPozicanychKnih;
    }


}
