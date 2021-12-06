package szathmary.peter.databaza.search;

import static szathmary.peter.databaza.VypisInfo.vypisInfoOKnihe;
import static szathmary.peter.databaza.VypisInfo.vypisInfoOPouzivatelovi;

/**
 * Vytvori sql string query na vypisanie vsetkych informacii o pouzivateloch alebo knihach podla ich ID
 */
public class HladajPodlaId implements IHladajKnihu, IHladajPouzivatela {

    /**
     * Vytvori sql string na vypisanie vsetkych informacii knih podla zadaneho ID
     *
     * @param vstup ID podla ktoreho sa ma vyhladat kniha
     */
    @Override
    public void hladajKnihu(String vstup) {
        String sql = "SELECT * FROM knihy where id = ?";
        vypisInfoOKnihe(vstup, sql);
    }

    /**
     * Vytvori sql string na vypisanie vsetkych informacii o pouzivatelovi podla zadaneho ID
     *
     * @param vstup ID podla ktoreho sa ma vyhladat pouzivatel
     */
    @Override
    public void hladajPouzivatela(String vstup) {
        String sql = "SELECT * FROM pouzivatelia where id = ?";
        vypisInfoOPouzivatelovi(vstup, sql);
    }
}
