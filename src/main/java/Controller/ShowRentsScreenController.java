package Controller;

import Model.Car.Car;
import Model.Person;
import Model.Rent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/**
 * Kontroler obsługujący wszelkie zdarzenia wykonane przez użytkownika oraz aktualizujący i odświeżający widok ShowRentsScreen.
 * @author s16844
 */
public class ShowRentsScreenController implements Initializable {

    /**
     * Finalna, Statyczna zmienna typu int przechowująca wartość określająca ile minut może upłynąć za nim opcja anulowania wypożyczenia zostanie zablokowana.
     */
    private static final int minutesToCancelRent=5;

    /**
     * Zmienna przechowująca referencję do obiektu klasy Osoba typu Klient, która dotyczy zalogowanego użytkownika.
     */
    private Person loggedClient;

    /**
     * Zmienna przechowująca referencję do kontrolki typu Label, wyświetlająca login zalogowanego użytkownika.
     */
    @FXML private Label userLogin;
    /**
     * Zmienna przechowująca referencję do kontrolki typu ListView, wyświetlająca historię wypożyczeń użytkownika.
     */
    @FXML private ListView rentsListView;

    /**
     * Kolekcja typu ObservableList przechowująca obiekty typu Wypożyczenie. Używana w celu wyświetlenia wypożyczeń danego klienta.
     */
    private ObservableList<Rent> clientRentList= FXCollections.observableArrayList();

    /**
     * Metoda wchodząca w skład interfejsu Initializable, która umożliwia utworzenie kontrolera dla danego widoku (LoginScreen).
     *
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/Initializable.html#initialize-java.net.URL-java.util.ResourceBundle-">Metoda initialize</a>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setRentListViewCellFactoryModifier();
        System.out.println("Downloading information about client's rents!");
        System.out.println(clientRentList);
        rentsListView.setItems(clientRentList);
    }

    /**
     * Metoda umożliwiająca przekazanie obiektu klasy Osoba typu Klient, który jest zalogowany w systemie, między scenami/kontrolerami.
     * @param client - przekazywany obiekt klasy Osoba typu Klient między scenami/kontrolerami.
     */
    public void passClient(Person client) throws IOException {
        this.loggedClient =client;
        userLogin.setText(this.loggedClient.getFirstName()+" "+this.loggedClient.getLastName());
        getClientsRent();
    }

