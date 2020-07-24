package Model.Worker;

import Model.Person;
import Model.Session.ClientSession;
import Model.Session.RentSession;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Odpowiada za tworzenie obiektu ClientServiceWorker, który jest utworzony w celu zamodelowania i
 * przechowania informacji o pracowniku działu obsługi klienta, który obsługuje sesję związane z klientem i jego wypożyczeniami.
 */
@Entity(name="ClientServiceWorker")
public class ClientServiceWorker extends Person {

    /**
     * Kolekcja przechowująca sesję klienta obsłużone przez danego pracownika działu obsługi klienta.
     */
    @ElementCollection
    private Set<ClientSession> serviceWorkerClientSessions=new HashSet<>();

    /**
     * Kolekcja przechowująca sesje wypożyczenia obsłużone przez danego pracownika działu obsługi klienta.
     */
    @ElementCollection
    private Set<RentSession> serviceWorkerRentSessions=new HashSet<>();

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public ClientServiceWorker(){}

    public ClientServiceWorker(String firstName, String lastName, Set<Long> phoneNumber, Long PESEL, LocalDate hireDate, Double salary)
    {
        this.firstName=firstName;
        this.lastName=lastName;
        this.phoneNumber=phoneNumber;
        this.PESEL=PESEL;
        this.hireDate=hireDate;
        this.salary=salary;

        types.add(PersonType.Worker);
    }

    @Override
    public String toString() {
        return "ClientServiceWorker{" +
                super.toString()+
                "serviceWorkerClientSessions=" + serviceWorkerClientSessions +
                ", serviceWorkerRentSessions=" + serviceWorkerRentSessions +
                '}';
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjacje dwukierunkową, binarną między klasą ClientServiceWorker i klasą ClientSession.
     */
    @OneToMany(mappedBy="clientServiceWorker",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<ClientSession> getServiceWorkerClientSessions() {
        return serviceWorkerClientSessions;
    }

    public void setServiceWorkerClientSessions(Set<ClientSession> serviceWorkerSessions) {
        this.serviceWorkerClientSessions = serviceWorkerSessions;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjacje dwukierunkową, binarną między klasą ClientServiceWorker i klasą RentSession.
     */
    @OneToMany(mappedBy="clientServiceWorker",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<RentSession> getServiceWorkerRentSessions() {
        return serviceWorkerRentSessions;
    }

    public void setServiceWorkerRentSessions(Set<RentSession> serviceWorkerRentSessions) {
        this.serviceWorkerRentSessions = serviceWorkerRentSessions;
    }
}
