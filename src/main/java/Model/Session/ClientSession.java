package Model.Session;

import Model.Person;
import Model.Worker.ClientServiceWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Odpowiada za tworzenie obiektu ClientSession, który jest utworzony w celu zamodelowania i
 * przechowania informacji o sesjach zgłoszonych przez osobę z typem klient oraz obsługiwanych przez pracownika oddziału obsługi klienta.
 */
@Entity(name="ClientSession")
public class ClientSession {

    public int sessionNumber;

    /**
     *  Zmienne klasy ClientSession
     */
    private String problemDescription;
    private boolean solved=false;

    /**
     * Zmienne przechowujące dane o obiektach klas połączonych asocjacją z klasą sesji klienta.
     */
    private Person client;
    private ClientServiceWorker clientServiceWorker;

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public ClientSession(){}

    /**
     * Publiczny konstruktor tworzący obiekt klasy ClientSession
     *
     * @param client - zmienna określająca klienta biorącego udział w danej sesji
     * @param clientServiceWorker - zmienna określająca pracownika działu obsługi klienta biorącego udział w danej sesji
     * @param problemDescription - zmienna określająca opis problemu, który ma rozwiązać dana sesja
     * @param solved - zmienna określająca czy problem został rozwiązany
     */
    public ClientSession(Person client,ClientServiceWorker clientServiceWorker, String problemDescription, boolean solved){
        this.client=client;
        this.clientServiceWorker=clientServiceWorker;
        this.problemDescription=problemDescription;
        this.solved=solved;
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych danej klasy.
     */
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name="increment", strategy = "increment")
    public int getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    @ManyToOne
    public Person getClient() {
        return client;
    }

    public void setClient(Person client) {
        this.client = client;
    }

    @ManyToOne
    public ClientServiceWorker getClientServiceWorker() {
        return clientServiceWorker;
    }

    public void setClientServiceWorker(ClientServiceWorker clientServiceWorker) {
        this.clientServiceWorker = clientServiceWorker;
    }

    @Basic
    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    @Basic
    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
}
