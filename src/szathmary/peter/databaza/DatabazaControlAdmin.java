package szathmary.peter.databaza;

import org.jetbrains.annotations.NotNull;
import szathmary.peter.databaza.exceptions.KnihaJePozicanaException;
import szathmary.peter.databaza.exceptions.KnihaNeexistujeException;
import szathmary.peter.databaza.exceptions.KnihaNieJePozicanaException;
import szathmary.peter.databaza.exceptions.PouzivatelNeexistujeException;
import szathmary.peter.databaza.exceptions.PouzivatelNevratilVsetkyKnihyException;
import szathmary.peter.databaza.exceptions.PrekrocenyMaximalnyLimitPozicanychKnihException;
import szathmary.peter.user.DefaultPouzivatel;
import szathmary.peter.user.UdajeOUzivatelovi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Sluzi na komunikaciu s databazou pre druhUctu admin
 */
public class DatabazaControlAdmin extends DatabazaControl {

    /**
     * Maximalny povoleny pocet pozicanych knih pre jedneho uzivatela
     */
    private static final int MAXIMALNYPOCETPOZICANYCHKNIH = 15;

    /**
     * Url link na databazu
     */
    private static final String URLNADATABAZU = "jdbc:mysql://localhost:3306/kniznica?allowMultiQueries=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";

    /**
     * prihlasovacie heslo pouzivatela do databazy
     */
    private static final String MENODODATABAZY = "peto";

    /**
     * heslo na prihlasenie pouzivatela do databazy
     */
    private static final String HESLODODATABAZY = "1234";

    /**
     * Prida knihu do databazy
     *
     * @param nazovKnihy nazov knihy, ktoru chceme pridat
     * @param autor      autor knihy, ktoru chceme pridat
     * @param zaner      zaner knihy, ktoru chceme pridat
     * @param pocetStran pocet stran knihy, ktoru chceme pridat
     * @param jePozicana ukazovatel, co je kniha prave pozicana
     */
    @Override
    public void pridajKnihu(@NotNull String nazovKnihy, @NotNull String autor, @NotNull String zaner, int pocetStran, int jePozicana) {

        String sql = "INSERT INTO knihy (nazov, autor, zaner, pocet_stran, jePozicana) VALUES(?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, nazovKnihy);
            pst.setString(2, autor);
            pst.setString(3, zaner);
            pst.setString(4, String.valueOf(pocetStran));
            pst.setString(5, String.valueOf(jePozicana));
            pst.executeUpdate();

            System.out.format("\nUspesne si pridal knihu %s\n", nazovKnihy);

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }

    /**
     * Odstrani knihu z databazy
     *
     * @param idKnihy ID knihy, ktora sa ma odstranit zo systemu
     * @throws KnihaNeexistujeException nastane, ak chceme odstranit knihu, ktora neexistuje
     * @throws KnihaJePozicanaException nastane, ak je kniha, ktoru chceme odstranit, stale pozicana
     */
    @Override
    public void odstranKnihu(String idKnihy) throws KnihaNeexistujeException, KnihaJePozicanaException {

        String sql = "DELETE FROM knihy WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, idKnihy);
            if (this.jeIdVTabulkeDatabazy(idKnihy, "knihy")) {
                if (this.jeKnihaPozicana(idKnihy)) {
                    throw new KnihaJePozicanaException("Kniha je pozicana!");
                }
                pst.executeUpdate();
                System.out.format("\nUspesne si odstranil knihu s ID %s", idKnihy);
            } else {
                throw new KnihaNeexistujeException("Kniha Neexistuje");
            }

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }

    /**
     * Vytvori sql query string na vypisanie vsetkych informacii o vsetkych knihach
     */
    @Override
    public void vypisVsetkyKnihy() {
        String sql = "SELECT * FROM knihy";
        VypisInfo.vypisInfoOKnihe("*", sql);
    }

    /**
     * Odstrani pouzivatelovi knihu zo slotu pozicanych knih, zmensi pocet pozicanych knih o 1
     *
     * @param idKnihy       ID knihy, ktoru chceme vratit
     * @param pouzivatel    objekt pouzivatela, ktory chce knihu vratit
     * @param idPouzivatela ID pouzivatela, ktory chce knihu vratit
     * @throws KnihaNieJePozicanaException nastane, ak chceme pouzivatelovi vratit knihu, ktoru nema pozicanu
     */
    @Override
    public void vratKnihu(String idKnihy, DefaultPouzivatel pouzivatel, String idPouzivatela) throws KnihaNieJePozicanaException, KnihaNeexistujeException {

        if (!this.jeIdVTabulkeDatabazy(idKnihy, "knihy")) {
            throw new KnihaNeexistujeException("Tato kniha neexistuje!");
        }

        if (!this.jeKnihaPozicana(idKnihy)) {
            throw new KnihaNieJePozicanaException("Tuto knihu nemas pozicanu, nemozes ju vratit!");
        }
        pouzivatel = this.getPouzivatelPodlaId(idPouzivatela);
        if (this.maPouzivatelPozicanuTutoKnihu(pouzivatel.getId(), idKnihy)) {
            this.zmenStavDostupnostiKnihy(idKnihy, "0");
            this.odstranPozicanuKnihuPouzivatelovi(idKnihy, pouzivatel.getId());
            this.zmenPocetPozicanychKnihPouzivatela(pouzivatel, "-1");
        } else {
            throw new KnihaNieJePozicanaException("Tuto knihu nemas pozicanu, nemozes ju vratit!");
        }
    }

