package Controller;

import Model.Person;
import Model.Rent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * Kontroler obsługujący wszelkie zdarzenia wykonane przez użytkownika oraz aktualizujący i odświeżający widok MenuScreen.
 * @author s16844
 */
public class MenuScreenController implements Initializable {

    /**
     * Zmienna określająca obiekt klasy Osoba typu Klient, która przechowuje obiekt dotyczący zalogowanego użytkownika.
     */
    private Person client;

    /**
     * Kolekcja typu ObservableList przechowująca obiekty typu Wypożyczenie. Używana w celu wyświetlenia historii wypożyczeń danego użytkownika.
     */
    private ObservableList<Rent> userActivityFeed= FXCollections.observableArrayList();

    /**
     * Zmienna przechowująca referencję do kontrolki typu ListView, wyświetlająca historię wypożyczeń użytkownika.
     */
    @FXML private ListView userActivityListView;
    /**
     * Zmienna przechowująca referencję do kontrolki typu Label, która wyświetla login zalogowanego użytkownika.
     */
    @FXML private Label userLogin;

    /**
     * Metoda wchodząca w skład interfejsu Initializable, która umożliwia utworzenie kontrolera dla danego widoku (LoginScreen).
     *
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/Initializable.html#initialize-java.net.URL-java.util.ResourceBundle-">Metoda initialize</a>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("INITIALIZE...");
    }

    /**
     * Metoda umożliwiająca przekazanie obiektu klasy Osoba typu Klient, który jest zalogowany w systemie, między scenami/kontrolerami.
     * @param client - przekazywany obiekt klasy Osoba typu Klient między scenami/kontrolerami.
     */
    public void passClient(Person client)
    {
        setUserActivityListViewCellFactoryModifier();
        this.client=client;
        userLogin.setText(client.getFirstName()+" "+client.getLastName());
        downloadUserActivityFeed();
        userActivityListView.setItems(userActivityFeed);
    }

    /**
     * Metoda pobierająca historię wypożyczeń danego użytkownika.
     */
    private void downloadUserActivityFeed()
    {
        userActivityFeed.addAll(client.getRents());
    }

    /**
     * Metoda określająca wygląd komórki kontrolki ListView (userActivityListView).
     */
    private void setUserActivityListViewCellFactoryModifier()
    {
        userActivityListView.setCellFactory(x -> new ListCell<Rent>(){
            @Override
            protected void updateItem(Rent rent, boolean nullable) {
                super.updateItem(rent, nullable);

                if(rent!= null && !nullable)
                {
                    String textToShow="Reservation placed for car with register number: "+rent.getCar().getRegisterNumber()+"\nDate: "+rent.getRentStartDate();

                    setText(textToShow);

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
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Rent a car) w celu wyświetlenia sceny, w której umożliwione jest wypożyczenie wybranego auta.
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Rent a car))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void rentCarButtonClicked(javafx.event.ActionEvent event) throws IOException {

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
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button (Show rents history) w celu wyświetlenia jego historii wypożyczeń.
     * @param event - zdarzenie, które wywołuje daną metodę (naciśnięcie kontrolki typu Button (Show rents history))
     * @throws IOException - wyjątek wyrzucany w momencie braku podanego pliku określającego widok dla wybranej nowej sceny.
     */
    @FXML
    public void showRentsButtonClicked(ActionEvent event) throws IOException {
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
}
