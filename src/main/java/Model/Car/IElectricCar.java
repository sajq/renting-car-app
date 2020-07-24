package Model.Car;

/**
 * Interfejs, który pomaga w rozwiązaniu problemu wielodziedziczenia dla klasy HybridCar.
 */
public interface IElectricCar {

    /**
     * Statyczna zmienna typu String, która jest taka sama dla każdego samochodu elektrycznego.
     */
    static String fuelType="prąd elektryczny";

    /**
     * Metoda informująca o potrzebie naładowania samochodu.
     */
    void informAboutRecharging();
}