    /**
     * Prida uzivatela do databazi
     *
     * @param udajeOUzivatelovi objekt udajov o uzivatelovi, ktoreho chcem pridat do databazy
     */
    @Override
    public void pridajUzivatela(UdajeOUzivatelovi udajeOUzivatelovi) {
        String sql = "INSERT INTO pouzivatelia (meno, priezvisko, login, heslo, druhUctu, vek, pozicaneKnihyPocet, " +
                "pozicanaKniha1, pozicanaKniha2, pozicanaKniha3, pozicanaKniha4, pozicanaKniha5, pozicanaKniha6, " +
                "pozicanaKniha7, pozicanaKniha8, pozicanaKniha9, pozicanaKniha10, pozicanaKniha11, pozicanaKniha12," +
                " pozicanaKniha13, pozicanaKniha14, pozicanaKniha15)" +
                " VALUES(?, ?, ?, ?, ?, ?, 0, null, null, null, null, null, null, null, null, null, null, " +
                "null, null, null, null, null)";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, udajeOUzivatelovi.getMeno());
            pst.setString(2, udajeOUzivatelovi.getPriezvisko());
            pst.setString(3, udajeOUzivatelovi.getLogin());
            pst.setString(4, udajeOUzivatelovi.getHeslo());
            pst.setString(5, udajeOUzivatelovi.getDruhUctu());
            pst.setString(6, String.valueOf(udajeOUzivatelovi.getVek()));

            pst.executeUpdate();

            System.out.format("Uspesne si pridal pouzivatela %s %s s pravami %s", udajeOUzivatelovi.getMeno(),
                    udajeOUzivatelovi.getPriezvisko(), udajeOUzivatelovi.getDruhUctu());

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }

    /**
     * Odstrani pouzivatela z databazi
     *
     * @param idPouzivatela ID pouzivatela, ktoreho chceme odstranit z databazy
     * @throws PouzivatelNeexistujeException          nastane, ak chceme odstranit pouzivatela, ktory neexistuje
     * @throws PouzivatelNevratilVsetkyKnihyException nastane, ak chceme odstranit pouzivatela, ktory este nevratil vsetky knihy
     */
    @Override
    public void odstranPouzivatela(String idPouzivatela) throws PouzivatelNeexistujeException, PouzivatelNevratilVsetkyKnihyException {
        String sql = "DELETE FROM pouzivatelia WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            if (!this.jeIdVTabulkeDatabazy(idPouzivatela, "pouzivatelia")) {
                throw new PouzivatelNeexistujeException("Pouzivatel s tymto ID neexistuje");
            }

            pst.setString(1, idPouzivatela);
            if (this.getPocetPozicanychKnihPouzivatela(idPouzivatela) != 0) {
                throw new PouzivatelNevratilVsetkyKnihyException("Pouzivatel nevratil este vsetky knihy!");
            }

            pst.executeUpdate();
            System.out.format("\nUspesne si odstranil pouzivatela s ID %s", idPouzivatela);

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }

    /**
     * Vytvori sql query string na vypisanie vsetkych informacii o vsetkych pouzivateloch
     */
    @Override
    public void vypisPouzivatelov() {
        String sql = "SELECT * FROM pouzivatelia";
        VypisInfo.vypisInfoOPouzivatelovi("*", sql);
    }

    /**
     * Prida knihu pouzivatelovi do volneho slotu na pozicane knihy, zvysi mu pocet pozicanych knih o 1
     *
     * @param idKnihy       ID knihy, ktory si chce pouzivatel pozicat
     * @param pouzivatel    objekt pouzivatela, ktory si chce pozicat knihu
     * @param idPouzivatela ID pouzivatela, ktory si chce pozicat knihu
     * @throws PrekrocenyMaximalnyLimitPozicanychKnihException nastane, ak pouzivatel prekrocil maximalny povoleny pocet pozicanych knih
     * @throws KnihaJePozicanaException                        nastane, ak si pouzivatel chce pozicat knihu, ktora uz je pozicana
     */
    @Override
    public void pozicajKnihu(String idKnihy, DefaultPouzivatel pouzivatel, String idPouzivatela) throws PrekrocenyMaximalnyLimitPozicanychKnihException, KnihaJePozicanaException, PouzivatelNeexistujeException, KnihaNeexistujeException {
        String sql = "SELECT * FROM knihy WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, idKnihy);

            if (!this.jeIdVTabulkeDatabazy(idKnihy, "knihy")) {
                throw new KnihaNeexistujeException("Kniha s tymto ID neexistuje!");
            }
            if (!this.jeIdVTabulkeDatabazy(idPouzivatela, "pouzivatelia")) {
                throw new PouzivatelNeexistujeException("Pouzivatel s tymto ID neexistuje!");
            }
            if (this.getPocetPozicanychKnihPouzivatela(idPouzivatela) >= MAXIMALNYPOCETPOZICANYCHKNIH) {
                throw new PrekrocenyMaximalnyLimitPozicanychKnihException("Bol prekroceny maximalny pocet pozicanych knih!");
            }
            ResultSet rs = pst.executeQuery();
            rs.next();
            if (this.jeKnihaPozicana(idKnihy)) {
                throw new KnihaJePozicanaException("Tato kniha uz je pozicana!");
            } else {
                String id = rs.getString("id");
                String nazov = rs.getString("nazov");

                this.pridajPozicanuKnihuPouzivatelovi(idKnihy, idPouzivatela);
                this.zmenPocetPozicanychKnihPouzivatela(this.getPouzivatelPodlaId(idPouzivatela), "1");
                System.out.format("Uspesne si si pozical knihu %s s id %s", nazov, id);
                this.zmenStavDostupnostiKnihy(idKnihy, "1");
            }

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }
}
