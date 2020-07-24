package Model;

import Model.Car.Car;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Odpowiada za tworzenie obiektu Rejon, który jest utworzony w celu zamodelowania i
 * przechowania informacji o rejonach, w których pojawiły się samochody wypożyczone przez klientów oraz należące do wypożyczalni.
 */
@Entity(name="Area")
public class Area {

    public int id;

    /**
     * Kolekcja przechowująca ulice należące do danego rejonu.
     */
    @ElementCollection
    private Set<String> streets=new HashSet<>();

    /**
     * Kolekcja przechowująca strefy, które należą do danego rejonu.
     */
    @ElementCollection
    private Set<Zone> zones=new HashSet<>();

    /**
     * Zmienne przechowujące referencję do obiektów klas Wypożyczenie oraz Samochód, które biorą udział w asocjacji.
     */
    private Rent rent;
    private Car car;

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public Area(){}

    public Area(Set<String> streets)
    {
        this.streets.addAll(streets);
    }

    @Override
    public String toString() {
        return "Area{" +
                "id=" + id +
                ", streets=" + streets +
                ", zones=" + zones +
                ", rent=" + rent +
                ", car=" + car +
                '}';
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych i kolekcji danej klasy.
     */
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjację dwukierunkową, binarną między klasą Strefa i klasą Rejon.
     */
    @OneToMany(mappedBy="area",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<Zone> getZones() {
        return zones;
    }

    public void setZones(Set<Zone> zones) {
        this.zones = zones;
    }

    @ManyToOne
    public Rent getRent() {
        return rent;
    }

    public void setRent(Rent rent) {
        this.rent = rent;
    }

    @ManyToOne
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @ElementCollection
    public Set<String> getStreets() {
        return streets;
    }

    public void setStreets(Set<String> streets) {
        this.streets = streets;
    }
}
