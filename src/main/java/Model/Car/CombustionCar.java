package Model.Car;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.time.LocalDate;

/**
 * Odpowiada za tworzenie obiektu Samochód spalinowy dziedziączego po klasie Samochód, który jest utworzony w celu zamodelowania i
 * przechowania informacji o samochodzie spalinowym, który należy lub należał do wypożyczalni.
 */
@Entity(name="CombustionCar")
@Inheritance(strategy = InheritanceType.JOINED)
public class CombustionCar extends Car{

    /**
     * Zmienne przechowujące dane o pojemności baku na paliwo samochodu oraz ilości aktualnego paliwa.
     */
    double tankCapacity, fuelAmount;

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public CombustionCar(){}

    public CombustionCar(String brand, String model, double mileage, double tankCapacity, double fuelAmount, double priceForRent, double pricePerMinute, boolean occupied, String registerNumber, LocalDate policyEndDate,String pictureURL){
        this.brand = brand;
        this.model = model;
        this.mileage = mileage;
        this.tankCapacity=tankCapacity;
        this.fuelAmount = fuelAmount;
        this.priceForRent = priceForRent;
        this.pricePerMinute = pricePerMinute;
        this.occupied = occupied;
        this.registerNumber = registerNumber;
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

    @Override
    public String toString() {
        return "CombustionCar{" +
                super.toString()+
                "tankCapacity=" + tankCapacity +
                ", fuelAmount=" + fuelAmount +
                '}';
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych danej klasy.
     */
    @Basic
    public double getTankCapacity() {
        return tankCapacity;
    }

    public void setTankCapacity(double tankCapacity) {
        this.tankCapacity = tankCapacity;
    }

    @Basic
    public double getFuelAmount() {
        return fuelAmount;
    }

    public void setFuelAmount(double fuelAmount) {
        this.fuelAmount = fuelAmount;
    }
}
