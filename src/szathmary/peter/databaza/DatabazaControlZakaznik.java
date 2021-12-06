package szathmary.peter.databaza;

import szathmary.peter.databaza.exceptions.KnihaJePozicanaException;
import szathmary.peter.databaza.exceptions.KnihaNeexistujeException;
import szathmary.peter.databaza.exceptions.KnihaNieJePozicanaException;
import szathmary.peter.databaza.exceptions.NemasPravomocException;
import szathmary.peter.databaza.exceptions.PouzivatelNeexistujeException;
import szathmary.peter.databaza.exceptions.PrekrocenyMaximalnyLimitPozicanychKnihException;
import szathmary.peter.user.DefaultPouzivatel;
import szathmary.peter.user.UdajeOUzivatelovi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Sluzi na komunikaciu s databazou pre druh uctu zakaznik
 */
public class DatabazaControlZakaznik extends DatabazaControl {

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
     * Zakaznik nemoze pridat knihu do databazy
     *
     * @param nazovKnihy nazov knihy, ktoru chceme pridat
     * @param autor      autor knihy, ktoru chceme pridat
     * @param zaner      zaner knihy, ktoru chceme pridat
     * @param pocetStran pocet stran knihy, ktoru chceme pridat
     * @param jePozicana ukazovatel, co je kniha prave pozicana
     * @throws NemasPravomocException nastane, ak sa pouzivatel s druhom uctu zakaznik snazi pridat
     */
    @Override
    public void pridajKnihu(String nazovKnihy, String autor, String zaner, int pocetStran, int jePozicana) throws NemasPravomocException {
        throw new NemasPravomocException("Na tuto operaciu nemas pravomoci!");
    }

    /**
     * Zakaznik nemoze odstranit knihu z databazy
     *
     * @param idKnihy ID knihy, ktora sa ma odstranit zo systemu
     * @throws NemasPravomocException nastane, ak sa pouzivatel s druhom uctu zakaznik snazi odstranit
     */
    @Override
    public void odstranKnihu(String idKnihy) throws NemasPravomocException {
        throw new NemasPravomocException("Na tuto operaciu nemas pravomoci!");
    }

    /**
     * Zakaznik nemoze vypisat informacie o vsetkych knihach
     *
     * @throws NemasPravomocException nastane, ak sa pouzivatel s druhom uctu zakaznik snazi vypisat informacie o vsetkych knihach
     */
    @Override
    public void vypisVsetkyKnihy() throws NemasPravomocException {
        throw new NemasPravomocException("Na tuto operaciu nemas pravomoci!");
    }

    /**
     * Odstrani pouzivatelovi knihu zo slotu pozicanych knih, zmensi pocet pozicanych knih o 1
     *
     * @param idKnihy       ID knihy, ktoru chceme vratit
     * @param pouzivatel    objekt pouzivatela, ktory chce knihu vratit
     * @param idPouzivatela ID pouzivatela, ktory chce knihu vratit
     * @throws KnihaNieJePozicanaException nastane, ak sa pouzivatel snazi vratit knihu, ktora nie je pozicana
     */
    @Override
    public void vratKnihu(String idKnihy, DefaultPouzivatel pouzivatel, String idPouzivatela) throws KnihaNieJePozicanaException, KnihaNeexistujeException {

        if (!this.jeIdVTabulkeDatabazy(idKnihy, "knihy")) {
            throw new KnihaNeexistujeException("Tato kniha neexistuje!");
        }

        if (!this.jeKnihaPozicana(idKnihy)) {
            throw new KnihaNieJePozicanaException("Tuto knihu nemas pozicanu, nemozes ju vratit!");
        }
        if (this.maPouzivatelPozicanuTutoKnihu(pouzivatel.getId(), idKnihy)) {
            this.zmenStavDostupnostiKnihy(idKnihy, "0");
            this.odstranPozicanuKnihuPouzivatelovi(idKnihy, pouzivatel.getId());
            this.zmenPocetPozicanychKnihPouzivatela(pouzivatel, "-1");
        } else {
            throw new KnihaNieJePozicanaException("Tuto knihu nemas pozicanu, nemozes ju vratit!");
        }
    }

