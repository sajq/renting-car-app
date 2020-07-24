package Controller;

import Model.Person;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.Query;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * Kontroler reagujący na próbę logowania przez użytkownika oraz aktualizujący i odświeżający widok LoginScreen.
 * @author s16844
 */
public class LoginScreenController implements Initializable {

    /**
     * Zmienna statyczna przechowująca wartość typu String, którego treść będzie wyświetlana w momencie próby logowania zakończonej porażką.
     */
    static String loginFailedText =new String("Login or password is wrong. Please, try again.");

    /**
     * Zmienna statyczna przechowująca wartość typu String, którego treść będzie wyświetlana w momencie próby logowania przy czym przynajmniej jedna z podanych kontrolek nie jest wypełniona.
     */
    static String noCredentialsText =new String("Please, fill all given fields.");

    /**
     * Zmienna przechowująca referencję do kontrolki typu TextField przyjmującą login użytkownika w celu jego zalogowania.
     */
    @FXML
    private TextField loginField;

    /**
     * Zmienna przechowująca referencję do kontrolki typu PasswordField przyjmującą hasło użytkownika w celu jego zalogowania
     */
    @FXML
    private PasswordField passwordField;

    /**
     * Zmienna przechowująca referencję do kontrolki typu Label, która informuje użytkownika o błędach podczas logowania do systemu.
     */
    @FXML
    private Label informationLabel;

    /**
     * Metoda wchodząca w skład interfejsu Initializable, która umożliwia utworzenie kontrolera dla danego widoku (LoginScreen).
     *
     * @see <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/Initializable.html#initialize-java.net.URL-java.util.ResourceBundle-">Metoda initialize</a>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginField.setOnKeyPressed(loginToSystemHandler());
        passwordField.setOnKeyPressed(loginToSystemHandler());
    }

    /**
     * Metoda sprawdzająca czy użytkownika wypełnij wszystkie kontrolki potrzebne do jego zalogowania.
     * @return - zwracany jest true w momencie, gdy obie kontrolki są wypełnione. False gdy przynajmniej jedna z nich jest pusta.
     */
    private boolean checkFields() {
        if(loginField.getText().isEmpty() || passwordField.getText().isEmpty())
        {
            informationLabel.setText(noCredentialsText);
            return false;
        }
        return true;
    }

    /**
     * Metoda łącząca się z bazą danych w celu pobrania obiektu klasy Osoba typu Klient, który jest walidowany poprzez podany login i hasło.
     * @param login - zmienna typu String przechowująca login użytkownika
     * @param password - zmienna typu String przechowująca hasło użytkownika
     * @return - metoda zwraca obiekt klasy Obiekt, który jest wadidowany poprzez podany login i hasło. W momencie wystąpienia wyjątku lub brak takiej osoby w bazie, zostaje zwrócony null.
     */
    private Person checkCredentials(String login, String password)
    {
        Person client=null;
        StandardServiceRegistry registry=null;
        SessionFactory sessionFactory=null;
        try{
            registry=new StandardServiceRegistryBuilder().configure().build();
            sessionFactory=new MetadataSources(registry).buildMetadata().buildSessionFactory();

            Session session=sessionFactory.openSession();
            session.beginTransaction();

            String queryString="FROM Person  WHERE login=:login AND password=:password";
            Query query=session.createQuery(queryString);
            query.setParameter("login",login);
            query.setParameter("password",password);

            client=(Person)query.getSingleResult();


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
        return client;
    }

    /**
     * Metoda, która tworzy i zwraca obiekt klasy EventHandler, która służy do dynamicznego generowania "nasłuchiwaczy" wydarzeń.
     * W tym konkretnym przypadku tworzony jest listener, który nasłuchuje zdrarzenia, w którym użytkownik naciska przycisk Enter.
     * W razie tego zdarzenia wywoływana jest metoda logowania się do systemu.
     * @return - zwracany jest listener ("nasłuchiwacz") typu EventListener, który nasłuchuje pojawienie się zdarzenia w postaci naciśniecia przycisku Enter.
     */
    private EventHandler<KeyEvent> loginToSystemHandler()
    {
        return new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent keyEvent) {
                if(keyEvent.getCode()== KeyCode.ENTER)
                {
                    try {
                        logInButtonClicked(keyEvent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     * Metoda obsługująca zdarzenie, w którym użytkownik naciska kontrolkę typu Button w celu zalogowania do systemu.
     * W momencie podania poprawnych danych uwierzyteniających użytkownika, zostaje zmieniona na scenę, w której można znaleźć menu główne aplikacji.
     * @param event - zdarzenie, które wywołuje daną metodę
     * @throws Exception - wyjątek wyrzucany w momencie podania niepoprawnych danych logowania.
     */
    @FXML
    public void logInButtonClicked(Event event) throws Exception {

        Person client=null;
        if(checkFields())
        {
            client=checkCredentials(loginField.getText(),passwordField.getText());
        }
        else{
            throw new Exception("One of the given fields is empty!");
        }

        if(client!=null)
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
        }else
        {
            informationLabel.setText(loginFailedText);
        }
    }


}
