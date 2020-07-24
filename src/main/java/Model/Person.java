package Model;

import Model.Session.ClientSession;
import Model.Worker.CarServiceWorker;
import Model.Worker.ClientServiceWorker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Odpowiada za tworzenie obiektu Osoba, który jest utworzony w celu zamodelowania i
 * przechowania informacji o osobach, które pracują lub korzystały z usług wypożyczalni.
 */
@Entity(name="Person")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Person {

    /**
     * Zmienna typu enum, która określa możliwe typy osób.
     * Jedna osoba może mieć więcej niż jeden typ (dziedziczenie overlapping).
     */
    public enum PersonType {Client,Worker,Serviceman};

    /**
     *  Zmienne klasy abstrakcyjnej Person
     */
    public long id;
    protected String firstName,lastName;

    @ElementCollection
    protected Set<Long> phoneNumber=new HashSet<>();
    protected long PESEL;

    /**
     * Zmienne klasy Client
     */
    private LocalDate driveLicenceEndDate;
    private String login,password;

    /**
     * Zmienne klasy Serviceman
     */
    private Set<String> specialization;
    private String serviceName;
    private Long serviceRegon;

    /**
     *  Zmienne klasy Worker
     */
    protected LocalDate hireDate;
    protected Double salary;

    /**
     * Kolekcja przechowująca typ/typy danej osoby
     */
    @ElementCollection
    public Set<PersonType> types=new HashSet<>();

    /**
     * Kolekcja przechowująca wypożyczenia danego klienta.
     */
    private Set<Rent> rents=new HashSet<>();

    /**
     *  Kolekcja przechowująca sesję danego klienta.
     */
    private Set<ClientSession> sessions=new HashSet<>();

    /**
     *  Kolekcja przechowująca serwisy w których serwisant wykonywał naprawy smaochodu.
     */
    private Set<Service> services=new HashSet<>();

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public Person() {}

    /**
     * Publiczny konstruktor tworzący obiekt klasy Osoba bez przpisanego typu.
     *
     * @param firstName - zmienna określająca imię dodawanej osoby
     * @param lastName - zmienna określająca nazwisko dodawanej osoby
     * @param phoneNumber - zmienna określająca numer telefonu (może być podany więcej niż jeden numer telefonu) dodawanej osoby
     * @param PESEL - zmienna określająca numer PESEL dodawanej osoby
     */
    public Person(String firstName,String lastName, Set<Long> phoneNumber, Long PESEL) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.phoneNumber=phoneNumber;
        this.PESEL=PESEL;
    }

    /**
     *  Konstruktor tworzący obiekt klasy Osoba, przy czym obiekt ten będzie typu Client.
     */
    public Person(String firstName,String lastName, Set<Long> phoneNumber, Long PESEL, LocalDate driveLicenceEndDate) throws Exception
    {
        checkObjectType();

        this.firstName=firstName;
        this.lastName=lastName;
        this.phoneNumber=phoneNumber;
        this.PESEL=PESEL;
        this.driveLicenceEndDate=driveLicenceEndDate;

        setTypes(Collections.singleton(PersonType.Client));
    }

    /**
     *  Konstruktor tworzący obiekt klasy Osoba, przy czym obiekt ten będzie typu Worker.
     */
    public Person(String firstName,String lastName, Set<Long> phoneNumber, Long PESEL, LocalDate hireDate, Double salary)
    {
        this.firstName=firstName;
        this.lastName=lastName;
        this.phoneNumber=phoneNumber;
        this.PESEL=PESEL;
        this.hireDate=hireDate;
        this.salary=salary;

        setTypes(Collections.singleton(PersonType.Worker));
    }

    /**
     *  Konstruktor tworzący obiekt klasy Osoba, przy czym obiekt ten będzie typu Serviceman.
     */
    public Person(String firstName,String lastName, Set<Long> phoneNumber, Long PESEL, HashSet<String> specialization, String serviceName, Long serviceRegon) throws Exception
    {
        checkObjectType();

        this.firstName=firstName;
        this.lastName=lastName;
        this.phoneNumber=phoneNumber;
        this.PESEL=PESEL;
        this.specialization=specialization;
        this.serviceName=serviceName;
        this.serviceRegon=serviceRegon;

        setTypes(Collections.singleton(PersonType.Serviceman));
    }

    /**
     * Metoda tworząca asocjaję między obiektem klasy Wpożyczenie,a obiektem klasy Osoba typu Klient
     *
     * @param rent - obiekt klasy Wypożyczenie
     * @throws Exception - w razie próby utworzenia asocjacji dla osoby, której typem nie jest typ Klient, zostanie wyrzucony wyjątek.
     * @throws Exception - w razie próby utworzenia ponownie tej samej asocjacji, zostanie wyrzucony wyjątek.
     */
    public void addRent(Rent rent) throws Exception {
        if(!types.contains(PersonType.Client))
        {
            throw new Exception("This person is not of type client!");
        }
        else if(rents.contains(rent))
        {
            throw new Exception("This rent is already added for this client!");
        }
        getRents().add(rent);
    }

    /**
     * Metoda usuwająca asocjaję między obiektem klasy Wpożyczenie,a obiektem klasy Osoba typu Klient
     *
     * @param rent - obiekt klasy Wypożyczenie
     * @throws Exception - w razie próby usunięcia asocjacji gdzie osoba nie jest typu Klient, zostanie wyrzucony wyjątek.
     * @throws Exception - w razie próby usunięcia asocjacji dla osoby, która nie posiada danego wypożyczenia, zostanie wyrzucony wyjątek.
     */
    public void removeRent(Rent rent) throws Exception {
        if(!getTypes().contains(PersonType.Client))
        {
            throw new Exception("This person is not of type client!");
        }
        if(!getRents().contains(rent))
        {
            throw new Exception("This client is not an owner of this rent!");
        }
        getRents().remove(rent);
    }

    /**
     * Poniższe trzy metody (addClientType, addWorkerType, addServicemanType) umożliwiają dodanie konkretnego typu dla obiektu klasy Osoba.
     */
    public void addClientType(LocalDate driveLicenceEndDate) throws Exception {
        if(types.contains(PersonType.Client))
        {
            throw new Exception("This person is already of type client!");
        }
        this.driveLicenceEndDate=driveLicenceEndDate;
    }

    public void addWorkerType(LocalDate hireDate, Double salary) throws Exception {
        if(types.contains(PersonType.Worker))
        {
            throw new Exception("This person is already of type worker!");
        }
        this.hireDate=hireDate;
        this.salary=salary;
    }

    public void addServicemanType(HashSet<String> specialization, String serviceName, Long serviceRegon) throws Exception {
        if(types.contains(PersonType.Serviceman))
        {
            throw new Exception("This person is already of type serviceman!");
        }
        this.specialization=specialization;
        this.serviceName=serviceName;
        this.serviceRegon=serviceRegon;
    }

    /**
     * Metoda zmieniająca typ pracownika. (implementacja dziedziczenia dynamicznego).
     */
    public Person changeWorkerType() throws Exception {
        if(!this.types.contains(PersonType.Worker))
        {
            throw new Exception("This person in not of type worker!");
        }

        if(this instanceof ClientServiceWorker)
        {
            CarServiceWorker newWorker= new CarServiceWorker(this.firstName,this.lastName,new HashSet<>(this.phoneNumber),this.PESEL,this.hireDate,this.salary);
            newWorker.setId(this.id);
            newWorker.getTypes().addAll(this.getTypes());
            if(this.types.contains(PersonType.Client))
            {
                newWorker.setSessions(this.getSessions());
            }
            return newWorker;
        }
        else{
            ClientServiceWorker newWorker = new ClientServiceWorker(this.firstName, this.lastName, new HashSet<>(this.phoneNumber), this.PESEL, this.hireDate, this.salary);
            newWorker.setId(this.id);
            newWorker.getTypes().addAll(this.getTypes());
            if (this.types.contains(PersonType.Client)) {
                newWorker.setSessions(this.getSessions());
            }
            return newWorker;
        }
    }

    /**
     * Metoda sprawdzająca typ obiektu na rzecz którego zostaje wywołany określony konstruktor
     * @throws Exception - wyrzuca wyjątek w momencie gdy któraś z zabronionych klas chce skorzystać z określonego konstruktora
     */
    private void checkObjectType() throws Exception {
        if(this instanceof CarServiceWorker || this instanceof ClientServiceWorker) {
            throw new Exception("This class cannot use this kind of constructor");
        }
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", PESEL=" + PESEL +
                ", driveLicenceEndDate=" + driveLicenceEndDate +
                ", specialization=" + specialization +
                ", serviceName='" + serviceName + '\'' +
                ", serviceRegon=" + serviceRegon +
                ", hireDate=" + hireDate +
                ", salary=" + salary +
                ", types=" + types +
                ", rents=" + rents +
                ", sessions=" + sessions +
                ", services=" + services +
                '}';
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych i kolekcji danej klasy.
     */
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjację kwalifikowaną między klasą Osoba typu Klient oraz klasą Wypożyczenie.
     */
    @OneToMany(mappedBy = "client",cascade = CascadeType.ALL, orphanRemoval = true, fetch= FetchType.EAGER)
    public Set<Rent> getRents() {
        return rents;
    }

    public void setRents(Set<Rent> rents) {
        this.rents = rents;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjację dwukierunkową, binarną między klasą Osoba typu Klient oraz klasą ClientSession.
     */
    @OneToMany(mappedBy="client",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<ClientSession> getSessions() {
        return sessions;
    }

    public void setSessions(Set<ClientSession> sessions) {
        this.sessions = sessions;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjację dwukierunkową, binarną między klasą Osoba typu Serwisant oraz klasą Serwis.
     */
    @OneToMany(mappedBy="serviceman",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    public Set<PersonType> getTypes() {
        return types;
    }

    public void setTypes(Set<PersonType> types) {
        this.types = types;
    }

    @Basic
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Basic
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ElementCollection
    public Set<Long> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Set<Long> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Basic
    public long getPESEL() {
        return PESEL;
    }

    public void setPESEL(long PESEL) {
        this.PESEL = PESEL;
    }

    @Column
    public LocalDate getDriveLicenceEndDate() {
        return driveLicenceEndDate;
    }

    public void setDriveLicenceEndDate(LocalDate driveLicenceEndDate) {
        this.driveLicenceEndDate = driveLicenceEndDate;
    }

    @ElementCollection
    public Set<String> getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Set<String> specialization) {
        this.specialization = specialization;
    }

    @Basic
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Column
    public Long getServiceRegon() {
        return serviceRegon;
    }

    public void setServiceRegon(Long serviceRegon) {
        this.serviceRegon = serviceRegon;
    }

    @Column
    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    @Basic
    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }
}
