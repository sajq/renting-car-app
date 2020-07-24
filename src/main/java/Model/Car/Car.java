package Model.Car;

import Model.*;
import Model.Worker.CarServiceWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Odpowiada za tworzenie obiektu Samochód, który jest utworzony w celu zamodelowania i
 * przechowania informacji o samochodzie, który należy lub należał do wypożyczalni.
 */
@Entity(name="Car")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Car {

    /**
     * zmienna typu enum, która określa możliwe statusy dla danego auta
     */
    enum CarStatus {niezarejestrowany,wolne,w_serwisie,zarezewowane,zajęte,wyłączone_z_użytku}

    /**
     * Zmienna przechowująca unikalną wartość id, dzięki której Hibernate będzie w stanie jednoznacznie określić pobraną klasę z bazy danych.
     */
    private long id;

    protected String brand,model,registerNumber;
    protected double mileage,priceForRent,pricePerMinute;
    protected boolean occupied;
    protected LocalDate policyEndDate;
    protected CarStatus status;
    protected String pictureURL;

    /**
     * Kolekcja typu Set przechowująca wszelkie wypożyczenia danego auta.
     */
    private Set<Rent> rents=new HashSet<>();

    /**
     * Kolekcja typu Set przechowująca wszelkie przygotowania danego auta.
     */
    private Set<ArrangeCar> arrangements=new HashSet<>();

    /**
     * Kolekcja typu Set przechowująca wszelkie rejony na, których pojawił się dany samochód.
     */
    private Set<Area> areas=new HashSet<>();

    /**
     * Kolekcja typu Set przechowująca wszelkie serwisy, w których znajdował się dany samochód.
     */
    private Set<Service> services=new HashSet<>();

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public Car(){}

    /**
     * Publiczny konstruktor, w którym następuję wstępne określenie statusu auta.
     *
     * @param brand - zmienna określająca markę dodawanego auta
     * @param model - zmienna określająca model dodawanego auta
     * @param mileage - zmienna określająca przebieg dodawanego auta
     * @param priceForRent - zmienna określająca cenę za wypożyczenie dodawanego auta
     * @param pricePerMinute - zmienna określająca cenę za minutę korzystania z dodawanego auta
     * @param occupied - zmienna określająca zajętość dodawanego auta
     * @param registerNumber - zmienna określająca numer rejestracyjny dodawanego auta
     * @param policyEndDate - zmienna określająca datę zakończenia polisy OC/AC dodawanego auta
     */
    public Car(String brand, String model, double mileage, double priceForRent, double pricePerMinute, boolean occupied, String registerNumber, LocalDate policyEndDate,String pictureURL){
        this.brand = brand;
        this.model = model;
        this.mileage = mileage;
        this.occupied = occupied;
        this.registerNumber = registerNumber;
        this.priceForRent = priceForRent;
        this.pricePerMinute = pricePerMinute;
        this.policyEndDate = policyEndDate;
        this.pictureURL=pictureURL;

        status=CarStatus.niezarejestrowany;

        if(registerNumber!=null)
        {
            status=CarStatus.wolne;
        }

        if(occupied==true)
        {
            status=CarStatus.zajęte;
        }
    }

    /**
     * Metoda do tworzenia asocjacji między obiektami klasy Rent i klasy Car.
     *
     * @param rent - instancja klasy Wypożyczenie
     */
    public void addRent(Rent rent)
    {
        getRents().add(rent);
    }

    /**
     * Metoda do usuwania asocjacji między obiektami klasy Rent i klasy Car.
     *
     * @param rent - instancja klasy Wypożyczenie
     */
    public void removeRent(Rent rent)
    {
        getRents().remove(rent);
        rent.setCar(null);
    }


    /**
     * Metoda do zmiany wartości statusu auta, która określa czy dane auto jest w tej chwili okupowane.
     *
     * @throws Exception - rzucenie wyjątkiem w momencie próby zmiany wartości statusu auta, na taką samą wartość.
     */
    public void occupyCar() throws Exception {
        if(isOccupied())
        {
            throw new Exception("Car is already occupied");
        }
        else
        {
            setOccupied(true);
            setStatus(CarStatus.zajęte);
        }
    }

    /**
     * Metoda do zmiany wartości statusu auta, która określa czy dane auto jest w tej chwili okupowane.
     *
     * @throws Exception - rzucenie wyjątkiem w momencie próby zmiany wartości statusu auta, na taką samą wartość.
     */
    public void freeCar() throws Exception {
        System.out.println(isOccupied());
        if(!isOccupied())
        {
            throw new Exception("Car is not occupied");
        }
        else
        {
            setOccupied(false);
            setStatus(CarStatus.wolne);
        }
    }

    /**
     * Metoda informująca pracownika działu obsługi aut o potrzebie obsłużenia auta
     */
    public void informAboutNeedToArrangeCar(CarServiceWorker carServiceWorker)
    {
        carServiceWorker.addCarToArrange(this);
        System.out.println("Car service worker has been informed!");
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", registerNumber='" + registerNumber + '\'' +
                ", mileage=" + mileage +
                ", priceForRent=" + priceForRent +
                ", pricePerMinute=" + pricePerMinute +
                ", occupied=" + occupied +
                ", policyEndDate=" + policyEndDate +
                ", status=" + status +
                '}';
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych danej klasy.
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

    /**
     * Getter i setter określający (dla Hibernate) asocjację binarną, dwukierunkową między klasą Wypożyczenie i klasą Samochód.
     */
    @OneToMany(mappedBy = "car",cascade = CascadeType.ALL, orphanRemoval = true, fetch=FetchType.EAGER)
    public Set<Rent> getRents(){
        return rents;
    }

    public void setRents(Set<Rent> rents)
    {
        this.rents=rents;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjację binarną, dwukierunkową między klasą Przygotowanie pojazdu i klasą Samochód.
     */
    @OneToMany(mappedBy = "car",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    public Set<ArrangeCar> getArrangements() {
        return arrangements;
    }

    public void setArrangements(Set<ArrangeCar> arrangements) {
        this.arrangements = arrangements;
    }

    /**
     * Getter i setter określający (dla Hibernate) asocjację binarną, dwukierunkową między klasą Rejon i klasą Samochód.
     */
    @OneToMany(mappedBy="car",cascade = CascadeType.ALL,orphanRemoval = true,fetch=FetchType.LAZY)
    public Set<Area> getAreas() {
        return areas;
    }

    public void setAreas(Set<Area> areas) {
        this.areas = areas;
    }


    @OneToMany(mappedBy="car",cascade = CascadeType.ALL,orphanRemoval = true,fetch=FetchType.LAZY)
    /**
     * Getter i setter określający (dla Hibernate) asocjację binarną, dwukierunkową między klasą Serwis i klasą Samochód.
     */
    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }

    @Basic(optional=true)
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Basic
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Basic
    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    @Basic
    public double getMileage() {
        return mileage;
    }

    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    @Basic
    public double getPriceForRent() {
        return priceForRent;
    }

    public void setPriceForRent(double priceForRent) {
        this.priceForRent = priceForRent;
    }

    @Basic
    public double getPricePerMinute() {
        return pricePerMinute;
    }

    public void setPricePerMinute(double pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
    }

    @Basic
    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Basic
    public LocalDate getPolicyEndDate() {
        return policyEndDate;
    }

    public void setPolicyEndDate(LocalDate policyEndDate) {
        this.policyEndDate = policyEndDate;
    }

    @Enumerated
    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    @Basic
    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }
}

