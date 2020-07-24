package Model;

import Model.Car.Car;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Odpowiada za tworzenie obiektu Service, który jest utworzony w celu zamodelowania i
 * przechowania informacji o serwisach, w których pracują Serwisanci i są serwisowane auta należące do wypożyczalni.
 */
@Entity(name="Service")
public class Service {

    public int id;

    /**
     * Zmienne przechowująca dane o początku serwisu, końcu serwisu danego auta, cenie za serwis oraz czy auto zostało naprawione.
     */
    private LocalDate serviceStartDate;
    private LocalDate serviceEndDate;
    private Double serviceCost;
    private boolean repaired=false;

    /**
     * Zmienne przechowujące referencję do obiektów klasy Samochód i Serwisant biorących udział w asocjacji
     */
    private Car car;
    private Person serviceman;

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public Service(){}

    /**
     * Publiczny konstruktor do tworzenia rezerwacji na serwis danego auta przez określonego serwisanta.
     */
    public Service(LocalDate serviceStartDate,Car car, Person serviceman) throws Exception {
        this.serviceStartDate=serviceStartDate;
        if(!serviceman.types.contains(Person.PersonType.Serviceman))
        {
            throw new Exception("This person is not of type Serviceman!");
        }
        this.serviceman=serviceman;
        this.car=car;
    }

    /**
     * Publiczny konstruktor do tworzenia obiektu przeprowadzonego serwisu.
     */
    public Service(LocalDate serviceStartDate,LocalDate serviceEndDate, Double serviceCost, boolean repaired,Car car, Person serviceman) throws Exception {
        this.serviceStartDate=serviceStartDate;
        this.serviceEndDate=serviceEndDate;
        this.serviceCost=serviceCost;
        this.repaired=repaired;
        this.car=car;
        if(!serviceman.types.contains(Person.PersonType.Serviceman))
        {
            throw new Exception("This person is not of type Serviceman!");
        }
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", serviceStartDate=" + serviceStartDate +
                ", serviceEndDate=" + serviceEndDate +
                ", serviceCost=" + serviceCost +
                ", repaired=" + repaired +
                ", car=" + car +
                ", serviceman=" + serviceman +
                '}';
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych i kolekcji danej klasy.
     */
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @ManyToOne
    public Person getServiceman() {
        return serviceman;
    }

    public void setServiceman(Person serviceman) {
        this.serviceman = serviceman;
    }

    @Column
    public LocalDate getServiceStartDate() {
        return serviceStartDate;
    }

    public void setServiceStartDate(LocalDate serviceStartDate) {
        this.serviceStartDate = serviceStartDate;
    }

    @Column
    public LocalDate getServiceEndDate() {
        return serviceEndDate;
    }

    public void setServiceEndDate(LocalDate serviceEndDate) {
        this.serviceEndDate = serviceEndDate;
    }

    @Basic
    public Double getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(Double serviceCost) {
        this.serviceCost = serviceCost;
    }

    @Basic
    public boolean isRepaired() {
        return repaired;
    }

    public void setRepaired(boolean repaired) {
        this.repaired = repaired;
    }
}
