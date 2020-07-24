package Controller;

import Model.Car.Car;
import Model.Car.CombustionCar;
import Model.Car.ElectricCar;
import Model.Car.HybridCar;
import Model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.util.*;

/**
 * Kontroler obsługujący wszelkie zdarzenia wykonane przez użytkownika oraz aktualizujący i odświeżający widok RentCarScreen.
 * @author s16844
 */
public class RentCarScreenController implements Initializable {

    /**
     * Zmienna przechowująca referencję do obiektu klasy Osoba typu Klient, która dotyczy zalogowanego użytkownika.
     */
    private Person client;
    /**
     * Zmienna przechowująca referencję do obiektu klasy Samochód, która dotyczy wybranego samochodu, który ma być wypożyczony przez użytkownika.
     */
    private Car pickedCar;

    /**
     * Kolekcja typu ObservableList przechowująca obiekty typu Wypożyczenie. Używana w celu wyświetlenia dostępnych aut do wypożyczenia.
     */
    private ObservableList<Car> availableCarsList= FXCollections.observableArrayList();

    /**
     * Zmienna przechowująca referencję do kontrolki typu ListView, wyświetlająca listę dostępnych aut do wypożyczenia.
     */
    @FXML private ListView carsListView;
    /**
     * Zmienna przechowująca referencję do kontrolki typu ImageView, wyświetlająca obraz samochodu, po tym jak użytkownik nacisnął na komórkę listy danego auta.
     */
    @FXML private ImageView focusedCarImageView;
    /**
     * Zmienna przechowująca referencję do kontrolki typu TextArea, wyświetlająca informacje o samochodzie, po tym jak użytkownik nacisnął na komórkę listy danego auta.
     */
    @FXML private TextArea carInformationTextArea;
    /**
     * Zmienna przechowująca referencję do kontrolki typu Label, wyświetlająca login zalogowanego użytkownika.
     */
    @FXML private Label userLogin;

    /**
     * Metoda wchodząca w skład interfejsu Initializable, która umożliwia utworzenie kontrolera dla danego widoku (LoginScreen).
     *
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/Initializable.html#initialize-java.net.URL-java.util.ResourceBundle-">Metoda initialize</a>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carsListViewCellFactoryModifier();
        System.out.println("Downloading information about available cars!");
        getAvailableCars();
        System.out.println(availableCarsList);
        carsListView.setItems(availableCarsList);
    }

    /**
     * Metoda pobierająca dostępne samochody do wypożyczenia z bazy danych.
     */
    private void getAvailableCars()
    {
        StandardServiceRegistry registry=null;
        SessionFactory sessionFactory=null;
        try{
            registry=new StandardServiceRegistryBuilder().configure().build();
            sessionFactory=new MetadataSources(registry).buildMetadata().buildSessionFactory();

            Session session=sessionFactory.openSession();
            session.beginTransaction();

            availableCarsList.addAll(session.createQuery("from Car where status=1").list());

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
     * Metoda określająca wygląd komórki kontrolki ListView (carsListView).
     */
    private void carsListViewCellFactoryModifier()
    {
        DecimalFormat df=new DecimalFormat("#.##");
        carsListView.setCellFactory(x -> new ListCell<Car>(){
            private ImageView carPhoto=new ImageView();
            private Image carImage;
            @Override
            protected void updateItem(Car car, boolean nullable) {
                super.updateItem(car, nullable);

                if(car!= null && !nullable)
                {
                    String textToShow="Brand: "+car.getBrand()+"\n"+"Model: "+car.getModel()+"\nRegister number: "+car.getRegisterNumber()+"\nRenting price: "+df.format(car.getPriceForRent())+"zł";

                    try {
                        carImage=downloadCarImage(new URL(car.getPictureURL()));
                    } catch (IOException e) {
                        awaitingSpinnerInit();
                        e.printStackTrace();
                    }

                    carPhoto.setImage(carImage);
                    carPhoto.setFitHeight(100);
                    carPhoto.setFitWidth(150);

                    setText(textToShow);
                    setGraphic(carPhoto);
                    setContentDisplay(ContentDisplay.RIGHT);
                    setGraphicTextGap(50);

                    ListCell<Car> focusedCell=this;

                    setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            focusedCell.setStyle("-fx-background-color: royalblue; -fx-text-fill: white;");
                            awaitingSpinnerInit();
                            updateCarInformationPanel(car,carImage);
                            pickedCar=car;
                        }
                    });

                    this.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
                }
                else
                {
                    this.setStyle("-fx-background-color: black;");
                }
            }
        });
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
     * Metoda wstawiająca do kontrolki typu ImageView, tymczasowy obraz, który ma zasygnalizować pobieranie obrazu danego auta.
     */
    private void awaitingSpinnerInit() {
        focusedCarImageView.setImage(new Image(getClass().getResource("/spinner.gif").toString()));
    }

    /**
     * Metoda określająca jakie informację powinny zostać podane dla poszczególnego typu auta i wstawiające je do odpowiednich kontrolek (również wstawia obraz auta do kontrolki carImage).
     * @param car - obiekt klasy Samochód na rzecz, którego określany jest odpowiedni tekst, który powinien zostać wyświetlony dla danego auta.
     * @param focusedCarImage - obraz przedstawiający wybrane auto.
     */
    private void updateCarInformationPanel(Car car, Image focusedCarImage)
    {
        String informationStringTemplate;
        DecimalFormat df=new DecimalFormat("#.##");
        if(car.getClass()==CombustionCar.class)
        {
            informationStringTemplate="Brand & Model: "+car.getBrand()+" "+car.getModel()+"\nRegister number: "+car.getRegisterNumber()+"\nOdometer: "+car.getMileage()+
                    "\nFuel tank status: "+df.format((((CombustionCar) car).getFuelAmount()/((CombustionCar) car).getTankCapacity())*100)+"% fuel left\nStatus: "+car.getStatus();
        }
        else if(car.getClass()==ElectricCar.class)
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

        focusedCarImageView.setImage(focusedCarImage);
        carInformationTextArea.setText(informationStringTemplate);
    }

    /**
     * Metoda umożliwiająca przekazanie obiektu klasy Osoba typu Klient, który jest zalogowany w systemie, między scenami/kontrolerami.
     * @param client - przekazywany obiekt klasy Osoba typu Klient między scenami/kontrolerami.
     */
    public void passClient(Person client)
    {
        this.client=client;
        userLogin.setText(client.getFirstName()+" "+client.getLastName());
    }

    /**
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Rent) w celu wypożyczenia wybranego auta.
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Rent))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void rentSelectedCarButtonClicked(ActionEvent event) throws IOException {
        URL urlToRentCarScreen= Paths.get("src/main/java/View/ConfirmRentScreen.fxml").toUri().toURL();
        FXMLLoader loader=new FXMLLoader(urlToRentCarScreen);
        Parent confirmRentScreenParent= loader.load();

        Scene rentCarScene=new Scene(confirmRentScreenParent);

        ConfirmRentScreenController controller=loader.getController();
        System.out.println(controller);
        controller.passClientAndCar(client,pickedCar);

        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("RentalCars - Confirm rent");
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