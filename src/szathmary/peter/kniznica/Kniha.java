package szathmary.peter.kniznica;

/**
 * Objektova reprezentacia knihy
 */
public class Kniha {


    private final String id;
    private final String autor;
    private final String nazov;
    private final int pocetStran;
    private final String zaner;

    /**
     * Konstruktor knihy
     *
     * @param id         ID knihy
     * @param autor      Autor knihy
     * @param nazov      Nazov knihy
     * @param pocetStran Pocet stran knihy
     * @param zaner      Zaner knihy
     */
    public Kniha(String id, String autor, String nazov, int pocetStran, String zaner) {
        this.id = id;
        this.autor = autor;
        this.nazov = nazov;
        this.pocetStran = pocetStran;
        this.zaner = zaner;
    }

    public String getId() {
        return this.id;
    }

    public String getAutor() {
        return this.autor;
    }

    public String getNazov() {
        return this.nazov;
    }

    public String getZaner() {
        return this.zaner;
    }
}
