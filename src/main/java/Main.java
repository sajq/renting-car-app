
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Paths;

/**
 * Klasa main służąca do inicjalizacji ekranu logowania aplikacji.
 */
public class Main extends Application {
    public static void main(String[] args) throws Exception {

        launch(args);
    }

    /**
     * Metoda określająca, która scena powinna zostać zainicjalizowana jako pierwsza.
     * @param stage - obiekt na którym będą przedstawiane poszczególne sceny.
     * @throws Exception - wyrzucany w momencie braku określonego widoku
     */
    @Override
    public void start(Stage stage) throws Exception {
        URL url= Paths.get("src/main/java/View/LoginScreen.fxml").toUri().toURL();
        Parent root= FXMLLoader.load(url);
        Scene scene=new Scene(root);

        stage.setScene(scene);
        stage.setTitle("RentalCars - log in");
        stage.show();
    }
}