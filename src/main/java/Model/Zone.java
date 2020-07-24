package Model;

import Model.Car.Car;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Odpowiada za tworzenie obiektu Strefa, który jest utworzony w celu zamodelowania i
 * przechowania informacji o strefach, znajdujących się w rejonach.
 */
@Entity(name="Zone")
public class Zone {

    public int zoneNumber;

    /**
     * Kolecka przechowująca samochody, które choć raz pojawiły się w danym rejonie.
     */
    @ElementCollection
    private Set<Car> carsInArea=new HashSet<>();

    /**
     * Zmienna przechowująca referencję do obiektu klasy Rejon biorącego udział w asocjacji.
     */
    private Area area;

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public Zone(){}

    public Zone(Area area)
    {
        this.area=area;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "zoneNumber=" + zoneNumber +
                ", carsInArea=" + carsInArea +
                ", area=" + area +
                '}';
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych i kolekcji danej klasy.
     */
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy="increment")
    public int getZoneNumber() {
        return zoneNumber;
    }

    public void setZoneNumber(int zoneNumber) {
        this.zoneNumber = zoneNumber;
    }

    @ElementCollection
    public Set<Car> getCarsInArea() {
        return carsInArea;
    }

    public void setCarsInArea(Set<Car> carsInArea) {
        this.carsInArea = carsInArea;
    }

    @ManyToOne
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
