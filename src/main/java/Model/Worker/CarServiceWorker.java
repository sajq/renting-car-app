package Model.Worker;

import Model.ArrangeCar;
import Model.Car.Car;
import Model.Person;
import Model.Session.ClientSession;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Odpowiada za tworzenie obiektu CarServiceWorker, który jest utworzony w celu zamodelowania i
 * przechowania informacji o pracowniku działu obsługi samochodu, który zajmuje się samochodami wypożyczalni.
 */
@Entity(name="CarServiceWorker")
public class CarServiceWorker extends Person {

    /**
     * Kolekcja przechowująca sesję klienta obsłużone przez danego pracownika działu obsługi klienta.
     */
    private Set<ArrangeCar> arrangments=new HashSet<>();

    /**
     * Kolekcja przechowująca auta, które mają być obsłużone.
     */
    private Set<Car> carsToArrange=new HashSet<>();

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public CarServiceWorker(){}

    public CarServiceWorker(String firstName, String lastName, Set<Long> phoneNumber, Long PESEL, LocalDate hireDate, Double salary)
    {
        super(firstName,lastName,phoneNumber,PESEL,hireDate,salary);
    }

    /**
     * Metoda dodająca samochód do puli aut do obsłużenia przez danego pracownika.
     * @param car - obiekt klasy Samochód, który powinien być obsłużony.
     */
    public void addCarToArrange(Car car) {
        carsToArrange.add(car);
    }

    @Override
    public String toString() {
        return "CarServiceWorker{" +
                super.toString()+
                "arrangments=" + arrangments +
                '}';
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjacje dwukierunkową, binarną między klasą ClientServiceWorker i klasą ClientSession.
     */

    @OneToMany(mappedBy="carServiceWorker",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<ArrangeCar> getArrangments() {
        return arrangments;
    }

    public void setArrangments(Set<ArrangeCar> arrangments) {
        this.arrangments = arrangments;
    }
}
