package szathmary.peter.riadenieStavuAplikacie;

import szathmary.peter.databaza.DatabazaControl;
import szathmary.peter.databaza.DatabazaControlAdmin;
import szathmary.peter.databaza.DatabazaControlZakaznik;
import szathmary.peter.user.Admin;
import szathmary.peter.user.DefaultPouzivatel;
import szathmary.peter.user.UdajeOUzivatelovi;
import szathmary.peter.user.Zakaznik;

import java.util.List;

/**
 * Riadi aplikaciu, v akom stave sa nachadza, riesi prihlasovanie a priradenie druh uctu pouzivatelovi
 *
 * @author petos
 */
public class RiadenieStavuAplikacie {
    private STAVYAPLIKACIE stav;
    private DefaultPouzivatel aktualnyPouzivatel;
    private DatabazaControl db = new DatabazaControlZakaznik();

    /**
     * Nastaví pociatocny stav aplikacie na zaciatok
     */
    public RiadenieStavuAplikacie() {
        this.stav = STAVYAPLIKACIE.ZACIATOK;
    }

    /**
     * Zavola metodu vyberStavAplikacie() a tym spustí aplikaciu
     */
    public void spustiAplikaciu() {
        this.vyberStavAplikacie();
    }

    private void vyberStavAplikacie() {
        switch (this.stav) {
            case ZACIATOK -> this.vypisUvod();
            case LOGIN -> this.prihlasUzivatela();
            case MENU -> this.vypisMoznosti();
            default -> throw new IllegalStateException("Neznamy stav: " + this.stav);
        }
    }

    /**
     * Prihlasi pouzivatela a ziska jeho udaje a robi to dovtedy, pokial sa pouzivatela nepodari prihlasit
     * nasledne priradi druh uctu pouzivatelovi na zaklade jeho udaju
     */
    private void prihlasUzivatela() {
        Login login = new Login();
        List<String> udajeOPouzivatelovi = login.prihlasPouziatela(this.db);

        while (udajeOPouzivatelovi == null) {
            udajeOPouzivatelovi = login.prihlasPouziatela(this.db);
        }

        this.priradDruhUctuAktualnehoPouzivatela(udajeOPouzivatelovi);
    }

    /**
     * Na zaklade udajov o pouzivatelovi vytvori objekt pouzivatela bud Admin alebo Zakaznik
     *
     * @param parametre List naplneny udajmi o pouzivatelovi
     */
    private void priradDruhUctuAktualnehoPouzivatela(List<String> parametre) {
        UdajeOUzivatelovi udajeOUzivatelovi = new UdajeOUzivatelovi(parametre);

        switch (parametre.get(0)) {
            case "admin" -> {
                this.aktualnyPouzivatel = new Admin(udajeOUzivatelovi);
                this.db = new DatabazaControlAdmin();
                System.out.println("Si admin.");
                System.out.println();
                this.stav = STAVYAPLIKACIE.MENU;
                this.db.vypisKnihyKtoreTrebaOchviluVratit(this.aktualnyPouzivatel.getId());
                this.vyberStavAplikacie();
            }
            case "zakaznik" -> {
                this.aktualnyPouzivatel = new Zakaznik(udajeOUzivatelovi);
                this.db = new DatabazaControlZakaznik();
                System.out.println("Si zakaznik.");
                System.out.println();
                this.stav = STAVYAPLIKACIE.MENU;
                this.db.vypisKnihyKtoreTrebaOchviluVratit(this.aktualnyPouzivatel.getId());
                this.vyberStavAplikacie();
            }
        }
    }

    /**
     * Vypise a vykona vybranu moznost pouzivatela na zaklade jeho druhu uctu
     */
    private void vypisMoznosti() {
        this.vykonajMoznost(this.aktualnyPouzivatel.vypisANacitajMoznost());
    }

    /**
     * Vykona vybranu moznost z vypisMoznosti() a na zaklade vrateneho stavu aplikacie nastavi aplikaciu na dany stav
     *
     * @param vybranaMoznost Vybrana moznost, ktora sa ma vykonat
     */
    private void vykonajMoznost(String vybranaMoznost) {
        this.stav = this.aktualnyPouzivatel.vykonajVybranuMoznost(vybranaMoznost, this.db);
        this.vyberStavAplikacie();
    }

    /**
     * Vypise uvodny pozdrav pri starte aplikacie
     */
    private void vypisUvod() {
        System.out.println("Vitaj v informacnom systeme pre nasu kniznicu!");
        this.stav = STAVYAPLIKACIE.LOGIN;
        this.vyberStavAplikacie();
    }
}