    /**
     * Metoda pobierająca wypożyczenia danego klienta dzięki kolekcji wypożyczeń,
     * która jest stworzona poprzez utworzone asocjacje między obiektami klasy Osoba typu Klient i klasą Wypożyczenia.
     */
    private void getClientsRent()
    {
        StandardServiceRegistry registry=null;
        SessionFactory sessionFactory=null;
        try{
            registry=new StandardServiceRegistryBuilder().configure().build();
            sessionFactory=new MetadataSources(registry).buildMetadata().buildSessionFactory();

            Session session=sessionFactory.openSession();
            session.beginTransaction();
            session.flush();


            clientRentList.clear();
            clientRentList.addAll(loggedClient.getRents());
            System.out.println(clientRentList);

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
     * Metoda określająca wygląd komórki kontrolki ListView (rentListView).
     */
    private void setRentListViewCellFactoryModifier()
    {
        DecimalFormat df=new DecimalFormat("#.##");
        rentsListView.setCellFactory(x -> new ListCell<Rent>(){
            private ImageView carPhoto=new ImageView();
            private Image carImage;

            @Override
            protected void updateItem(Rent rent, boolean nullable) {

                super.updateItem(rent, nullable);

                Car car;
                String textToShow="";

                if(rent!= null && !nullable)
                {
                    HBox cellBox=new HBox(3);
                    cellBox.setAlignment(Pos.CENTER_LEFT);

                    car=rent.getCar();

                    try {
                        carImage=downloadCarImage(new URL(car.getPictureURL()));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    if(rent.getRentEndDate()==null)
                    {
                        textToShow="Id: "+rent.getId()+"\nRent start date: "+rent.getRentStartDate()+"\nRenting price: "+df.format(car.getPriceForRent())+"zł\nRent status: ONGOING"+"\nBrand & Model: "+car.getBrand()+" "+car.getModel()+"\nRegister number: "+car.getRegisterNumber();
                        if(ChronoUnit.MINUTES.between(rent.getRentStartDate(), LocalDateTime.now())>minutesToCancelRent)
                        {
                            cellBox.getChildren().addAll(carImageView(carImage),rentTextLabel(textToShow),endRentButton(rent));
                            setGraphic(cellBox);
                            cellBox.setSpacing(50);
                        }
                        else
                        {
                            VBox buttonBox=new VBox(2);
                            buttonBox.getChildren().addAll(cancelRentButton(rent),endRentButton(rent));
                            cellBox.getChildren().addAll(carImageView(carImage),rentTextLabel(textToShow),buttonBox);
                            cellBox.setSpacing(50);
                            buttonBox.setAlignment(Pos.CENTER);
                            buttonBox.setSpacing(25);
                            setGraphic(cellBox);
                        }
                    }
                    else
                    {
                        textToShow="Id: "+rent.getId()+"\nRent start date: "+rent.getRentStartDate()+"\nRent end date: "+rent.getRentEndDate()+"\nRenting price: "+df.format(car.getPriceForRent())+"zł\nRent status: ENDED"+"\nBrand & Model: "+car.getBrand()+" "+car.getModel()+"\nRegister number: "+car.getRegisterNumber();
                        carPhoto.setImage(carImage);
                        carPhoto.setFitHeight(100);
                        carPhoto.setFitWidth(150);

                        setText(textToShow);
                        setGraphic(carPhoto);
                        setGraphicTextGap(50);
                    }

                    setOnMouseClicked(event -> {
                        if(event.getClickCount()==2 && !isEmpty())
                        {
                            try {
                                rentsListViewCellDoubleClicked(event,rent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            setStyle("-fx-background-color: royalblue; -fx-text-fill: white;");
                        }
                    });

                    this.setStyle("-fx-background-color: grey; -fx-text-fill: white; -fx-border-color: black");
                }
                else
                {
                    this.setStyle("-fx-background-color: black;");
                }
            }
        });
    }

    /**
     * Metoda zwracająca obiekt klasy ImageView, który posłuży jako kontrolka do wyświetlenia obrazu wypożyczonego auta.
     * @param image - obraz jako parametr w celu wyświetlenia go w konkretnej komórce kontrolki ListView
     * @return - zwracany jest obiekt typu ImageView
     */
    private ImageView carImageView(Image image)
    {
        ImageView imageView=new ImageView();
        imageView.setImage(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(150);

        return imageView;
    }

    /**
     * Metoda zwracająca obiekt klasy Label, który posłuży jako kontrolka to wyświetlenia informacji o określonym wypożyczeniu.
     * @param text - wartość typu String, który będzie wstawiony jako zawartość kontrolki Label
     * @return - zwracany jest obiekt typu Label
     */
    private Label rentTextLabel(String text)
    {
        Label label=new Label(text);
        label.setTextFill(Color.WHITE);
        return label;
    }

    /**
     * Metoda tworząca kontrolkę typu Button, którego będzie dodawana do każdej komórki kontrolki ListView, gdy dane wypożyczenie nadal trwa.
     * @param rent - określa obiekt dla na rzecz którego, będą dokonywane zmiany przez daną kontrolkę typu Button
     * @return - zwraca utworzoną kontrolkę typu Button
     */
    private Button endRentButton(Rent rent){

        Button button=new Button("End rent");
        button.setOnAction(event -> {
            try {
                StandardServiceRegistry registry=null;
                SessionFactory sessionFactory=null;
                try{
                    registry=new StandardServiceRegistryBuilder().configure().build();
                    sessionFactory=new MetadataSources(registry).buildMetadata().buildSessionFactory();

                    Session session=sessionFactory.openSession();
                    session.beginTransaction();

                    rent.endRent();
                    session.update(rent);
                    session.update(rent.getCar());

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
                getClientsRent();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return button;
    }

    /**
     * Metoda tworząca kontrolkę typu Button, którego będzie dodawana do każdej komórki kontrolki ListView, gdy dane wypożyczenie nadal trwa i nie minął czas na anulowanie wypożyczenia.
     * @param rent - określa obiekt dla na rzecz którego, będą dokonywane zmiany przez daną kontrolkę typu Button
     * @return - zwraca utworzoną kontrolkę typu Button
     */
    private Button cancelRentButton(Rent rent)
    {
        Button button=new Button("Cancel rent");
        button.setOnAction(event -> {
            if(ChronoUnit.MINUTES.between(rent.getRentStartDate(), LocalDateTime.now())<=minutesToCancelRent)
            {
                StandardServiceRegistry registry=null;
                SessionFactory sessionFactory=null;
                try{
                    registry=new StandardServiceRegistryBuilder().configure().build();
                    sessionFactory=new MetadataSources(registry).buildMetadata().buildSessionFactory();

                    Session session=sessionFactory.openSession();
                    session.beginTransaction();
                    session.flush();

                    rent.getCar().freeCar();
                    session.update(rent.getCar());
                    session.delete(rent);
                    rent.getCar().getRents().remove(rent);
                    rent.getClient().removeRent(rent);


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
                getClientsRent();
            }
            else
            {
                informAboutNotBeingAbleToCancelRentInformationAlert();
            }
            });
        return button;
    }

    /**
     * Metoda wyświetlająca kontrolkę typu Alert w celu poinformowania użytkownika o braku możliwości anulowania danego wypożyczenia.
     */
    private void informAboutNotBeingAbleToCancelRentInformationAlert()
    {
        Alert dialogAlert=new Alert(Alert.AlertType.INFORMATION);
        dialogAlert.setHeaderText("Cannot cancel rent!");
        dialogAlert.setContentText("The reservation was created more than 5 minutes ago!");

        dialogAlert.showAndWait();
    }

    /**
     * Metoda pobierająca obraz ze linku zawartego w atrybucie pictureurl obiektu klasy Car
     * @param url - Link do obrazu
     * @return - zwracany jest obraz pobrany z podanego linku
     * @throws IOException - wyjątek wyrzucany jest w momencie, gdy obraz nie może zostać pobrany.
     */
    private Image downloadCarImage(URL url) {

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
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska dwukrotnie komórkę kontrolki ListView w celu zobaczenia szczegółowych informacji o danym wypożyczeniu.
     * @param event - zdarzenie, które wywołuje daną metodę (dwukrotne naciśnięcie komórki kontrolki typu ListView)
     * @param rent - obiekt wypożyczenia na rzecz, którego zostaną wyświetlone szczegółowe dane
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void rentsListViewCellDoubleClicked(MouseEvent event, Rent rent) throws Exception {
        URL urlToSpecificInformationScreen= Paths.get("src/main/java/View/ShowSpecificRentInformationScreen.fxml").toUri().toURL();
        FXMLLoader loader=new FXMLLoader(urlToSpecificInformationScreen);
        Parent showSpecificRentInformationScreenParent= loader.load();

        Scene showSpecificRentInformationScene=new Scene(showSpecificRentInformationScreenParent);

        ShowSpecificRentInformationScreenController controller=loader.getController();
        System.out.println(controller);
        controller.passNeededObjects(loggedClient,rent.getCar(),rent);

        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("RentalCars - show specific information about rent");
        stage.setScene(showSpecificRentInformationScene);
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
        controller.passClient(loggedClient);

        Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("RentalCars - menu");
        stage.setScene(rentCarScene);
        stage.show();
    }
}
