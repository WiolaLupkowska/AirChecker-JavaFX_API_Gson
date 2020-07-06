

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class StatystykiController {

    String s1 = "https://api.openaq.org/v1/measurements?city=";
    String s2 = "&parameter=";
    private String miasto;
    private String parametr;
    private LocalDate date;
    private double srednia;
    private int liczbaPomiarow;
    private String liczbaPomiarowString;
    private double wynikiSuma;
    private double min;
    private double max;
    private double stddev;
    private String sredniaString;
    private String minString;
    private String maxString;
    private String stddevString;


    List<Object> lista = new ArrayList();
    List<Object> listaJsonow = new ArrayList();
    List<Object> listaObiektow = new ArrayList();
    ArrayList wyniki = new ArrayList();
    ArrayList godziny = new ArrayList();

    XYChart.Series series = new XYChart.Series();
    ObservableList parametry = FXCollections.observableArrayList("bc", "co", "no2", "o3", "pm10", "PM2.5", "so2");

    String json;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label labelParametr1;

    @FXML
    private Text textLiczbaPomiarow;

    @FXML
    private Label labelMin;

    @FXML
    private Label labelMax;

    @FXML
    private Label labelOdchylenie;

    @FXML
    private Text textMin;

    @FXML
    private Text textMax;

    @FXML
    private Text textOdchylenie;

    @FXML
    private ScatterChart<Number, Number> chartStosunekDoNorm;

    @FXML
    private Label labelOcena;

    @FXML
    private Text textOcena;

    @FXML
    private TextArea textAreaGodzinaPomiar;

    @FXML
    private ChoiceBox<String> choiceBoxWybierzParametr;

    @FXML
    private Button buttonZapiszDane;

    @FXML
    private Text textWpiszNazweMiasta;

    @FXML
    private TextField textFieldNazwaMiasta;

    @FXML
    private Text textWybierzInteresujacaDate;

    @FXML
    private DatePicker datePickerWybierzDate;

    @FXML
    private Text textWybierzParametr;

    @FXML
    private Button buttonZatwierdzParam;

    @FXML
    private Button buttonStatystyki;

    @FXML
    private Label labelSrednia;

    @FXML
    private Text textSrednia;

    @FXML
    private Text textMiasto;

    @FXML
    private Button buttonWczytajDane;

    /**
     * Funkcja zwracająca json na podstawie url powstającego z zadanych parametrów.
     * @param miasto typu String
     * @param parametr typu String
     * @return String
     * @throws UnsupportedEncodingException
     */

    public String zapytanie(String miasto, String parametr) throws UnsupportedEncodingException {
        this.miasto = miasto;
        this.parametr = parametr;

        StringBuffer response = new StringBuffer();
        String url = s1.concat(this.miasto).concat(s2).concat(this.parametr);
        System.out.println(url);

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            System.out.println("Response :" + responseCode);
            BufferedReader in = new BufferedReader((new InputStreamReader(connection.getInputStream())));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (MalformedURLException ex) {
            System.out.println("bad url");
            ex.printStackTrace();
            textLiczbaPomiarow.setText("x");
            textSrednia.setText("x");
            textMin.setText("x");
            textMax.setText("x");
            textOdchylenie.setText("x");

        } catch (IOException ex) {
            System.out.println("connection failed");
            ex.printStackTrace();
            textLiczbaPomiarow.setText("x");
            textSrednia.setText("x");
            textMin.setText("x");
            textMax.setText("x");
            textOdchylenie.setText("x");
        }
        return response.toString();
    }

    /**
     *  Funkcja przyjmująca surowy String, zwraca listę obiektów.
     * @param response typ String
     * @return lista zawierająca elementy typu Object
     */

    public List<Object> obiektZJsona(String response) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        Map<String, Object> m = gson.fromJson(response.toString(), Map.class);
        List<Object> resultsList = (List<Object>) m.get("results");
        System.out.println(resultsList.size()+"lista z surowego stringa");
        return resultsList;
    }

    /**
     *  Funkcja tworząca osobne jsony z listy obiektów.
     * @param miasto typ String
     * @param parametr typ String
     * @return lista jsonów
     * @throws UnsupportedEncodingException
     */

    public List naJson(String miasto, String parametr) throws UnsupportedEncodingException {
        this.miasto = miasto;
        this.parametr = parametr;
        List<Object> listaJsonowParametr = new ArrayList();

        String zapytanie = zapytanie(miasto, parametr);
        System.out.println(zapytanie);

        //mam tutaj liste wynikow
        lista = obiektZJsona(zapytanie);
        System.out.println(lista.size());

        //kazdy wynik robie na json
        for (int i = 0; i < lista.size(); i++) {
            json = gson.toJson(lista.get(i));
            listaJsonowParametr.add(json);
        }

        return listaJsonowParametr;
    }

    /**
     * Funkcja tworząca obiekty typu Informacja, przypisuje jej polom wartości otrzymane na podstawie jsonów.
     * @param listaJsonow List
     * @param wyniki typ ArrayList
     * @param desiredDate typLocalDate
     * @return typ List, lista obiektów typu Informacja
     */
    public List jsonNaObiekt(List<Object> listaJsonow, ArrayList wyniki, LocalDate desiredDate) {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        List<Object> listaObiektow = new ArrayList();
        for (int i = 0; i < listaJsonow.size(); i++) {
            Informacja info = gson.fromJson(listaJsonow.get(i).toString(), Informacja.class);  //stworzenie obiektu Informacji
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(info.getDate().getUtc());        //data Date -> ZonedDateTime (słowna)
            LocalDate data = zonedDateTime.toLocalDate();                                      // ZonedDateTime->LocalDate  (liczbowa)

            if (data.equals(desiredDate)) {                                     //jesli data w jsonie rowna sie dacie wybranej z kalendarza

                this.wyniki.add(Double.valueOf(info.getValue()));               //zapełnienie list z wynikami i odpowiadającymi godzinami
                godziny.add((info.getDate().getUtc().substring(11, 13)));
            }
            listaObiektow.add(info);
        }


        return listaObiektow;
    }


    /**
     * Funkcja klasyfikująca średni wynik pomiaru i ustawiająca informację w Text Field na temat stanu powietrza związanego z parametrem
     */
    public  void ocena() {
        String wybranyParametr = parametr;
        System.out.println(wybranyParametr);
        String info;
        String s1 = "bardzo zly";
        String s2 = "zly";
        String s3 = "Dostateczny";
        String s4 = "Umiarkowany";
        String s5 = "Dobry";
        String s6 = "Bardzo dobry";
        info = textAreaGodzinaPomiar.getText();
        if (wybranyParametr.equals(parametry.get(5))) {
            info = info.concat("\n wynik>121: bardzo zly \n wynik<121 i wynik>85.1: zly \n wynik<85 i wynik >61.1: umiarkowany" +
                    " \n wynik<61 i wynik>37.1: dostateczny \n wynik <37 i wynik>12.1: dobry \n wynik<13: bardzo dobry\n");
            textAreaGodzinaPomiar.setText(info);
            if (srednia > 121) textOcena.setText(s1);
            if (srednia < 121 & srednia > 85.1) textOcena.setText(s2);
            if (srednia < 85 & srednia > 61.1) textOcena.setText(s3);
            if (srednia < 61 & srednia > 37.1) textOcena.setText(s4);
            if (srednia < 37 & srednia > 13.1) textOcena.setText(s5);
            if (srednia < 13 & srednia > 0) textOcena.setText(s6);
        }

            else if (wybranyParametr.equals(parametry.get(4))) {
                info = info.concat("\n wynik>201: bardzo zly \n wynik<201 i wynik>141.1: zly \n wynik<141 i wynik >101.1: umiarkowany" +
                        " \n wynik<101 i wynik>61.1: dostateczny \n wynik <61 i wynik>21.1: dobry \n wynik<21: bardzo dobry\n");
                textAreaGodzinaPomiar.setText(info);
                if (srednia > 201) textOcena.setText(s1);
                if (srednia < 201 & srednia > 141.1) textOcena.setText(s2);
                if (srednia < 141 & srednia > 101.1) textOcena.setText(s3);
                if (srednia < 101 & srednia > 61.1) textOcena.setText(s4);
                if (srednia < 61 & srednia > 21.1) textOcena.setText(s5);
                if (srednia < 21 & srednia > 0) textOcena.setText(s6);

            } else if (wybranyParametr.equals(parametry.get(3))) {
                info = info.concat("\n wynik>241: bardzo zly \n wynik<241 i wynik>181.1: zly \n wynik<181 i wynik >151.1: umiarkowany" +
                        " \n wynik<151 i wynik>121.1: dostateczny \n wynik <121 i wynik>71.1: dobry \n wynik<71: bardzo dobry\n");
                textAreaGodzinaPomiar.setText(info);
                if (srednia > 241) textOcena.setText(s1);
                if (srednia < 241 & srednia > 181.1) textOcena.setText(s2);
                if (srednia < 181 & srednia > 151.1) textOcena.setText(s3);
                if (srednia < 151 & srednia > 121.1) textOcena.setText(s4);
                if (srednia < 121 & srednia > 71.1) textOcena.setText(s5);
                if (srednia < 71 & srednia > 0) textOcena.setText(s6);

            } else if (wybranyParametr.equals(parametry.get(2))) {
                info = info.concat("\n wynik>401: bardzo zly \n wynik<401 i wynik>201.1: zly \n wynik<201 i wynik >151.1: umiarkowany" +
                        " \n wynik<151 i wynik>101.1: dostateczny \n wynik <101 i wynik>41.1: dobry \n wynik<41: bardzo dobry\n");
                textAreaGodzinaPomiar.setText(info);
                if (srednia > 401) textOcena.setText(s1);
                if (srednia < 401 & srednia > 201.1) textOcena.setText(s2);
                if (srednia < 201 & srednia > 151.1) textOcena.setText(s3);
                if (srednia < 151 & srednia > 101.1) textOcena.setText(s4);
                if (srednia < 101 & srednia > 41.1) textOcena.setText(s5);
                if (srednia < 41 & srednia > 0) textOcena.setText(s6);

            } else if (wybranyParametr.equals(parametry.get(1))) {
            info = info.concat("\n wynik>501: bardzo zly \n wynik<501 i wynik>351.1: zly \n wynik<351 i wynik >201.1: umiarkowany" +
                    " \n wynik<201 i wynik>101.1: dostateczny \n wynik <101 i wynik>51.1: dobry \n wynik<51: bardzo dobry\n");
            textAreaGodzinaPomiar.setText(info);
                if (srednia > 501) textOcena.setText(s1);
                if (srednia < 501 & srednia > 351.1) textOcena.setText(s2);
                if (srednia < 351 & srednia > 201.1) textOcena.setText(s3);
                if (srednia < 201 & srednia > 101.1) textOcena.setText(s4);
                if (srednia < 101 & srednia > 51.1) textOcena.setText(s5);
                if (srednia < 51 & srednia > 0) textOcena.setText(s6);
            } else if (wybranyParametr.equals(parametry.get(0))) {
            info = info.concat("\n wynik>51: bardzo zly \n wynik<51 i wynik>21.1: zly \n wynik<21 i wynik >16.1: umiarkowany" +
                    " \n wynik<16 i wynik>11.1: dostateczny \n wynik <11 i wynik>6.1: dobry \n wynik<6: bardzo dobry\n");
            textAreaGodzinaPomiar.setText(info);
                if (srednia > 51) textOcena.setText(s1);
                if (srednia < 51 & srednia > 21.1) textOcena.setText(s2);
                if (srednia < 21 & srednia > 16.1) textOcena.setText(s3);
                if (srednia < 16 & srednia > 11.1) textOcena.setText(s4);
                if (srednia < 11 & srednia > 6.1) textOcena.setText(s5);
                if (srednia < 6 & srednia > 0) textOcena.setText(s6);
            } else {
                System.out.println("!!!");
            }

        }

    /**
     *  Funkcja zwracająca średnią arytmetyczną z wyników pomiarów.
     * @param wyniki ArrayList
     * @return double
     */

    public static double srednia(ArrayList wyniki) {
        List<Double> numbers = wyniki;
        double sum = numbers.stream().mapToDouble(x -> x).sum();
        System.out.println(sum);
        double srednia = 0;
        srednia = sum / wyniki.size();
        System.out.println("srednia " + srednia);
        return srednia;

    }

    /**
     * Funkcja zwracająca wartość minimalną z otrzymanych wyników.
     * @param wyniki ArrayList
     * @return double
     */

    public static double min(ArrayList wyniki) {
        List<Double> numbers = wyniki;
        double min = numbers.stream().mapToDouble(x -> x).min().getAsDouble();
        System.out.println(min);
        return min;

    }

    /**
     * Funkcja zwracająca wartość maksymalną z otrzymanych wyników.
     * @param wyniki ArrayList
     * @return double
     */

    public static double max(ArrayList wyniki) {
        List<Double> numbers = wyniki;
        double max = numbers.stream().mapToDouble(x -> x).max().getAsDouble();
        System.out.println(max);
        return max;
    }

    /**
     * Funkcja zwracająca wartość odchylenie standardowe z otrzymanych wyników.
     * @param wyniki ArrayList
     * @return double
     */

    public static double stddev(ArrayList wyniki) {
        List<Double> numbers = wyniki;
        double sum = 0;
        double stddev = 0;

        double mean = srednia(wyniki);
        for (int i = 0; i < wyniki.size(); i++) {
            sum += Math.pow((numbers.get(i) - mean), 2);
        }
        stddev = Math.sqrt(sum);
        return sum;
    }

    /**
     * Funkcja zapisująca wyniki do pliku tekstowego w folderze resources.
     * @param event
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     */
    @FXML
    void buttonZapiszDanePressed(ActionEvent event) throws UnsupportedEncodingException, FileNotFoundException {

        File file = new File("C:/Users/wiole_5ewf698/Desktop/ZPO_W9/Pogoda/src/main/java/" + miasto + "_" + parametr + ".txt");
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            for (int i = 0; i < listaJsonow.size(); i++) {
                writer.println(listaJsonow.get(i));
            }
            writer.close();
        } catch (IOException ex) {
            System.out.println("błąd zapisu do pliku");
        }
    }

    /**
     * Funkcja Wczytująca dane z dowolnego pliku wybranego z komputera.
     * @param event
     * @throws IOException
     */

    @FXML
    void buttonWczytajDanePressed(ActionEvent event) throws IOException {
        ArrayList listaWczytanychJsonow = new ArrayList();
        List<Object> listaWczytanychObiektow = new ArrayList();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("text files(*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(null);
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        Informacja informacja = gson.fromJson(bufferedReader,Informacja.class);

    }

    /**
     * Funkcja powodująca pojawienie się w oknie aplikacji statystyk wyników: wykresu,
     * liczby pomiarów, wartości min, max, odchylenia standardowego, oceny powietrza,
     * ustawiająca do wglądu użytkowanika wartości dokładne pomiarów, godziny,
     * informacje na temat kalsyfikacji stanu powietrza.
     * @param event
     * @throws UnsupportedEncodingException
     */
    @FXML
    void buttonStatystykiPressed(ActionEvent event) throws UnsupportedEncodingException {

        chartStosunekDoNorm.getData().remove(series);       //usuwam serię danych
        series = new XYChart.Series();
        this.listaJsonow = naJson(miasto, parametr);
        System.out.println(listaJsonow);

        if (listaJsonow.size() != 0) {
            //jsony na obiekty poprzez iteracje w funkcji
            jsonNaObiekt(listaJsonow, wyniki, date);
            System.out.println(wyniki.size());

            if (wyniki.size() != 0) {


                liczbaPomiarow = wyniki.size();
                liczbaPomiarowString = String.valueOf(liczbaPomiarow);
                textLiczbaPomiarow.setText(liczbaPomiarowString);

                srednia = srednia(wyniki);
                sredniaString = String.valueOf(srednia);
                //String strDouble = S
                textSrednia.setText(sredniaString);

               // ocena();tring.format("%.2f", 1.23456);
                sredniaString = String.format("%.2f", srednia);

                min = min(wyniki);
                minString = String.valueOf(min);
                textMin.setText(minString);

                max = max(wyniki);
                maxString = String.valueOf(max);
                textMax.setText(maxString);

                stddev = stddev(wyniki);
                stddevString = String.valueOf(stddev);
                textOdchylenie.setText(stddevString);

                String info;
                info = textAreaGodzinaPomiar.getText();
                info=info+"Lista dokladnych wartosci wynikow pomiarow \n" + wyniki.toString() + "\n Lista godzin wykonania pomiarow \n" + godziny.toString();
                textAreaGodzinaPomiar.setText(info);

                int max = wyniki.size();
                for (int i = 0; i < max; i++) {
                    series.getData().add(new XYChart.Data(godziny.get(i), wyniki.get(i)));      //dodaję do data pary punktów x, y
                }
                chartStosunekDoNorm.getData().add(series);                                          //dodaję serię do wykresu
            } else {
                textLiczbaPomiarow.setText("x");
                textSrednia.setText("x");
                textMin.setText("x");
                textMax.setText("x");
                textOdchylenie.setText("x");
            }
        } else {
            textLiczbaPomiarow.setText("x");
            textSrednia.setText("x");
            textMin.setText("x");
            textMax.setText("x");
            textOdchylenie.setText("x");
        }
    }

    /**
     *  Funkcja zatwierdzająca wpisane/wybrane przez użytkownika parametry poprzez przypisanie ich do zmiennych.
     */

    @FXML
    void buttonZatwierdzParametrOnAction(ActionEvent event) {
        parametr = choiceBoxWybierzParametr.getValue();
        miasto = textFieldNazwaMiasta.getText();
        date = datePickerWybierzDate.getValue();


        lista = new ArrayList();
        listaJsonow = new ArrayList();
        listaObiektow = new ArrayList();
        wyniki = new ArrayList();
        godziny = new ArrayList();
    }

    /**
     * Fukcja pozwalająca użytkownikowi na wybór daty, pobranie jej do zmiennej date(LocalDate).
     * @param event
     */
    @FXML
    void datePickerWybierzDate(ActionEvent event) {
        date=datePickerWybierzDate.getValue();
    }

    /**
     * Funkcja przypisująca do zmiennej "miasto" nazwę wpisanego miasta
     * @param event
     */
    @FXML
    void textFieldNazwaMiastaOnAction(ActionEvent event) {
        miasto=textFieldNazwaMiasta.getText();
    }

    /**
     * Zapełnienie choicebox listą parametrów.
     */
    @FXML
    void initialize() {
        assert labelParametr1 != null : "fx:id=\"labelParametr1\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textLiczbaPomiarow != null : "fx:id=\"textLiczbaPomiarow\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert labelMin != null : "fx:id=\"labelMin\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert labelMax != null : "fx:id=\"labelMax\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert labelOdchylenie != null : "fx:id=\"labelOdchylenie\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textMin != null : "fx:id=\"textMin\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textMax != null : "fx:id=\"textMax\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textOdchylenie != null : "fx:id=\"textOdchylenie\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert chartStosunekDoNorm != null : "fx:id=\"chartStosunekDoNorm\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert labelOcena != null : "fx:id=\"labelOcena\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textOcena != null : "fx:id=\"textOcena\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textAreaGodzinaPomiar != null : "fx:id=\"textAreaGodzinaPomiar\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert choiceBoxWybierzParametr != null : "fx:id=\"choiceBoxWybierzParametr\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert buttonZapiszDane != null : "fx:id=\"buttonZapiszDane\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textWpiszNazweMiasta != null : "fx:id=\"textWpiszNazweMiasta\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textFieldNazwaMiasta != null : "fx:id=\"textFieldNazwaMiasta\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textWybierzInteresujacaDate != null : "fx:id=\"textWybierzInteresujacaDate\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert datePickerWybierzDate != null : "fx:id=\"datePickerWybierzDate\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textWybierzParametr != null : "fx:id=\"textWybierzParametr\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert buttonZatwierdzParam != null : "fx:id=\"buttonZatwierdzParam\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert buttonStatystyki != null : "fx:id=\"buttonStatystyki\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert labelSrednia != null : "fx:id=\"labelSrednia\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textSrednia != null : "fx:id=\"textSrednia\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert textMiasto != null : "fx:id=\"textMiasto\" was not injected: check your FXML file 'statystyki.fxml'.";
        assert buttonWczytajDane != null : "fx:id=\"buttonWczytajDane\" was not injected: check your FXML file 'statystyki.fxml'.";


        choiceBoxWybierzParametr.setItems(parametry);



    }


}
