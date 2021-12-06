package szathmary.peter.databaza;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import szathmary.peter.databaza.exceptions.ChybaVCasoPriestoreException;
import szathmary.peter.databaza.exceptions.KnihaJePozicanaException;
import szathmary.peter.databaza.exceptions.KnihaNeexistujeException;
import szathmary.peter.databaza.exceptions.KnihaNieJePozicanaException;
import szathmary.peter.databaza.exceptions.NemasPravomocException;
import szathmary.peter.databaza.exceptions.PouzivatelNeexistujeException;
import szathmary.peter.databaza.exceptions.PouzivatelNevratilVsetkyKnihyException;
import szathmary.peter.databaza.exceptions.PrekrocenyMaximalnyLimitPozicanychKnihException;
import szathmary.peter.databaza.exceptions.TakytoLoginUzExistujeException;
import szathmary.peter.user.Admin;
import szathmary.peter.user.DefaultPouzivatel;
import szathmary.peter.user.UdajeOUzivatelovi;
import szathmary.peter.user.Zakaznik;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Sluzi na komunikaciu s databazou bez ohladu na druh uctu pouzivatelia
 */
public abstract class DatabazaControl {

    /**
     * Maximalny povoleny pocet pozicanych knih pre jedneho uzivatela
     */
    private static final int MAXIMALNYPOCETPOZICANYCHKNIH = 15;

    /**
     * Cas na vratenie knihy v dnoch
     */
    private static final int CASVDNOCHNAVRATENIEKNIHY = 1;

    /**
     * Url link na databazu
     */
    private static final String URLNADATABAZU = "jdbc:mysql://localhost:3306/kniznica?allowMultiQueries=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    /**
     * prihlasovacie meno pouzivatela do databazy
     */
    private static final String MENODODATABAZY = "peto";
    /**
     * heslo na prihlasenie pouzivatela do databazy
     */
    private static final String HESLODODATABAZY = "1234";

    /**
     * Abstraktna metoda pre pridanie knihy do databazy
     *
     * @param nazovKnihy nazov knihy, ktoru chceme pridat
     * @param autor      autor knihy, ktoru chceme pridat
     * @param zaner      zaner knihy, ktoru chceme pridat
     * @param pocetStran pocet stran knihy, ktoru chceme pridat
     * @param jePozicana ukazovatel, co je kniha prave pozicana
     * @throws NemasPravomocException nastane, ak sa nespravny druh uctu snazi pridat knihu do systemu
     */
    public abstract void pridajKnihu(String nazovKnihy, String autor, String zaner, int pocetStran, int jePozicana) throws NemasPravomocException;

    /**
     * Abstraktna metoda pre odstranenie knihy zo systemu
     *
     * @param idKnihy ID knihy, ktora sa ma odstranit zo systemu
     * @throws KnihaNeexistujeException nastane, ak kniha, ktoru chceme odstranit, neexistuje
     * @throws KnihaJePozicanaException nastane, ak kniha, ktoru chceme odstranit, je prave pozicana
     * @throws NemasPravomocException   nastane, ak sa nespravny druh uctu snazi odstranit knihu zo systemu
     */
    public abstract void odstranKnihu(String idKnihy) throws KnihaNeexistujeException, KnihaJePozicanaException, NemasPravomocException;

    /**
     * Vypise vsetky informacie k nihach, ktore nie su pozicane
     */
    public void vypisDostupneKnihy() {
        String sql = "SELECT * FROM knihy WHERE jePozicana = 0";
        VypisInfo.vypisInfoOKnihe("*", sql);
    }

    /**
     * Abstraktna metoda pre vypisanie informacii o vsetkych knihach
     */
    public abstract void vypisVsetkyKnihy() throws NemasPravomocException;

