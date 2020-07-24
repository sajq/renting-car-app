package Model.Car;

import javax.persistence.Basic;
import javax.persistence.Entity;
import java.time.LocalDate;

/**
 * Odpowiada za tworzenie obiektu Samochód hybrydowy dziedziączego po klasie Samochód spalinowy oraz implementujący interfejs Samochód elektryczny
 * (w celu umożliwienia dziedziczenia po więcej niż jednej klasie (wielodziedziczenie))
 * ,który jest utworzony w celu zamodelowania i przechowania informacji o samochodzie elektrycznym, który należy lub należał do wypożyczalni.
 */
@Entity(name="HybridCar")
public class HybridCar extends CombustionCar implements IElectricCar{

    /**
     * Zmienne przechowujące dane o pojemności baterii auta oraz o jej naładowaniu.
     */
    private double batteryCapacity;
    private double juiceAmount;

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public HybridCar(){}

    public HybridCar(String brand, String model, double mileage, double batteryCapacity, double juiceAmount, double tankCapacity, double fuelAmount , double priceForRent, double pricePerMinute, boolean occupied, String registerNumber, LocalDate policyEndDate,String pictureURL){
        this.brand = brand;
        this.model = model;
        this.mileage = mileage;
        this.batteryCapacity=batteryCapacity;
        this.juiceAmount = juiceAmount;
        this.tankCapacity=tankCapacity;
        this.fuelAmount=fuelAmount;
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
        return "HybridCar{" +
                super.toString()+
                "batteryCapacity=" + batteryCapacity +
                ", juiceAmount=" + juiceAmount +
                '}';
    }

    /**
     * Metoda określająca czy auto musi zostać naładowane.
     */
    @Override
    public void informAboutRecharging() {
        if(juiceAmount<(batteryCapacity*0.1))
        {
            System.out.println("Please recharge your car!");
        }
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych danej klasy.
     */
    @Basic
    public double getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    @Basic
    public double getJuiceAmount() {
        return juiceAmount;
    }

    public void setJuiceAmount(double juiceAmount) {
        this.juiceAmount = juiceAmount;
    }
}
