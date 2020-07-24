package Controller;

import Model.Car.Car;
import Model.Person;
import Model.Rent;
import Model.Worker.CarServiceWorker;
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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Kontroler obsługujący wszelkie zdarzenia wykonane przez użytkownika oraz aktualizujący i odświeżający widok CarConditionScreen.
 * @author s16844
 */
public class CarConditionScreenController implements Initializable {

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
     * Zmeinna przechowująca referencję do obiektu klasy CarServiceWorker, który będzie pracownikiem osługującym wypożyczone auto, jeżeli będzie wymagało obsłużenia.
     */
    private CarServiceWorker carServiceWorker=new CarServiceWorker();

    /**
     * Zmienna statyczna typu String przechowująca informację dotyczącą odpowiedzialności prawnej za niepodanie informacji o stanie wypożczonego samochodzie.
     */
    private String dialogAlertInformation="Do you want to cancel adding information about car condition?\n"+
            "Resignation from providing information on the condition of the car may be treated as an attempt of concealment of damage," +
            " and hence, the rental company may charge the borrower with an amount that will cover the cost of repairing the car.\n" +
            "Clicking 'OK' button will mean that borrower agrees on above provisions and will not add information about car condition!";

    /**
     * Zmienna statyczna typu String przechowująca informację, która będzie informowała użytkownika o konsekwencjach niepodania informacji o stanie samochodu.
     */
    private String alertEmptyTextFieldInformation="Please, fill text field with information about rented car's condition or abort this process by clicking 'Cancel' button";

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
    @FXML private TextArea carConditionTextArea;

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
    public void passNeededObjects(Person client, Car pickedCar, Rent rent) throws IOException {
        this.client=client;
        this.car=pickedCar;
        this.rent=rent;
        userLogin.setText(this.client.getFirstName()+" "+this.client.getLastName());
        updateCarInformationPanel(this.car,downloadCarImage(new URL(car.getPictureURL())));
    }

    /**
     * Metoda aktualizująca kontrolkę typu TextArea (carInformationTextArea), która wyświetla informację dotyczące wypożyczonego auta.
     * @param car - obiekt klasy Samochód, który dotyczy wypożyczonego auta, dla którego określamy informację o jego stanie.
     * @param carImage - obiekt typu Image, który przedstawia wygląda opisywanego/wypożyczonego auta.
     */
    private void updateCarInformationPanel(Car car, Image carImage)
    {
        String informationStringTemplate="Brand & Model: "+car.getBrand()+" "+car.getModel()+"\nRegister number: "+car.getRegisterNumber();
        carImageView.setImage(carImage);
        carInformationTextArea.setText(informationStringTemplate);
    }

    /**
     * Metoda wyświetlająca kontrolkę typu Alert w celu poinformowania użytkownika o konsekwencjach zrezygnowania z podania informacji o stanie auta.
     * @return - Zwracany jest obiekt typu Optional, który sam w sobie może zwrócić kontrolkę typu Alert lub zdefiniowaną domyślną wartość.
     */
    private Optional<ButtonType> confirmCancellationOfAddingInformation()
    {
        Alert dialogAlert=new Alert(Alert.AlertType.CONFIRMATION);
        dialogAlert.setHeaderText("Rental cars - cancel adding informations about rented car");
        dialogAlert.setContentText(dialogAlertInformation);

        return dialogAlert.showAndWait();
    }

    /**
     * Metoda wyświetlająca kontrolkę typu Alert w celu poinformowania użytkownika o pustym polu określającym stan auta.
     */
    private void emptyTextFieldWarningAlert()
    {
        Alert dialogAlert=new Alert(Alert.AlertType.WARNING);
        dialogAlert.setHeaderText("Warning! Empty text field with information about rented car!");
        dialogAlert.setContentText(alertEmptyTextFieldInformation);

        dialogAlert.showAndWait();
    }

    /**
     * Metoda wyświetlająca kontrolkę typu Alert w celu poinformowania użytkownika o poprawnym przyjęciu informacji dotyczącej stanu auta.
     */
    private void informAboutSuccessfullySentInformationAlert()
    {
        Alert dialogAlert=new Alert(Alert.AlertType.INFORMATION);
        dialogAlert.setHeaderText("RentalCars - Information about car condition sent!");
        dialogAlert.setContentText("Information about rented car's condition has been sent. Thank you for your time!");

        dialogAlert.showAndWait();
    }

    /**
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Cancel) w celu zrezygnowania z podania informacji o stanie wypożyczonego auta.
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Cancel))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void cancelButtonClicked(ActionEvent event) throws IOException {

        Optional<ButtonType> decision=confirmCancellationOfAddingInformation();

        if(decision.isPresent() && decision.get()==ButtonType.OK) {
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

    /**
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Send) w celu podania i zakutalizowania informacji o stanie wypożyczonego auta.
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Send))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void sendAdditionalInformationButtonClicked(ActionEvent event) throws IOException {

        if(carConditionTextArea.getText()!=null && !carConditionTextArea.getText().isEmpty())
        {
            StandardServiceRegistry registry=null;
            SessionFactory sessionFactory=null;
            try{
                registry=new StandardServiceRegistryBuilder().configure().build();
                sessionFactory=new MetadataSources(registry).buildMetadata().buildSessionFactory();

                Session session=sessionFactory.openSession();
                session.beginTransaction();

                rent.setInformationAboutRentedCarCondtion(carConditionTextArea.getText());
                session.update(rent);

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
            car.informAboutNeedToArrangeCar(carServiceWorker);
            informAboutSuccessfullySentInformationAlert();
            menuButtonClicked(event);
        }
        else
        {
            emptyTextFieldWarningAlert();
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
