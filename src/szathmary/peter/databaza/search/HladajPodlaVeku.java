package szathmary.peter.databaza.search;

import szathmary.peter.databaza.VypisInfo;

/**
 * Vytvori sql string query na vypisanie vsetkych informacii o pouzivateloch podla ich veku
 */
public class HladajPodlaVeku implements IHladajPouzivatela {
    /**
     * Vytvori sql string na vypisanie vsetkych informacii o pouzivateloch podla zadaneho veku
     *
     * @param vstup vek, podla ktoreho sa maju vyhladat pouzivatelia
     */
    @Override
    public void hladajPouzivatela(String vstup) {
        String sql = "SELECT * FROM pouzivatelia WHERE vek = ?";
        VypisInfo.vypisInfoOPouzivatelovi(vstup, sql);
    }
}
