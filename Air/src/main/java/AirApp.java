import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AirApp extends Application {

    /** Funkcja tworząca nową odsłonę w opraciu o obiekt klasy parent.
     * @param primaryStage
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        //nazwa katalogu z resources
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("start.fxml"));
        primaryStage.setTitle("Aplikacja do kontroli jakosci powietrza");
        Scene scene = new Scene(root, 411,259);
        primaryStage.setScene(scene);
        primaryStage.show();




    }

}
