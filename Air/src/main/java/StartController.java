import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StartController {
    private Clock clock;
    String stop ="IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text textIII;

    @FXML
    private Button buttonDalej;


    /**
     * Funkcja otwierająca nowe okno aplikacji
     * @param event
     */
    @FXML
    void dalejOnAction(ActionEvent event) {

        Parent root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("statystyki.fxml"), resources);
            Stage stage = new Stage();
            stage.setTitle("Air");
            stage.setScene(new Scene(root, 874, 624));
            stage.show();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void initialize() {
        assert textIII != null : "fx:id=\"textIII\" was not injected: check your FXML file 'start.fxml'.";
        assert buttonDalej != null : "fx:id=\"buttonDalej\" was not injected: check your FXML file 'start.fxml'.";

        clock = new Clock(textIII,buttonDalej);
        clock.start();

    }
}
