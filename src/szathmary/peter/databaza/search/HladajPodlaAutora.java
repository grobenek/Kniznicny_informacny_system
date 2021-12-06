package szathmary.peter.databaza.search;

import szathmary.peter.databaza.VypisInfo;

/**
 * Vytvori sql string query na vypisanie vsetkych informacii o knihach podla autora
 */
public class HladajPodlaAutora implements IHladajKnihu {

    /**
     * Vytvori sql string query na vypisanie vsetkych informacii o knihach podla ich autora
     *
     * @param vstup meno autora, podla ktoreho sa maju vyhladat knihy
     */
    @Override
    public void hladajKnihu(String vstup) {
        String sql = "SELECT * FROM knihy WHERE autor = ?";
        VypisInfo.vypisInfoOKnihe(vstup, sql);
    }
}
