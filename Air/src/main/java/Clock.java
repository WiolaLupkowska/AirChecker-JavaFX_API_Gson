import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class Clock  implements Runnable  {
     public Text textIII;
    String info="I";
    private Thread thread;
    protected volatile boolean isRunning = false;
    private int interval;
    String stop ="IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";
    Button button;


    /**
     * Konstruktor parametrowy służący tworzeniu obiektu klasy Clock
     * @param weryfikacja typ String- wyrażenie, po osiągnięciu którego wątek zostaje zzatrzymany.
     * @param button typ Button- aktywowany po zatrzymaniu wątku
     */
    public Clock(Text weryfikacja, Button button) {
        this.textIII = weryfikacja;
        this.interval=50;
        this.button=button;

    }

    /**
     * Funkcja zwracająca wartość pola isRunning klasy.
     * @return
     */
    public boolean isRunning(){
        return isRunning;
    }

    /**
     * Funkcja nadająca zmiennej isRunning wartośc true.
     */
    public void resume(){
        isRunning=true;
    }

    /**
     * Funkcja tworząca nowy wątek, uruchamiająca go.
     */
    public void start() {
        thread = new Thread(this, "Clock thread");
        thread.start();
    }

    /**
     * Funkcja powodująca zatrzymanie działania wątku poprzez przypisanie zmiennej isRunning wartości false.
     */
    public void stop() {
        isRunning = false;

    }

    /**
     * Funkcja uruchamiająca wątek, przypisuje zmiennej isRunning wartość prawdziwą
     * oraz powoduje dodawanie znaków do zmiennej info (String) aż do osiągnięcia
     * żądanej wartości. Po jej osiągnięciu wątek zostaje zatrzymany, przycisk
     * określony w klasie zostaje uaktywniony.
     */
    @Override
    public void run() {

        isRunning = true;
        while (isRunning) {
            info=info.concat("I");
            textIII.setText(info);
            if (info.equals(stop)){
                isRunning=false;
                button.setDisable(false);



            }


            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                System.out.println("zle");
                e.printStackTrace();
            }

        }
    }

}
