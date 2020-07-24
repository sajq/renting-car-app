package Controller;

import Model.Car.*;
import Model.Person;
import Model.Rent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Kontroler obsługujący wszelkie zdarzenia wykonane przez użytkownika oraz aktualizujący i odświeżający widok ConfirmationRentScreen.
 * @author s16844
 */
public class ConfirmRentScreenController implements Initializable {

    /**
     * Zmienna statyczna typu String przechowująca informację dotyczącą odpowiedzialności prawnej za niepodanie informacji o stanie wypożczonego samochodzie.
     */
    private String dialogAlertInformation="Do you want to add information about car condition?\n"+
            "Resignation from providing information on the condition of the car may be treated as an attempt of concealment of damage," +
            " and hence, the rental company may charge the borrower with an amount that will cover the cost of repairing the car.\n" +
            "Clicking 'Cancel' button will mean that borrower agrees on above provisions and will not add information about car condition!";

    /**
     * Zmienna przechowująca referencję do obiektu klasy Osoba typu Klient, która dotyczy zalogowanego użytkownika.
     */
    private Person client;
    /**
     * Zmienna przechowująca referencję do obiektu klasy Samochód, która dotyczy wybranego samochodu, który ma być wypożyczony przez użytkownika.
     */
    private Car car;
    /**
     * Zmienna przechowująca referencję do obiektu klasy Wypożyczenie, który użytkownik może zaakceptować lub anulować.
     */
    private Rent rent;

    /**
     * Zmienna przechowująca referencję do kontrolki typu Label, wyświetlająca login zalogowanego użytkownika.
     */
    @FXML private Label userLogin;
    /**
     * Zmienna przechowująca referencję do kontrolki typu ImageView, wyświetlająca obraz reprezentujący wybrany samochód.
     */
    @FXML private ImageView carImageView;
    /**
     * Zmienna przechowująca referencję do kontrolki typu TextArea, wyświetlająca informacje o samochodzie, po tym jak użytkownik nacisnął na komórkę listy danego auta.
     */
    @FXML private TextArea carInformationTextArea;

    /**
     * Metoda wchodząca w skład interfejsu Initializable, która umożliwia utworzenie kontrolera dla danego widoku (LoginScreen).
     *
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/Initializable.html#initialize-java.net.URL-java.util.ResourceBundle-">Metoda initialize</a>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Metoda tworząca i zapisująca obiekt klasy Wypożyczenie przy czym tworzona jest asocjacja między utworzonym wypożyczeniem, klientem i wypożyczanym samochodem.
     * @throws Exception - wyrzucany jest wyjątek w momencie problemu z zapisanem danych do bazy danych (np. otwarta baza danych)
     */
    private void rentCar() throws Exception {
        rent=new Rent(car,client, LocalDateTime.now());

        StandardServiceRegistry registry=null;
        SessionFactory sessionFactory=null;
        try{
            registry=new StandardServiceRegistryBuilder().configure().build();
            sessionFactory=new MetadataSources(registry).buildMetadata().buildSessionFactory();

            Session session=sessionFactory.openSession();
            session.beginTransaction();

            session.saveOrUpdate(car);
            session.save(rent);

            session.getTransaction().commit();
            session.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }finally {
            if(sessionFactory!=null)
            {
                sessionFactory.close();
            }
        }
    }

    /**
     * Metoda pobierająca obraz ze linku zawartego w atrybucie pictureurl obiektu klasy Car
     * @param url - Link do obrazu
     * @return - zwracany jest obraz pobrany z podanego linku
     * @throws IOException - wyjątek wyrzucany jest w momencie, gdy obraz nie może zostać pobrany.
     */
    private Image downloadCarImage(URL url) throws IOException {
        URLConnection con=url.openConnection();

        con.setConnectTimeout(4000);
        con.connect();

        return SwingFXUtils.toFXImage(ImageIO.read(url),null);
    }

    /**
     * Metoda umożliwiająca przekazanie obiektu klasy Osoba typu Klient oraz obiektu klasy Samochód.
     * Wszystkie wymienione zmienne dotyczą obiektów potrzebnych do potwierdzenia wypożyczenia przez użytkownika.
     * @param client - przekazywany obiekt klasy Osoba typu Klient między scenami/kontrolerami.
     * @param pickedCar - @param client - przekazywany obiekt klasy Samochód między scenami/kontrolerami.
     * @throws IOException - wyjątek wyrzucany w momencie problemu z pobraniem obrazu reprezentującego auto.
     */
    public void passClientAndCar(Person client, Car pickedCar) throws IOException {
        this.client=client;
        this.car=pickedCar;
        userLogin.setText(this.client.getFirstName()+" "+this.client.getLastName());
        updateCarInformationPanel(this.car,downloadCarImage(new URL(car.getPictureURL())));
    }

