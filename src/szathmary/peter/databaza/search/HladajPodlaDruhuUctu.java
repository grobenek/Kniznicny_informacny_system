package szathmary.peter.databaza.search;

import szathmary.peter.databaza.VypisInfo;

/**
 * Vytvori sql string query na vypisanie vsetkych informacii o pouzivateloch druhu uctu
 */
public class HladajPodlaDruhuUctu implements IHladajPouzivatela {
    /**
     * Vytvori sql string query na vypisanie vsetkych informacii o pouzivateloch podla druhu uctu
     *
     * @param vstup druh uctu pouzivatela, podla ktoreho sa maju vyhladat pouzivatelia
     */
    @Override
    public void hladajPouzivatela(String vstup) {
        String sql = "SELECT * FROM pouzivatelia WHERE druhUctu = ?";
        VypisInfo.vypisInfoOPouzivatelovi(vstup, sql);
    }
}
