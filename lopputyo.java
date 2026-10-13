package oma.grafiikka.graphics;



import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.Serializable;
import java.io.*;
import java.util.*;

/**
 * To do lista jolla voi lisata uuden tehtavan ja poistaa vanhoja tehtavia
 *
 * @author Kimmo Viljanen
 * @version 1.0
 */
public class lopputyo extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Lista mihin tallenetaan ja missa kasitellaan kaikkia tehtavia
     */
    public ArrayList<uusi_tehtava> kaikki_tehtavata = new ArrayList<>();
    /**
     * Uusi tehtava painike
     */
    public Button b1 = new Button("Uusi tehtava");


    /**
     * Grid pane mihin laitetaan text fieldit mihin syotetaan tehtavan tiedot
     */
    public GridPane root2 = new GridPane();
    /**
     * Vbox mihin laitetaan valmis tehtava ja sen tiedot
     */
    public VBox root_tehtavat = new VBox();
    /**
     * Ikkuna missa nakyy tehtavat ja missa voi tehda uuden tehtavan
     */
    Scene s1 = new Scene(root_tehtavat);
    Scene s2 = new Scene(root2);
    /**
     * Luo tiedoston tehtavat.dat jos sita ei ole olemassa
     */
    public File tehtavien_tiedot = new File("tehtavat.dat");
    /**
     * Luo stagen olion jotta sita voidaan kayttaa muuaallakin kuin start metodissa
     */
    public Stage stage = new Stage();

    /**
     * Ohjelma ikkunan kaynnistys ja sen toiminnalisuuden tekeminen
     * @param st stage olio joka on tyhja
     */
    @Override
    public void start(Stage st) {
        lataa_tiedot();
        stage = st;

        b1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            /**
             * Avaa uuden ikkunan missa voi tehda uuden tehtavan
             */
            @Override
            public void handle(MouseEvent mouseEvent) {
                //region<Käyttöliittymän alustus>
                TextField kello_aika = new TextField();
                TextField paivamaara = new TextField();
                TextField tehtavat = new TextField();
                Button b2 = new Button("Ok!");
                Text a1 = new Text("Kellon aika (xx.xx): ");
                Text a2 = new Text("Paiva maara (xx.xx): ");
                Text a3 = new Text("Tehtava: ");

                root2.add(a1, 0, 0);
                root2.add(kello_aika, 1, 0);
                root2.add(a2, 0, 1);
                root2.add(paivamaara, 1, 1);
                root2.add(a3, 0, 2);
                root2.add(tehtavat, 1, 2);
                root2.add(b2, 2, 1);

                stage.setScene(s2);
                stage.show();
                //endregion<>

                b2.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        kaikki_tehtavata.add(luo_tehtava(Float.parseFloat(kello_aika.getText()), Float.parseFloat(paivamaara.getText()), tehtavat.getText()));
                        kaikki_tehtavata.sort(uusi_tehtava::compareTo);
                        root_tehtavat.getChildren().add(vbox_listasta());

                        stage.setScene(s1);
                        stage.show();

                    }
                });

            }
        });

        paivita_stage();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            /**
             * Tallentaa tiedot tiedostoon
             */
            @Override
            public void handle(WindowEvent windowEvent) {
                if (kaikki_tehtavata.size() > 0) {
                    tallenna_tiedot();
                }
            }
        });
    }

    /**
     * Luo uuden tehtavan
     * @param x Kellon aika
     * @param y Paiva maara
     * @param z Tehtava
     * @return Palauttaa valmiin tehtava olion
     */


    public uusi_tehtava luo_tehtava(float x, float y, String z){
        uusi_tehtava t = new uusi_tehtava(x,y,z);

        t.kuukausi.setText(String.format("PVM: %.2f", t.getMonth()));
        t.kuukausi.setText(t.kuukausi.getText().replace(",","."));
        t.klo.setText(String.format(" KLO: %.2f", t.getTime()));
        t.klo.setText(t.klo.getText().replace(",","."));
        t.aika.getChildren().addAll(t.kuukausi, t.klo);
        t.tehtava.setText(" Tehtava: " + t.getTask());
        t.yksi_tehtava.getChildren().addAll(t.check, t.aika, t.tehtava);

        t.check.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                kaikki_tehtavata.remove(t);
                paivita_stage();
            }
        });
        return t;
    }

    /**
     * Tekee kaikista listassa olevista tehtava olioista yhden vbox olion
     * @return palauttaa vbox olion
     */
    public VBox vbox_listasta(){
        VBox on_temp = new VBox();
        for (int i = 0; kaikki_tehtavata.size() > i; i++) {
            if (!root_tehtavat.getChildren().contains(kaikki_tehtavata.get(i).yksi_tehtava)) {
                on_temp.getChildren().add(kaikki_tehtavata.get(i).yksi_tehtava);
            }
        }
        return on_temp;
    }

    /**
     * Lataa tiedot tiedostosta
     */

    public void lataa_tiedot(){
        try{
            FileInputStream fi = new FileInputStream(tehtavien_tiedot);
            ObjectInputStream oi = new ObjectInputStream(fi);
            ArrayList<tallentava_tehtava> lista_temp = (ArrayList)oi.readObject();
            for(int i = 0; lista_temp.size() > i; i++){
                tallentava_tehtava t1 = lista_temp.get(i);
                uusi_tehtava t = luo_tehtava(t1.time,t1.month,t1.task);
                kaikki_tehtavata.add(t);
            }
            fi.close();
            oi.close();

        }
        catch (IOException e) {
            System.out.println(e);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tallentaa tiedot tiedostoon
     */
    public void tallenna_tiedot(){
        ArrayList<tallentava_tehtava> lista = new ArrayList<>();
        for (int i = 0; kaikki_tehtavata.size() > i; i++){
            uusi_tehtava temp1 = kaikki_tehtavata.get(i);
            tallentava_tehtava temp2 = new tallentava_tehtava(temp1.time,temp1.month,temp1.task);
            lista.add(temp2);
        }
        try {

            FileOutputStream fo = new FileOutputStream(tehtavien_tiedot);
            ObjectOutputStream oo = new ObjectOutputStream(fo);
            oo.writeObject(lista);

            fo.close();
            oo.close();
        } catch (Exception e) {
        }
    }

    /**
     * Paivittaa stagen niin etta uudet tehtavat nakyvat
     */
    public void paivita_stage(){
        root_tehtavat = new VBox();
        root_tehtavat.getChildren().addAll(b1,vbox_listasta());
        s1 = new Scene(root_tehtavat);
        stage.setScene(s1);
        stage.setWidth(500);
        stage.setHeight(500);
        stage.setTitle("To-do-list");
        stage.show();
    }

    /**
     * Luokka josta tehdaan uudet tehtavat ja josta luodaan uuteen tehtavaan liittyvat graafiset komponentit
     */
    class uusi_tehtava implements Comparable, Serializable{
        /**
         * Kellon aika muuttuja
         */
        private float time;
        /**
         * Paivamaara muuttuja
         */
        private float month;
        /**
         * Tehtava muuttuja
         */
        private String task;

        /**
         * Konsturktori
         * @param t Kellon aika
         * @param pv Paivamaara
         * @param teht Tehtava
         */

        public uusi_tehtava(float t, float pv, String teht) {
            this.time = t;
            this.month = pv;
            this.task = teht;
        }

        /**
         * Hbox josta tehdaan kokonainen tehtava
         */
        public HBox yksi_tehtava = new HBox();
        /**
         * Tehtavan poistamista varten oleva Chekbox
         */
        public CheckBox check = new CheckBox();
        /**
         * Paivamaaran ja kellon ajan omaava Hbox
         */
        public HBox aika = new HBox();
        /**
         * Muokkaa kellon aikaa niin että se on Text muodossa
         */
        public Text klo = new Text();
        /**
         * Muokkaa kuukautta niin että se on Text muodossa
         */
        public Text kuukausi = new Text();
        /**
         * Tehtava Text muodossa
         */

        public Text tehtava = new Text();

        /**
         * Palauttaa ajan
         */
        public float getTime() {
            return time;
        }

        /**
         * Palauttaa paivamaaran
         */

        public Float getMonth() {
            return month;
        }

        /**
         * Palauttaa tehtavan
         */
        public String getTask() {
            return task;
        }

        @Override
        public int compareTo(Object a) {
            uusi_tehtava b = (uusi_tehtava) a;
            if (this.getMonth() > b.getMonth()) {
                if(this.getTime() > b.getTime()){
                    return 1;
                }

                return 1;
            }
            if (this.getMonth() < b.getMonth()) {
                if(this.getTime() < b.getTime()){
                    return -1;
                }
                return -1;
            }
            if(this.getMonth().equals(b.getMonth())){
                if(this.getTime() < b.getTime()){
                    return -1;
                }
            }
            return 1;
        }
    }

}

/**
 * Luokka jota kaytetaan tehtavien tallentamisessa ja tiedostosta lukemisesta
 */
class tallentava_tehtava implements Serializable{
    public float time;
    public float month;
    public String task;
    public tallentava_tehtava(float t, float pv, String teht) {
        this.time = t;
        this.month = pv;
        this.task = teht;
    }

}


