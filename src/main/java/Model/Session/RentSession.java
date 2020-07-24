package Model.Session;

import Model.Rent;
import Model.Worker.ClientServiceWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Odpowiada za tworzenie obiektu RentSession, który jest utworzony w celu zamodelowania i
 * przechowania informacji o sesjach związanych z wypożyczeniem oraz obsługiwanych przez pracownika oddziału obsługi klienta.
 */
@Entity(name="RentSession")
public class RentSession {

    public int sessionNumber;

    /**
     *  Zmienne klasy ClientSession
     */
    private String problemDescription;
    private boolean solved=false;

    /**
     * Zmienne przechowujące dane o obiektach klas połączonych asocjacją z klasą sesji klienta.
     */
    private Rent rent;
    private ClientServiceWorker clientServiceWorker;

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public RentSession(){}

    /**
     * Publiczny konstruktor tworzący obiekt klasy RentSession
     *
     * @param rent - zmienna określająca wypożyczenie, którego dotyczy sesja
     * @param clientServiceWorker - zmienna określająca pracownika działu obsługi klienta biorącego udział w danej sesji
     * @param problemDescription - zmienna określająca opis problemu, który ma rozwiązać dana sesja
     * @param solved - zmienna określająca czy problem został rozwiązany
     */
    public RentSession(Rent rent,ClientServiceWorker clientServiceWorker, String problemDescription, boolean solved){
        this.rent=rent;
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
    public Rent getRent() {
        return rent;
    }

    public void setRent(Rent rent) {
        this.rent = rent;
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
