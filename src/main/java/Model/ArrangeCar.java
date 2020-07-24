package Model;

import Model.Car.Car;
import Model.Worker.CarServiceWorker;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

/**
 * Odpowiada za tworzenie obiektu ArrangeCar, który jest utworzony w celu zamodelowania i
 * przechowania informacji o przygotowaniu konkretnego samochodu przez określonego pracownika działu obsługi aut.
 */
@Entity(name="ArrangeCar")
public class ArrangeCar {

    /**
     * Zmienna przechowująca unikalną wartość id, dzięki której Hibernate będzie w stanie jednoznacznie określić pobraną klasę z bazy danych.
     */
    public int id;

    private String stationName,stationAddress;
    private double costOfArrangingCar;
    private Set<String> doneArrangements;
    private Float amountOfTankedFuel,amountOfRechargedJuice;

    /**
     * Zmienna przechowująca referencję do obiektu Car, który jest połączony z daną klasą za pomocą asocjacji.
     */
    private Car car;
    /**
     * Zmienna przechowująca referencję do obiektu CarServiceWorker, który jest połączony z daną klasą za pomocą asocjacji.
     */
    private CarServiceWorker carServiceWorker;

    public ArrangeCar(){}

    /**
     * Cztery różne konstruktory służące do tworzenia odpowiednich przygotowań samochodu w zależności od typu auta (Silnik spalinowy,elektryczny i hybrydowy).
     */
    public ArrangeCar(Car car,CarServiceWorker carServiceWorker,double costOfArrangingCar,Set<String> doneArrangement)
    {
        this.car=car;
        this.carServiceWorker=carServiceWorker;
        this.costOfArrangingCar=costOfArrangingCar;
        this.doneArrangements=doneArrangement;
    }

    public ArrangeCar(Car car,CarServiceWorker carServiceWorker,double costOfArrangingCar,Set<String> doneArrangement,String stationName,String stationAddress,Float amountOfTankedFuel)
    {
        this.car=car;
        this.carServiceWorker=carServiceWorker;
        this.costOfArrangingCar=costOfArrangingCar;
        this.doneArrangements=doneArrangement;
        this.stationName=stationName;
        this.stationAddress=stationAddress;
        this.amountOfTankedFuel=amountOfTankedFuel;
    }

    public ArrangeCar(Car car,CarServiceWorker carServiceWorker,double costOfArrangingCar,Set<String> doneArrangement,String stationName,Float amountOfRechargedJuice,String stationAddress)
    {
        this.car=car;
        this.carServiceWorker=carServiceWorker;
        this.costOfArrangingCar=costOfArrangingCar;
        this.doneArrangements=doneArrangement;
        this.stationName=stationName;
        this.stationAddress=stationAddress;
        this.amountOfRechargedJuice=amountOfRechargedJuice;
    }

    public ArrangeCar(Car car,CarServiceWorker carServiceWorker,double costOfArrangingCar,Set<String> doneArrangement,String stationName,String stationAddress,Float amountOfTankedFuel,Float amountOfRechargedJuice)
    {
        this.car=car;
        this.carServiceWorker=carServiceWorker;
        this.costOfArrangingCar=costOfArrangingCar;
        this.doneArrangements=doneArrangement;
        this.stationName=stationName;
        this.stationAddress=stationAddress;
        this.amountOfRechargedJuice=amountOfRechargedJuice;
        this.amountOfTankedFuel=amountOfTankedFuel;
    }

    @Override
    public String toString() {
        return "ArrangeCar{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", stationAddress='" + stationAddress + '\'' +
                ", costOfArrangingCar=" + costOfArrangingCar +
                ", doneArrangements=" + doneArrangements +
                ", amountOfTankedFuel=" + amountOfTankedFuel +
                ", amountOfRechargedJuice=" + amountOfRechargedJuice +
                ", car=" + car +
                ", carServiceWorker=" + carServiceWorker +
                '}';
    }

    /**
     * Poniżej znajdują się gettery i settery zmiennych danej klasy.
     */
    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @ManyToOne
    public CarServiceWorker getCarServiceWorker() {
        return carServiceWorker;
    }

    public void setCarServiceWorker(CarServiceWorker carServiceWorker) {
        this.carServiceWorker = carServiceWorker;
    }

    @Basic
    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    @Basic
    public String getStationAddress() {
        return stationAddress;
    }

    public void setStationAddress(String stationAddress) {
        this.stationAddress = stationAddress;
    }

    @Basic
    public double getCostOfArrangingCar() {
        return costOfArrangingCar;
    }

    public void setCostOfArrangingCar(double costOfArrangingCar) {
        this.costOfArrangingCar = costOfArrangingCar;
    }

    @ElementCollection
    public Set<String> getDoneArrangements() {
        return doneArrangements;
    }

    public void setDoneArrangements(Set<String> doneArrangement) {
        this.doneArrangements = doneArrangement;
    }

    @Basic
    public Float getAmountOfTankedFuel() {
        return amountOfTankedFuel;
    }

    public void setAmountOfTankedFuel(Float amountOfTankedFuel) {
        this.amountOfTankedFuel = amountOfTankedFuel;
    }

    @Basic
    public Float getAmountOfRechargedJuice() {
        return amountOfRechargedJuice;
    }

    public void setAmountOfRechargedJuice(Float amountOfRechargedJuice) {
        this.amountOfRechargedJuice = amountOfRechargedJuice;
    }
}