    /**
     * Skontroluje, ci sa zadane meno a heslo zhoduje s nejakym pouzivatelom v databaze
     *
     * @param meno  login, ktory sa porovnava s ostatnymi v databaze
     * @param heslo zahashovane heslo, ktore sa porovnava s ostatnymi v databaze
     * @return ak sa nasla zhoda, vrati TRUE, inac vrati FALSE
     */
    public boolean kontrolujLogin(String meno, int heslo) {
        String sql = "Select * from pouzivatelia Where LOWER(login)='" + meno + "' and LOWER(heslo)='" + heslo + "'";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (CommunicationsException ex) {
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("! Spojenie s databazou sa nepodarilo nadviazat !");
            System.err.println("! ---------------------------------------------!");
            System.err.println("! Skus to znovu neskor, alebo kontaktuj admina !");
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.exit(1);
        } catch (SQLException throwables) {
            System.out.println("Pozor, nastala chyba!");
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * Zisti a vrati informacie o pouzivatelovi na zaklade jeho mena a hesla
     *
     * @param meno  login, na zaklade ktoreho sa vyhlada pouzivatel
     * @param heslo heslo, na zaklade ktoreho sa vyhlada pouzivatel
     * @return ak sa nasla zhoda, vrati ArrayList naplneny udajmi o pouzivatelovi v podobe stringov, ak sa nenasla, vrati null
     */
    public ArrayList<String> zistiDruhAInformacieOUcte(String meno, int heslo) {
        String sql = "Select * from pouzivatelia Where LOWER(login)='" + meno + "' and LOWER(heslo)='" + heslo + "'";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                ArrayList<String> zoznamParametrov = new ArrayList<>();
                String druhUctu = rs.getString("druhUctu");
                zoznamParametrov.add(druhUctu);
                String menoPouzivatela = rs.getString("meno");
                zoznamParametrov.add(menoPouzivatela);
                String priezvisko = rs.getString("priezvisko");
                zoznamParametrov.add(priezvisko);
                String id = rs.getString("id");
                zoznamParametrov.add(id);
                String vek = rs.getString("vek");
                zoznamParametrov.add(vek);
                int pozicaneKnihy = rs.getInt("pozicaneKnihyPocet");
                zoznamParametrov.add(String.valueOf(pozicaneKnihy));
                String loginUzivatela = rs.getString("login");
                zoznamParametrov.add(loginUzivatela);
                String hesloUzivatela = rs.getString("heslo");
                zoznamParametrov.add(hesloUzivatela);

                return zoznamParametrov;
            }

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Zisti, ci sa zadana ID nachadza v tabulke, ktorej meno zadame
     *
     * @param id           ID, ktore chceme skontrolovat v tabulke
     * @param nazovTabulky nazov tabulky v ktorej chceme ID kontrolovat
     * @return ak sa ID v tabulke nachadza, vrati TRUE, ak nie, vrati FALSE
     */
    protected boolean jeIdVTabulkeDatabazy(String id, String nazovTabulky) {
        String sql = "SELECT * FROM " + nazovTabulky + " WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();

            return rs.next();

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Zisti, ci je kniha so zadanym ID pozicana
     *
     * @param idKnihy ID knihy, ktoru chceme skontrolovat
     * @return ak je kniha pozicana, vrati TRUE, inak vrati FALSE
     */
    protected boolean jeKnihaPozicana(String idKnihy) {
        String sql = "SELECT jePozicana FROM knihy WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, idKnihy);
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getString("jePozicana").equals("1");

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Zmeni stav dostupnosti knihy na dostupna (0), alebo pozicana (1)
     *
     * @param idKnihy ID knihy, ktorej stav dostupnosti chceme zmenit
     * @param stav    Stav, na ktory chceme stav knihy zmenit (0 alebo 1)
     */
    protected void zmenStavDostupnostiKnihy(String idKnihy, String stav) {
        String sql = "UPDATE knihy SET jePozicana = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, stav);
            pst.setString(2, idKnihy);
            pst.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }

    /**
     * Prehlada sloty na pozicane knihy a ak najde prazdny, zapise don knihu aj s casom pozicania
     *
     * @param idKnihy       ID knihy, ktoru chcem pouzivatelovi pridat ako pozicanu
     * @param idPouzivatela ID pouzivatelia, ktoremu chceme knihu ako pozicanu pridat
     */
    protected void pridajPozicanuKnihuPouzivatelovi(String idKnihy, String idPouzivatela) {
        String nazovColumnu = this.ktoryColumnPozicanaKnihaJeVolny(idPouzivatela);
        String sql = "UPDATE pouzivatelia SET " + nazovColumnu + " = ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(2, idPouzivatela);
            String nazovKnihy = this.zistiMenoKnihy(idKnihy);
            if (nazovKnihy != null) {
                LocalDate datum = LocalDate.now();
                nazovKnihy = idKnihy + " " + nazovKnihy + " ~`" + datum;
                pst.setString(1, nazovKnihy);
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }

    /**
     * Vypise knihy, ktore musi pouzivatel ochvilu vratit
     *
     * @param idPouzivatela ID pouzivatela, ktoreho sa vypis tyka
     */
    public void vypisKnihyKtoreTrebaOchviluVratit(String idPouzivatela) {
        int pocitadloVypisov = 0;
        String pozicanaKniha;
        for (int cisloColumnuPozicanaKniha = 1; cisloColumnuPozicanaKniha < 16; cisloColumnuPozicanaKniha++) {
            pozicanaKniha = "pozicanaKniha" + cisloColumnuPozicanaKniha;
            String sql = "SELECT " + pozicanaKniha + " FROM pouzivatelia WHERE id = ?";
            try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY)) {
                PreparedStatement pst = con.prepareStatement(sql);

                pst.setString(1, idPouzivatela);
                ResultSet rs = pst.executeQuery();
                List<String> zoznamDatumov = new ArrayList<>();
                while (rs.next()) {
                    if (rs.getString(1) == null) {
                        zoznamDatumov.add(null);
                        continue;
                    }
                    String obsahColumnu = rs.getString(pozicanaKniha);
                    String[] poleObsahu;
                    poleObsahu = obsahColumnu.split("~`");
                    zoznamDatumov.add(poleObsahu[1]);
                    List<String> skontrolovaneDatumy = this.porovnajDatumyNaVratenieKnih(zoznamDatumov);

                    SimpleDateFormat formaterDatumu = new SimpleDateFormat("yyyy-MM-dd");
                    for (String datum : skontrolovaneDatumy) {
                        if (datum == null) {
                            continue;
                        }
                        String[] rozdelenyDatum = datum.split("~");
                        Date datumZListu = formaterDatumu.parse(rozdelenyDatum[0]);

                        switch (rozdelenyDatum[1]) {
                            case "0":
                                break;
                            case "-1":
                                Date aktualnyDatum = new Date();
                                long rozdielMedziDatumamiVMiliSekundach = aktualnyDatum.getTime() - datumZListu.getTime();
                                TimeUnit time = TimeUnit.DAYS;
                                long rozdielMedziDatumamiVDnoch = time.convert(rozdielMedziDatumamiVMiliSekundach, TimeUnit.MILLISECONDS);

                                if ((CASVDNOCHNAVRATENIEKNIHY - rozdielMedziDatumamiVDnoch) <= 3 && (CASVDNOCHNAVRATENIEKNIHY - rozdielMedziDatumamiVDnoch) > 0) {
                                    String obsahColumnuKdeJeKniha = this.getPozicanaKnihaZColumnu(cisloColumnuPozicanaKniha, idPouzivatela);
                                    if (obsahColumnuKdeJeKniha == null) {
                                        continue;
                                    }
                                    pocitadloVypisov++;
                                    String[] poleObsahuColumnu = obsahColumnuKdeJeKniha.split("~`");
                                    String nazovKnihy = poleObsahuColumnu[0].replace(Character.toString(poleObsahuColumnu[0].charAt(0)), "");
                                    long casNaVratenieKnihyVDnoch = CASVDNOCHNAVRATENIEKNIHY - rozdielMedziDatumamiVDnoch;
                                    System.out.format("Pozor, mas uz len %d dni na vratenie knihy%s%n", casNaVratenieKnihyVDnoch, nazovKnihy);
                                } else if ((CASVDNOCHNAVRATENIEKNIHY - rozdielMedziDatumamiVDnoch) < 0) {
                                    String obsahColumnuKdeJeKniha = this.getPozicanaKnihaZColumnu(cisloColumnuPozicanaKniha, idPouzivatela);
                                    if (obsahColumnuKdeJeKniha == null) {
                                        continue;
                                    }
                                    pocitadloVypisov++;
                                    String[] poleObsahuColumnu = obsahColumnuKdeJeKniha.split("~`");
                                    long casNaVratenieKnihyVDnoch = CASVDNOCHNAVRATENIEKNIHY - rozdielMedziDatumamiVDnoch;
                                    String nazovKnihy = poleObsahuColumnu[0].replace(Character.toString(poleObsahuColumnu[0].charAt(0)), "");
                                    System.out.format("Pozor, meskas uz %d dni na vratenie knihy%s%n", casNaVratenieKnihyVDnoch * -1, nazovKnihy);
                                }
                        }
                    }
                }
            } catch (SQLException | ParseException throwables) {
                throwables.printStackTrace();
            }
        }
        if (pocitadloVypisov == 0) {
            System.out.println("Vsetko je v poriadku. Ziadne knihy netreba v najblizsej dobe vratit.");
        }
    }

    /**
     * Vrati meno knihy ulozenej v columne
     *
     * @param ktoryColumnPozicanaKniha cislo columnu pozicane knihy, z ktoreho chceme knihu
     * @param idPouzivatela            ID pouzivatela, ktoreho columny chceme kontrolovat
     * @return vrati meno knihy ak sa tam kniha nachadza, inac vrati null
     */
    private String getPozicanaKnihaZColumnu(int ktoryColumnPozicanaKniha, String idPouzivatela) {
        String pozicanaKniha = "pozicanaKniha" + ktoryColumnPozicanaKniha;
        String sql = "SELECT " + pozicanaKniha + " FROM pouzivatelia WHERE id = ?";

        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, idPouzivatela);
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getString(pozicanaKniha);
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Pozor! Nastala chyba!");
        }
        return null;
    }

    /**
     * Porovnava Aktualny datum so zaznamenanym datum pri pozicani knihy
     *
     * @param zoznamDatumov List naplneny datumami na kontrolu
     * @return vrati List naplneny Stringami vo formate "datumZoZanznamu~ukazatel". Ukazatel je 0 ak su to rovnake datumy,
     * *       1 ak je zo zaznamu dalej ako aktualny, -1 ak je zo zaznamu pred aktualnym
     */
    private List<String> porovnajDatumyNaVratenieKnih(List<String> zoznamDatumov) {
        List<String> vystup = new ArrayList<>();
        for (String aktualnyZaznam : zoznamDatumov) {
            SimpleDateFormat formaterDatumu = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
            Date datumAktualny = new Date();
            Date datumZoZaznamu;
            try {
                datumZoZaznamu = formaterDatumu.parse(aktualnyZaznam);
                int ukazatel = datumZoZaznamu.compareTo(datumAktualny);

                String ideDoVystupu;
                switch (ukazatel) {
                    case 0 -> vystup.add(null);
                    case -1 -> {
                        String datumNaVystupVStringu = formaterDatumu.format(datumZoZaznamu);
                        ideDoVystupu = datumNaVystupVStringu + "~-1";
                        vystup.add(ideDoVystupu);
                    }
                    case 1 -> throw new ChybaVCasoPriestoreException("Nachadzas sa v minulosti?!");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return vystup;
    }

    /**
     * Zmeni pocet pozicanych knih o tolko, kolko je zadane
     *
     * @param pouzivatel Objekt pouzivatela, ktoremu chceme zmenit pocet pozicanych knih
     * @param kolko      String o kolko chceme zmenit stav pozicanych knih
     */
    protected void zmenPocetPozicanychKnihPouzivatela(DefaultPouzivatel pouzivatel, String kolko) {
        String sql = "UPDATE pouzivatelia SET pozicaneKnihyPocet = pozicaneKnihyPocet + ? WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, kolko);
            pst.setString(2, pouzivatel.getId());
            pouzivatel.zmenPocetPozicanychKnih(Integer.parseInt(kolko));
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }

    /**
     * Odstrani pouzivatelovi knihu z databazy
     *
     * @param idKnihy       ID knihy, ktoru chceme odstranit
     * @param idPouzivatela ID pouzivatela, ktoremu chceme knihu odstranit
     */
    protected void odstranPozicanuKnihuPouzivatelovi(String idKnihy, String idPouzivatela) {
        String nazovColumnu = this.kdeMaPouzivatelPozicanuTutoKnihu(idPouzivatela, idKnihy);
        String sql = "UPDATE pouzivatelia SET " + nazovColumnu + " = null WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, idPouzivatela);
            String nazovKnihy = this.zistiMenoKnihy(idKnihy);
            if (nazovKnihy != null) {
                System.out.println("Kniha uspesne vratena");
                pst.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }

    /**
     * Prechadza vsetkymi slotami pre knihy a ak je nejaky volny, vrati jeho nazov
     *
     * @param idPouzivatela ID pouzivatela, ktoremu chceme najst prazdny slot pre knihu
     * @return ak najde prazdny slot, vrati jeho nazov, inac vrati null
     */
    private String ktoryColumnPozicanaKnihaJeVolny(String idPouzivatela) {
        String sql = "SELECT pozicanaKniha1, pozicanaKniha2, pozicanaKniha3, " +
                "pozicanaKniha4, pozicanaKniha5, pozicanaKniha6, pozicanaKniha7, " +
                "pozicanaKniha8, pozicanaKniha9, pozicanaKniha10, pozicanaKniha11, " +
                "pozicanaKniha12, pozicanaKniha13, pozicanaKniha14, pozicanaKniha15 FROM pouzivatelia WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, idPouzivatela);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String pozicanaKniha;
                for (int i = 1; i < MAXIMALNYPOCETPOZICANYCHKNIH + 1; i++) {
                    try {
                        pozicanaKniha = rs.getString(i);
                        if (pozicanaKniha == null) {
                            return "pozicanaKniha" + i;
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Prechadza vsetkymi slotmi a zistuje, ci sa tam nachadza kniha so zadanym ID
     *
     * @param idPouzivatela ID pouzivatela, u ktoreho chceme kontrolovat knihu
     * @param idKnihy       ID knihy, ktoru chceme kontrolovat
     * @return ak ma pouzivatel knihu pozicanu, vrati TRUE, inac FALSE
     */
    protected boolean maPouzivatelPozicanuTutoKnihu(String idPouzivatela, String idKnihy) {
        String sql = "SELECT pozicanaKniha1, pozicanaKniha2, pozicanaKniha3, " +
                "pozicanaKniha4, pozicanaKniha5, pozicanaKniha6, pozicanaKniha7, " +
                "pozicanaKniha8, pozicanaKniha9, pozicanaKniha10, pozicanaKniha11, " +
                "pozicanaKniha12, pozicanaKniha13, pozicanaKniha14, pozicanaKniha15 FROM pouzivatelia WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, idPouzivatela);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String pozicanaKniha;
                for (int i = 1; i < MAXIMALNYPOCETPOZICANYCHKNIH + 1; i++) {
                    try {
                        if (rs.getString(i) != null) {
                            int indexBodky = rs.getString(i).indexOf(" ");
                            pozicanaKniha = rs.getString(i).substring(0, indexBodky);
                        } else {
                            continue;
                        }
                        if (!pozicanaKniha.equals(idKnihy)) {
                            continue;
                        }
                        return true;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * Prehladava sloty na knihy a vrati nazov slotu kde sa kniha nachadza
     *
     * @param idPouzivatela ID pouzivatela, ktoremu chceme kontrolovat umiestenie knihy
     * @param idKnihy       ID knihy, ktorej umiestnenie chceme kontrolovat
     * @return ak sa najde kniha v slote, vrati sa jeho nazov, inac vrati null
     */
    protected String kdeMaPouzivatelPozicanuTutoKnihu(String idPouzivatela, String idKnihy) {
        String sql = "SELECT pozicanaKniha1, pozicanaKniha2, pozicanaKniha3, " +
                "pozicanaKniha4, pozicanaKniha5, pozicanaKniha6, pozicanaKniha7, " +
                "pozicanaKniha8, pozicanaKniha9, pozicanaKniha10, pozicanaKniha11, " +
                "pozicanaKniha12, pozicanaKniha13, pozicanaKniha14, pozicanaKniha15 FROM pouzivatelia WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, idPouzivatela);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String pozicanaKniha;
                for (int i = 1; i < MAXIMALNYPOCETPOZICANYCHKNIH + 1; i++) {
                    try {
                        if (rs.getString(i) != null) {
                            int indexBodky = rs.getString(i).indexOf(" ");
                            pozicanaKniha = rs.getString(i).substring(0, indexBodky);
                        } else {
                            continue;
                        }
                        if (pozicanaKniha.equals(idKnihy)) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            return rsmd.getColumnName(i);
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * Zisti meno knihy podla jej ID
     *
     * @param idKnihy ID knihy, ktorej nazov chceme zistit
     * @return ak sa meno najde, vrati ho, inac vrati null
     */
    protected String zistiMenoKnihy(String idKnihy) {
        String sql = "SELECT nazov FROM knihy WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, idKnihy);
            ResultSet rs = pst.executeQuery();
            rs.next();
            return rs.getString("nazov");


        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Zisti a vrati objekt uzivatela podla jeho ID
     *
     * @param id ID pouzivatela, ktoreho objekt chceme dostat
     * @return Ak sa pouzivatel s danym ID najde, vrati jeho objekt, inac vrati null
     */
    protected DefaultPouzivatel getPouzivatelPodlaId(String id) {
        String sql = "SELECT * FROM pouzivatelia WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();

            String druhUctu = rs.getString("druhUctu");
            String meno = rs.getString("meno");
            String priezvisko = rs.getString("priezvisko");
            //id
            int vek = Integer.parseInt(rs.getString("vek"));
            String login = rs.getString("login");
            int pozicaneKnihyPocet = Integer.parseInt(rs.getString("pozicaneKnihyPocet"));

            if (druhUctu.equals("zakaznik")) {
                return new Zakaznik(druhUctu, meno, priezvisko, id, vek, login, pozicaneKnihyPocet);
            } else {
                return new Admin(druhUctu, meno, priezvisko, id, vek, login, pozicaneKnihyPocet);
            }

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Kontroluje,ci uz takyto login existuje
     *
     * @param loginNaKontrolu login, ktory chceme skontrolovat
     * @throws TakytoLoginUzExistujeException nastane, ak sa najde rovnaky login
     */
    public void skontrolujCiExistujeTakyLogin(String loginNaKontrolu) throws TakytoLoginUzExistujeException {
        String sql = "SELECT * FROM pouzivatelia WHERE login = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, loginNaKontrolu);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                throw new TakytoLoginUzExistujeException("Takyto login uz existuje!");
            }

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }

    /**
     * Zisti a vrati pocet pozicanych knih pouzivatela
     *
     * @param idPouzivatela ID pouzivatela, ktoreho pocet pozicanych knih chceme zistit
     * @return vrati pocet pozicanych knih pouzivatela, ak nastane chyba, vrati 16 (viac ako maximalny povoleny pocet pozicanych knih)
     */
    protected int getPocetPozicanychKnihPouzivatela(String idPouzivatela) throws PouzivatelNeexistujeException {
        String sql = "SELECT pozicaneKnihyPocet FROM pouzivatelia WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, idPouzivatela);

            if (!this.jeIdVTabulkeDatabazy(idPouzivatela, "pouzivatelia")) {
                throw new PouzivatelNeexistujeException("Pouzivatel s tymto ID neexistuje!");
            }

            ResultSet rs = pst.executeQuery();

            rs.next();

            return Integer.parseInt(rs.getString("pozicaneKnihyPocet"));

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();

            return 16;
        }
    }

    /**
     * Zisti a vypise vsetky pozicane knihy pouzivatela
     *
     * @param idPouzivatela ID pouzivatela, ktoreho pozicane knihy chceme vypisat
     */
    public void vypisPozicaneKnihyPouzivatela(String idPouzivatela) {
        String pozicanaKniha;
        int pocitadlo = 0;
        System.out.println("******************************************************************************************************");
        for (int i = 1; i < MAXIMALNYPOCETPOZICANYCHKNIH + 1; i++) {
            pozicanaKniha = "pozicanaKniha" + i;
            String sql = "SELECT " + pozicanaKniha + " FROM pouzivatelia WHERE id = ?";
            try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
                 PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, idPouzivatela);

                ResultSet rs = pst.executeQuery();
                rs.next();
                String idKnihy;
                if (rs.getString(pozicanaKniha) != null) {
                    int indexBodky = rs.getString(pozicanaKniha).indexOf(" ");
                    idKnihy = rs.getString(pozicanaKniha).substring(0, indexBodky);

                } else {
                    continue;
                }
                ArrayList<String> parametre = this.zistiInfoOKnihe(idKnihy);
                if (parametre == null) {
                    continue;
                }
                System.out.format("%s. nazov: %s, autor: %s, zaner: %s, pocet stran: %s\n",
                        parametre.get(0),
                        parametre.get(1),
                        parametre.get(2),
                        parametre.get(3),
                        parametre.get(4));
                pocitadlo++;

            } catch (SQLException ex) {
                System.out.println("Pozor! Nastala chyba!");
                ex.printStackTrace();
            }
        }
        if (pocitadlo == 0) {
            System.out.println("Nemas ziadne pozicane knihy");
        }
        System.out.println("******************************************************************************************************");
    }

    /**
     * Zisti a vrati info o knihe podla zadaneho ID
     *
     * @param idKnihy ID knihy, ktorej info chceme dostat
     * @return vrati ArrayList naplneny informaciami o knihe, podla zadaneho ID, ak sa kniha nenajde, vrati null
     */
    protected ArrayList<String> zistiInfoOKnihe(String idKnihy) {
        String sql = "SELECT * FROM knihy WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, idKnihy);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                ArrayList<String> zoznamParametrov = new ArrayList<>();
                String id = rs.getString("id");
                zoznamParametrov.add(id);
                String nazov = rs.getString("nazov");
                zoznamParametrov.add(nazov);
                String autor = rs.getString("autor");
                zoznamParametrov.add(autor);
                String zaner = rs.getString("zaner");
                zoznamParametrov.add(zaner);
                String pocetStran = rs.getString("pocet_stran");
                zoznamParametrov.add(pocetStran);
                return zoznamParametrov;
            }


        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();

        }
        return null;
    }

    /**
     * Odstrani pouzivatelovi knihu zo slotu pozicanych knih, zmensi pocet pozicanych knih o 1
     *
     * @param idKnihy       ID knihy, ktoru chceme vratit
     * @param pouzivatel    objekt pouzivatela, ktory chce knihu vratit
     * @param idPouzivatela ID pouzivatela, ktory chce knihu vratit
     * @throws KnihaNieJePozicanaException nastane, ak pouzivatel chce vratit knihu, ktoru nema pozicanu
     */
    public abstract void vratKnihu(String idKnihy, DefaultPouzivatel pouzivatel, String idPouzivatela) throws KnihaNieJePozicanaException, KnihaNeexistujeException;

    /**
     * Prida uzivatela do databazy
     *
     * @param udajeOUzivatelovi objekt udajov o uzivatelovi, ktoreho chcem pridat do databazy
     * @throws NemasPravomocException nastane, ak sa nepovoleny pouzivatel pokusi pridat osobu do databazy
     */
    public abstract void pridajUzivatela(UdajeOUzivatelovi udajeOUzivatelovi) throws NemasPravomocException;

    /**
     * Odstrani pouzivatela z databazy
     *
     * @param idPouzivatela ID pouzivatela, ktoreho chceme odstranit z databazy
     * @throws PouzivatelNeexistujeException          nastane, ak chceme odstranit pouzivatela, ktory neexistuje
     * @throws PouzivatelNevratilVsetkyKnihyException nastane, ak chceme odstranit pouzivatela, ktory este nevratil vsetky knihy
     * @throws NemasPravomocException                 nastane, ak sa nepovoleny pouzivatel pokusi odstranit osobu z databazy
     */
    public abstract void odstranPouzivatela(String idPouzivatela) throws PouzivatelNeexistujeException, PouzivatelNevratilVsetkyKnihyException, NemasPravomocException;

    /**
     * Vypise informacie o vsetkych pouzivateloch
     *
     * @throws NemasPravomocException nastane, ak sa nepovoleny pouzivatel pokusi vypisat informacie o vsetkych pouzivateloch
     */
    public abstract void vypisPouzivatelov() throws NemasPravomocException;

    /**
     * Prida knihu ako pozicanu do volneho slotu a zvacsi pouzivatelovi pocet pozicanych knih
     *
     * @param idKnihy       ID knihy, ktory si chce pouzivatel pozicat
     * @param pouzivatel    objekt pouzivatela, ktory si chce pozicat knihu
     * @param idPouzivatela ID pouzivatela, ktory si chce pozicat knihu
     * @throws PrekrocenyMaximalnyLimitPozicanychKnihException nastane, ked si chce pouzivatel pozicat knihu, ale prekrocil pocet povolenych pozicanych knih
     * @throws KnihaJePozicanaException                        nastane, ak si pouzivatel chce pozicat uz pozicanu knihu
     * @throws KnihaNeexistujeException                        nastane, ak si pouzivatel chce pozicat knihu, ktora neexistuje
     */
    public abstract void pozicajKnihu(String idKnihy, DefaultPouzivatel pouzivatel, String idPouzivatela) throws PrekrocenyMaximalnyLimitPozicanychKnihException, KnihaJePozicanaException, KnihaNeexistujeException, PouzivatelNeexistujeException;

}