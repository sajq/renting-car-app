package Controller;

import Model.Car.Car;
import Model.Car.CombustionCar;
import Model.Car.ElectricCar;
import Model.Car.HybridCar;
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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Kontroler obsługujący wszelkie zdarzenia wykonane przez użytkownika oraz aktualizujący i odświeżający widok ShowSpecificRentInformationScreen.
 * @author s16844
 */
public class ShowSpecificRentInformationScreenController implements Initializable {

    /**
     * Zmienna przechowująca referencję do obiektu klasy Osoba typu Klient, która dotyczy zalogowanego użytkownika.
     */
    private Person client;
    /**
     * Zmienna przechowująca referencję do obiektu klasy Samochód, która dotyczy wybranego samochodu, który ma być opisany przez użytkownika.
     */
    private Car car;
    /**
     * Zmienna przechowująca referencję do obiektu klasy Wypożyczenie, dla którego zostaje zapisana informacja dotycząca stanu wypożyczonego auta.
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
     * Zmienna przechowująca referencję do kontrolki typu TextArea, która wyświetla informację o wypożyczonym samochodzie.
     */
    @FXML private TextArea carInformationTextArea;

    /**
     * Zmienna przechowująca referencję do kontrolki typu TextArea, która przyjmuje informacje o stanie samochodu, które są podawane przez użytkownika
     */
    @FXML private TextArea rentInformationTextArea;

    /**
     * Metoda wchodząca w skład interfejsu Initializable, która umożliwia utworzenie kontrolera dla danego widoku (LoginScreen).
     *
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/Initializable.html#initialize-java.net.URL-java.util.ResourceBundle-">Metoda initialize</a>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Metoda pobierająca obraz ze linku zawartego w atrybucie pictureurl obiektu klasy Car
     * @param url - Link do obrazu
     * @return - zwracany jest obraz pobrany z podanego linku
     * @throws IOException - wyjątek wyrzucany jest w momencie, gdy obraz nie może zostać pobrany.
     */
    private Image downloadCarImage(URL url) throws IOException {

        Image image;
        try {
            URLConnection con=url.openConnection();

            con.setConnectTimeout(4000);
            con.connect();

            image=SwingFXUtils.toFXImage(ImageIO.read(url),null);
        } catch (IOException e) {
            image=new Image(getClass().getClassLoader().getResource("spinner.gif").toString());
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Metoda umożliwiająca przekazanie obiektu klasy Osoba typu Klient, obiektu Samochód oraz obiektu Wypożyczenie.
     * Wszystkie wymienione zmienne dotyczą obiektów potrzebnych do poinformowania użytkownika o wypożyczonym samochodzie oraz do dodania informacji dotyczącej stanu auta.
     * @param client - przekazywany obiekt klasy Osoba typu Klient między scenami/kontrolerami.
     * @param pickedCar - @param client - przekazywany obiekt klasy Samochód między scenami/kontrolerami.
     * @param rent - przekazywany obiekt klasy Wypożyczenie między scenami/kontrolerami.
     * @throws IOException
     */
    public void passNeededObjects(Person client, Car pickedCar, Rent rent) throws Exception {
        this.client=client;
        this.car=pickedCar;
        this.rent=rent;
        userLogin.setText(this.client.getFirstName()+" "+this.client.getLastName());
        updateCarInformationPanel(this.car,downloadCarImage(new URL(car.getPictureURL())));
        updateRentInformationPanel(rent);
    }

    /**
     * Metoda aktualizująca kontrolkę typu TextArea (carInformationTextArea), która wyświetla informację dotyczące wypożyczonego auta.
     * @param car - obiekt klasy Samochód, który dotyczy wypożyczonego auta, dla którego określamy informację o jego stanie.
     * @param carImage - obiekt typu Image, który przedstawia wygląda opisywanego/wypożyczonego auta.
     */
    private void updateCarInformationPanel(Car car, Image carImage)
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

        carImageView.setImage(carImage);
        carInformationTextArea.setText(informationStringTemplate);
    }

    /**
     * Metoda aktualizująca kontrolkę typu TextArea (rentInformationTextArea), która wyświetla informację dotyczące wypożyczenia.
     * @param rent - obiekt klasy Wypożyczenie, którego dane będą wyświetlone na scenie.
     */
    private void updateRentInformationPanel(Rent rent) throws Exception {
        DecimalFormat df=new DecimalFormat("#.##");
        String informationStringTemplate="";
        if(rent.getRentEndDate()!=null && rent.getCarMileageAtEnd()!=null)
        {
            informationStringTemplate="Id: "+rent.getId()+"\nRent start date: "+rent.getRentStartDate()+"\nRent end date: "+rent.getRentEndDate()+"\nRenting price: "+df.format(rent.rentTotalPrice())+"zł\nRent total mileage: "+rent.rentTotalMileage()+"km\nRent status: ENDED";
        }
        else
        {
            informationStringTemplate="Id: "+rent.getId()+"\nRent start date: "+rent.getRentStartDate()+"\nRenting price: "+df.format(rent.getCar().getPricePerMinute())+"zł\nRent status: ONGOING";
        }

        if(rent.getInformationAboutRentedCarCondtion()!=null)
        {
            informationStringTemplate+="\n\nInformation about car condition:\n"+rent.getInformationAboutRentedCarCondtion();
        }

        rentInformationTextArea.setText(informationStringTemplate);
    }

    /**
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Go back) w celu powrotu do poprzedniej sceny.
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Go back))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void goBackButtonClicked(ActionEvent event) throws IOException {
        URL urlToShowRentsScreen= Paths.get("src/main/java/View/ShowRentsScreen.fxml").toUri().toURL();
        FXMLLoader loader=new FXMLLoader(urlToShowRentsScreen);
        Parent clientsRentsScreenParent= loader.load();

        Scene rentCarScene=new Scene(clientsRentsScreenParent);

        ShowRentsScreenController controller=loader.getController();
        System.out.println(controller);
        controller.passClient(client);

        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("RentalCars - client's rents list");
        stage.setScene(rentCarScene);
        stage.show();
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
