package szathmary.peter.databaza.search;

/**
 * Interface pre hladanie (vypisanie vsetkych informaii) knih podla zadaneho vstupu
 */
public interface IHladajPouzivatela {
    /**
     * Vyhlada (vypise vsetky info) o pouzivatelovi, podla zadaneho vstupu
     *
     * @param vstup kriterium, na zaklade ktoreho sa vyhlada (vypise info) o pouzivateloch
     */
    void hladajPouzivatela(String vstup);
}
