package Model;

import Model.Car.Car;
import Model.Session.RentSession;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

/**
 * Odpowiada za tworzenie obiektu Wypozyczenie, który jest utworzony w celu zamodelowania i
 * przechowania informacji o wypożyczeniach klientów wypożyczalni, którzy korzystają z określonego samochodu.
 */
@Entity(name="Rent")
public class Rent  {

    private long id;

    /**
     * Zmienne przechowujące dane o dacie rozpoczęcia wypożyczenia, zakończenia wypożyczenia, przebiegu auta na początku wypożyczenia i na jego końcu.
     */
    private LocalDateTime rentStartDate=null,rentEndDate=null;
    private Double carMileageAtStart=null,carMileageAtEnd=null;

    /**
     * Zmienna przechowującą informacje o stanie wypożyczonego auta. Klient nie musi wprowadzać informacji o stanie auta.
     */
    private String informationAboutRentedCarCondtion=null;

    /**
     * Zmienna przechowujące referencje do obiektów klas Samochód biorących udział w asocjacji.
     */
    private Car car;

    /**
     * Zmienna do przechowania klasyfikatora asocjacji kwalifikowanej zachodzącej między klasą Osoba typu Klient oraz klasy Wypożyczenie.
     */
    private Person client;

    /**
     * Kolekcja przechowująca sesję związane z wypożyczeniami, na których jest rozwiązanywany problem przez Pracownika działu obsługi klienta.
     */
    private Set<RentSession> sessions=new HashSet<>();

    /**
     * Kolekcja przechowująca rejony, w których pojawiał się dany Klient w ramach danego wypożyczenia.
     */
    private Set<Area> areas=new HashSet<>();

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public Rent(){}

    public Rent(LocalDateTime rentStartDate, Car car, Person person) throws Exception {
        this.rentStartDate = rentStartDate;
        this.car = car;
        setClient(person);

        person.addRent(this);
        car.addRent(this);
        startRent();
    }

    public Rent(Car car,Person client,LocalDateTime rentStartDate) throws Exception {
        setCar(car);
        setClient(client);

        client.addRent(this);
        car.addRent(this);
        startRent();
    }

    /**
     * Metoda tworząca asocjację między obiektem klasy Wypożyczenie i obiektem klasy Samochód.
     *
     * @throws Exception - wyjątek jest wyrzucany w momencie próby wypożyczenia samochodu, który jest w tym momencie wypożyczany.
     */
    public void startRent() throws Exception {
        setRentStartDate(LocalDateTime.now());
        setCarMileageAtStart(car.getMileage());
        car.occupyCar();
    }

    /**
     * Metoda aktualizująca asocjację, w celu określenia danego wypożyczenia jako zakończone (dodanie daty zakończenia wypożyczenia oraz zwolnienie samochodu).
     *
     * @throws Exception - wyjątek jest wyrzucany w momencie próby zwolnienia samochodu, który jest w tym momencie wolny.
     */
    public void endRent() throws Exception {
        setRentEndDate(LocalDateTime.now());
        setCarMileageAtEnd(car.getMileage());
        car.freeCar();
    }

    /**
     * Metoda usuwająca asocjację między obiektem klasy Wypożyczenie i obiektem klasy Samochód.
     *
     * @throws Exception - wyjątek jest wyrzucany w momencie próby zwolnienia samochodu, który jest w tym momencie wolny.
     */
    public void cancelRent() throws Exception {
        setRentEndDate(LocalDateTime.now());
        setCarMileageAtEnd(car.getMileage());
        car.freeCar();
    }

    public double rentTotalMileage() throws Exception {
        if(getCarMileageAtEnd()!=null)
        {
            return getCarMileageAtEnd()-getCarMileageAtStart();
        }
        else
        {
            throw new Exception("This rent has not ended!");
        }
    }

    public double rentTotalPrice() throws Exception {
        if(getRentEndDate()!=null)
        {
            long minutes= ChronoUnit.MINUTES.between(getRentStartDate(), getRentEndDate());
            return (minutes*getCar().getPricePerMinute())+getCar().getPriceForRent();
        }
        else
        {
            throw new Exception("This rent has not ended!");
        }
    }

    @Override
    public String toString() {
        return super.toString()+"{" +
                "car=" + car +
                ", rentStartDate=" + rentStartDate +
                ", rentEndDate=" + rentEndDate +
                ", carMileageAtStart=" + carMileageAtStart +
                ", getCarMileageAtEnd=" + carMileageAtEnd +
                '}';
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych i kolekcji danej klasy.
     */
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy ="increment")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @ManyToOne(targetEntity = Car.class)
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjację kwalifikowaną między klasą Osoba typu Klient oraz klasą Wypożyczenie.
     */
    @ManyToOne
    @JoinColumn(name="client_id")
    public Person getClient() throws Exception {
        if(!client.types.contains(Person.PersonType.Client))
        {
            throw new Exception("This is not a client!");
        }
        return client;
    }

    public void setClient(Person client) throws Exception {
        this.client = client;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjację dwukierunkową, binarną między Sesja wypożyczenia oraz klasą Wypożyczenie.
     */
    @OneToMany(mappedBy="rent",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<RentSession> getSessions() {
        return sessions;
    }

    public void setSessions(Set<RentSession> sessions) {
        this.sessions = sessions;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjację dwukierunkową, binarną między klasą Rejon oraz klasą Wypożyczenie.
     */
    @OneToMany(mappedBy="rent",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<Area> getAreas() {
        return areas;
    }

    public void setAreas(Set<Area> areas) {
        this.areas = areas;
    }

    @Basic(optional=true)
    public LocalDateTime getRentStartDate() {
        return rentStartDate;
    }

    public void setRentStartDate(LocalDateTime rentStartDate) {
        this.rentStartDate = rentStartDate;
    }

    @Basic(optional=true)
    public LocalDateTime getRentEndDate() {
        return rentEndDate;
    }

    public void setRentEndDate(LocalDateTime rentEndDate) {
        this.rentEndDate = rentEndDate;
    }

    @Basic(optional=true)
    public Double getCarMileageAtStart() {
        return carMileageAtStart;
    }

    public void setCarMileageAtStart(Double carMileageAtStart) {
        this.carMileageAtStart = carMileageAtStart;
    }

    @Basic
    public Double getCarMileageAtEnd() {
        return carMileageAtEnd;
    }

    public void setCarMileageAtEnd(Double getCarMileageAtEnd) {
        this.carMileageAtEnd = getCarMileageAtEnd;
    }

    @Basic(optional = true)
    public String getInformationAboutRentedCarCondtion() {
        return informationAboutRentedCarCondtion;
    }

    public void setInformationAboutRentedCarCondtion(String informationAboutRentedCarCondtion) {
        this.informationAboutRentedCarCondtion = informationAboutRentedCarCondtion;
    }
}
