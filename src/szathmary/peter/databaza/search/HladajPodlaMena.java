package szathmary.peter.databaza.search;

import static szathmary.peter.databaza.VypisInfo.vypisInfoOKnihe;
import static szathmary.peter.databaza.VypisInfo.vypisInfoOPouzivatelovi;

/**
 * Vytvori sql string query na vypisanie vsetkych informacii o knihach s danym nazvom, alebo o vsetkych pouzivateloch
 * podla ich priezivska
 */
public class HladajPodlaMena implements IHladajKnihu, IHladajPouzivatela {

    /**
     * Vytvori sql query string na vypisanie vsetkych informacii o knihach so zadanym nazvon
     *
     * @param vstup nazov, podla ktoreho sa maju vyhladat knihy
     */
    @Override
    public void hladajKnihu(String vstup) {
        String sql = "SELECT * FROM knihy where nazov = ?";
        vypisInfoOKnihe(vstup, sql);
    }

    /**
     * Vytvori sql string na vypisanie vsetkych informacii o pouzivateloch podla zadaneho priezviska
     *
     * @param vstup priezvisko, podla ktoreho sa maju vyhladat pouzivatelia
     */
    public void hladajPouzivatela(String vstup) {
        String sql = "SELECT * FROM pouzivatelia where priezvisko = ?";
        vypisInfoOPouzivatelovi(vstup, sql);
    }
}