    /**
     * Zakaznik nemoze pridat uzivatela do databazy
     *
     * @param udajeOUzivatelovi objekt udajov o uzivatelovi, ktoreho chcem pridat do databazy
     * @throws NemasPravomocException nastane, ak sa pouzivatel s druhom uctu zakaznik snazi pridat pouzivatela do databazy
     */
    @Override
    public void pridajUzivatela(UdajeOUzivatelovi udajeOUzivatelovi) throws NemasPravomocException {
        throw new NemasPravomocException("Na tuto operaciu nemas pravomoci!");
    }

    /**
     * Zakaznik nemoze odstranit pouzivatela z databazy
     *
     * @param idPouzivatela ID pouzivatela, ktoreho chceme odstranit z databazy
     * @throws NemasPravomocException nastane, ak sa pouzivatel s druhom uctu zakaznik snazi odstranit pouzivatela do databazy
     */
    @Override
    public void odstranPouzivatela(String idPouzivatela) throws NemasPravomocException {
        throw new NemasPravomocException("Na tuto operaciu nemas pravomoci!");
    }

    /**
     * Zakaznik nemoze vypisat informacie o pouzivateloch
     *
     * @throws NemasPravomocException nastane, ak sa pouzivatel s druhom uctu zakaznik snazi vypisat informacie o pouzivateloch
     */
    @Override
    public void vypisPouzivatelov() throws NemasPravomocException {
        throw new NemasPravomocException("Na tuto operaciu nemas pravomoci!");
    }

    /**
     * Prida knihu pouzivatelovi do volneho slotu na pozicane knihy, zvysi mu pocet pozicanych knih o 1
     *
     * @param idKnihy       ID knihy, ktory si chce pouzivatel pozicat
     * @param pouzivatel    objekt pouzivatela, ktory si chce pozicat knihu
     * @param idPouzivatela ID pouzivatela, ktory si chce pozicat knihu
     * @throws KnihaNeexistujeException                        nastane, ak si chce pouzivatel pozicat knihu, ktora neexistuje
     * @throws PrekrocenyMaximalnyLimitPozicanychKnihException nastane, ak pouzivatel prekrocil maximalny povoleny pocet pozicanych knih
     * @throws KnihaJePozicanaException                        nastane, ak si pouzivatel chce pozicat knihu, ktora uz je pozicana
     */
    @Override
    public void pozicajKnihu(String idKnihy, DefaultPouzivatel pouzivatel, String idPouzivatela) throws KnihaNeexistujeException, PrekrocenyMaximalnyLimitPozicanychKnihException, KnihaJePozicanaException, PouzivatelNeexistujeException {
        String sql = "SELECT * FROM knihy WHERE id = ?";
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, idKnihy);

            if (!this.jeIdVTabulkeDatabazy(idKnihy, "knihy")) {
                throw new KnihaNeexistujeException("Tato kniha neexistuje!");
            }

            if (this.getPocetPozicanychKnihPouzivatela(pouzivatel.getId()) >= MAXIMALNYPOCETPOZICANYCHKNIH) {
                throw new PrekrocenyMaximalnyLimitPozicanychKnihException("Prekrocil si maximalny limit pozicanych knih, dalsiu si nemozes pozicat!");
            }
            ResultSet rs = pst.executeQuery();
            rs.next();
            if (this.jeKnihaPozicana(idKnihy)) {
                throw new KnihaJePozicanaException("Tato kniha uz je pozicana. Vyber si inu.");
            } else {
                String id = rs.getString("id");
                String nazov = rs.getString("nazov");

                this.pridajPozicanuKnihuPouzivatelovi(idKnihy, pouzivatel.getId());
                this.zmenPocetPozicanychKnihPouzivatela(pouzivatel, "1");
                System.out.format("Uspesne si si pozical knihu %s s id %s", nazov, id);
                this.zmenStavDostupnostiKnihy(idKnihy, "1");
            }

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            System.out.println("Si si isty ze si zadal spravne ID knihy?");
        }
    }
}