    /**
     * Metoda wyświetlająca dane o wypożyczanym samochodzie w zależności od jego typu.
     * @param car - obiekt klasy Samochód na rzecz, którego określany jest odpowiedni tekst, który powinien zostać wyświetlony dla danego auta.
     * @param focusedCarImage - obraz przedstawiający wybrane auto.
     */
    private void updateCarInformationPanel(Car car, Image focusedCarImage)
    {
        String informationStringTemplate;
        DecimalFormat df=new DecimalFormat("#.##");
        if(car.getClass()== CombustionCar.class)
        {
            informationStringTemplate="Brand & Model: "+car.getBrand()+" "+car.getModel()+"\nRegister number: "+car.getRegisterNumber()+"\nOdometer: "+car.getMileage()+
                    "\nFuel tank status: "+df.format((((CombustionCar) car).getFuelAmount()/((CombustionCar) car).getTankCapacity())*100)+"% fuel left\nStatus: "+car.getStatus();
        }
        else if(car.getClass()== ElectricCar.class)
        {
            informationStringTemplate="Brand & Model: "+car.getBrand()+" "+car.getModel()+"\nRegister number: "+car.getRegisterNumber()+"\nOdometer: "+car.getMileage()+
                    "\nBattery charge status: "+df.format((((ElectricCar) car).getJuiceAmount()/((ElectricCar) car).getBatteryCapacity())*100)+"% juice left\nStatus: "+car.getStatus();
        }
        else
        {
            informationStringTemplate="Brand & Model: "+car.getBrand()+" "+car.getModel()+"\nRegister number: "+car.getRegisterNumber()+"\nOdometer: "+car.getMileage()+
                    "\nFuel tank status: "+df.format((((HybridCar) car).getFuelAmount()/((HybridCar) car).getTankCapacity())*100)+"% fuel left"+
                    "\nBattery charge status: "+df.format((((HybridCar) car).getJuiceAmount()/((HybridCar) car).getBatteryCapacity())*100)+"% juice left"+
                    "\nStatus: "+car.getStatus();
        }

        carImageView.setImage(focusedCarImage);
        carInformationTextArea.setText(informationStringTemplate);
    }

    /**
     * Metoda wyświetlająca kontrolkę typu Alert w celu zapytania użytkownika o tym czy chce dodać informację o stanie wypożyczanego auta.
     * @return - Zwracany jest obiekt typu Optional, który sam w sobie może zwrócić kontrolkę typu Alert lub zdefiniowaną domyślną wartość.
     */
    private Optional<ButtonType> askAboutAdditionalInformation()
    {
        Alert dialogAlert=new Alert(Alert.AlertType.CONFIRMATION);
        dialogAlert.setHeaderText("Rental cars - optional informations about rented car");
        dialogAlert.setContentText(dialogAlertInformation);

        return dialogAlert.showAndWait();
    }

    /**
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Cancel) w celu rezygnacji z wypożyczenia danego auta przy czym wyświetlana jest poprzednia scena (scena wyświetlająca dostępne auta).
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Cancel))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void cancelButtonClicked(ActionEvent event) throws IOException {
        URL urlToRentCarScreen= Paths.get("src/main/java/View/RentCarScreen.fxml").toUri().toURL();
        FXMLLoader loader=new FXMLLoader(urlToRentCarScreen);
        Parent rentCarScreenParent= loader.load();

        Scene rentCarScene=new Scene(rentCarScreenParent);

        RentCarScreenController controller=loader.getController();
        System.out.println(controller);
        controller.passClient(client);

        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("RentalCars - Renting a car");
        stage.setScene(rentCarScene);
        stage.show();
    }

    /**
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Rent) w celu powrotu potwierdzenia wypożyczenia danego auta.
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Rent))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void confirmButtonClicked(ActionEvent event) throws Exception {

        rentCar();

        Optional<ButtonType> decision=askAboutAdditionalInformation();

        if(decision.isPresent() && decision.get()==ButtonType.CANCEL)
        {
            URL urlToRentCarScreen= Paths.get("src/main/java/View/MenuScreen.fxml").toUri().toURL();
            FXMLLoader loader=new FXMLLoader(urlToRentCarScreen);
            Parent rentCarScreenParent= loader.load();

            Scene rentCarScene=new Scene(rentCarScreenParent);

            MenuScreenController controller=loader.getController();
            System.out.println(controller);
            controller.passClient(client);

            Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("RentalCars - menu");
            stage.setScene(rentCarScene);
            stage.show();
        }
        else
        {
            URL urlToCarConditionScreen= Paths.get("src/main/java/View/CarConditionScreen.fxml").toUri().toURL();
            FXMLLoader loader=new FXMLLoader(urlToCarConditionScreen);
            Parent carConditionScreenParent= loader.load();

            Scene carCondtionScene=new Scene(carConditionScreenParent);

            CarConditionScreenController controller=loader.getController();
            System.out.println(controller);
            controller.passNeededObjects(client,car,rent);

            Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("RentalCars - add information's about car's condition");
            stage.setScene(carCondtionScene);
            stage.show();
        }
    }

    /**
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Logout) w celu wylogowania się z systemu.
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Logout))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void logoutButtonClicked(ActionEvent event) throws IOException {
        URL urlToRentCarScreen= Paths.get("src/main/java/View/LoginScreen.fxml").toUri().toURL();
        Parent rentCarScreenParent= FXMLLoader.load(urlToRentCarScreen);

        Scene rentCarScene=new Scene(rentCarScreenParent);
        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("RentalCars - log in");
        stage.setScene(rentCarScene);
        stage.show();
    }

    /**
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Menu) w celu powrotu do menu głównego aplikacji.
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Menu))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void menuButtonClicked(ActionEvent event) throws IOException {
        URL urlToRentCarScreen= Paths.get("src/main/java/View/MenuScreen.fxml").toUri().toURL();
        FXMLLoader loader=new FXMLLoader(urlToRentCarScreen);
        Parent rentCarScreenParent= loader.load();

        Scene rentCarScene=new Scene(rentCarScreenParent);

        MenuScreenController controller=loader.getController();
        System.out.println(controller);
        controller.passClient(client);

        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("RentalCars - menu");
        stage.setScene(rentCarScene);
        stage.show();
    }
}
