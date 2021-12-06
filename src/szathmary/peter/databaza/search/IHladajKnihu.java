package szathmary.peter.databaza.search;

/**
 * Interface pre hladanie (vypisanie vsetkych informaii) knih podla zadaneho vstupu
 */
public interface IHladajKnihu {
    /**
     * Vyhlada (vypise vsetky info) o knihe, podla zadaneho vstupu
     *
     * @param vstup kriterium, na zaklade ktoreho sa maju vyhladat (vypisat) knihy
     */
    void hladajKnihu(String vstup);
}
