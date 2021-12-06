package szathmary.peter.databaza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Sluzi na vypisanie vsetkych informacii z databazi, podla sql string query
 */
public class VypisInfo {
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
     * Vypise vsetky informacie o knihe, podla kriterii v sql query stringu
     *
     * @param vstup kriterium, ktore sa vlozi na miesto "?" v sql query stringu
     * @param sql   sql string, na zaklade ktoreho sa maju vyhladat a vypisat informacie o knihach
     */
    public static void vypisInfoOKnihe(String vstup, String sql) {
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {
            if (!vstup.equals("*")) {
                pst.setString(1, vstup);
            }

            ResultSet rs = pst.executeQuery();

            System.out.println("\n");
            int pocitadloVysledkov = 0;
            System.out.println("******************************************************************************************************");
            while (rs.next()) {
                System.out.format("%d. nazov: %s, autor: %s, zaner: %s, pocet stran: %d, je pozicana: %d\n",
                        rs.getInt("id"),
                        rs.getString("nazov"),
                        rs.getString("autor"),
                        rs.getString("zaner"),
                        rs.getInt("pocet_stran"),
                        rs.getInt("jePozicana"));
                pocitadloVysledkov++;
            }
            if (pocitadloVysledkov == 0) {
                System.out.println("Neboli najdene ziadne vysledky!");
            }
            System.out.println("******************************************************************************************************");

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
        }
    }

    /**
     * Vypise vsetky informacie o pouzivatelovi, podla kriterii v sql query stringu
     *
     * @param vstup kriterium, ktore sa vlozi na miesto "?" v sql query stringu
     * @param sql   sql string, na zaklade ktoreho sa maju vyhladat a vypisat informacie o knihach
     */
    public static void vypisInfoOPouzivatelovi(String vstup, String sql) {
        try (Connection con = DriverManager.getConnection(URLNADATABAZU, MENODODATABAZY, HESLODODATABAZY);
             PreparedStatement pst = con.prepareStatement(sql)) {

            if (!vstup.equals("*")) {
                pst.setString(1, vstup);
            }

            ResultSet rs = pst.executeQuery();

            System.out.println("\n");
            int pocitadloVysledkov = 0;
            System.out.println("******************************************************************************************************");
            while (rs.next()) {
                System.out.format("%d. Druh uctu: %s, Meno: %s, Priezvisko: %s, Vek: %d, Pocet pozicanych knih: %s," +
                                " login: %s\n",
                        rs.getInt("id"),
                        rs.getString("druhUctu"),
                        rs.getString("meno"),
                        rs.getString("priezvisko"),
                        rs.getInt("vek"),
                        rs.getString("pozicaneKnihyPocet"),
                        rs.getString("login"));
                pocitadloVysledkov++;
            }
            if (pocitadloVysledkov == 0) {
                System.out.println("Neboli najdene ziadne vysledky!");
            }
            System.out.println("******************************************************************************************************");

        } catch (SQLException ex) {
            System.out.println("Pozor! Nastala chyba!");
            ex.printStackTrace();
        }
    }
}
