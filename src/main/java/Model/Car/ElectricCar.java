package Model.Car;

import javax.persistence.Basic;
import javax.persistence.Entity;
import java.time.LocalDate;

/**
 * Odpowiada za tworzenie obiektu Samochód elektryczny dziedziączego po klasie Samochód, który jest utworzony w celu zamodelowania i
 * przechowania informacji o samochodzie elektrycznym, który należy lub należał do wypożyczalni.
 */
@Entity(name="ElectricCar")
public class ElectricCar extends Car implements IElectricCar {

    /**
     * Zmienne przechowujące dane o pojemności baterii auta oraz o jej naładowaniu.
     */
    Double batteryCapacity = null;
    Double juiceAmount=null;

    /**
     * Publiczny pusty konstruktor jest wymagany przez Hibernate do jego poprawnego działania.
     */
    public ElectricCar(){}

    public ElectricCar(String brand, String model, double mileage, double batteryCapacity, double juiceAmount, double priceForRent, double pricePerMinute, boolean occupied, String registerNumber, LocalDate policyEndDate,String pictureURL){
        this.brand = brand;
        this.model = model;
        this.mileage = mileage;
        this.batteryCapacity=batteryCapacity;
        this.juiceAmount = juiceAmount;
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

    @Override
    public String toString() {
        return "ElectricCar{" +
                super.toString()+
                "batteryCapacity=" + batteryCapacity +
                ", juiceAmount=" + juiceAmount +
                '}';
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych danej klasy.
     */

    @Basic
    public Double getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(Double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    @Basic
    public Double getJuiceAmount() {
        return juiceAmount;
    }

    public void setJuiceAmount(Double juiceAmount) {
        this.juiceAmount = juiceAmount;
    }
}
