package szathmary.peter.databaza.search;

import szathmary.peter.databaza.VypisInfo;

/**
 * Vytvori sql string query na vypisanie vsetkych informacii o knihach podla ich zanru
 */
public class HladajPodlaZanru implements IHladajKnihu {

    /**
     * Vytvori sql string query na vypisanie vsetkych informacii o knihach podla ich zanru
     *
     * @param vstup zaner, podla ktoreho sa maju vyhladat knihy
     */
    @Override
    public void hladajKnihu(String vstup) {
        String sql = "SELECT * FROM knihy WHERE zaner = ?";
        VypisInfo.vypisInfoOKnihe(vstup, sql);
    }
}
