package praticaest1.praticaest1;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gluonhq.maps.*;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import praticaest1.praticaest1.obj.*;
import praticaest1.praticaest1.utility.*;
import java.io.*;
import java.net.http.*;
import java.time.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URLEncoder;

public class HomeController {
    @FXML
    private AnchorPane profileBox,tripBox,homeBox,intoTripBox,base,exploreBox;
    /*BARRA LATERALE*/
    @FXML
    private HBox b1,b2,b3,b4; //Quelli della barra laterale (Lo so nomi terribili ma non ho voglia di cambiarli)
    @FXML
    private Label nomeUtente;
    /*SEZIONE PROFILO*/
    @FXML
    private TextField t1,t2;
    @FXML
    private PasswordField t3;
    @FXML
    private TextArea t4;
    @FXML
    private Button salva,aggiungiNuovoViaggio;
    /*SEZIONE VIAGGI ED ESPLORA*/
    @FXML
    private VBox scrollTrip,scrollExplore;
    @FXML
    private Label nomeViaggio;
    @FXML
    private AnchorPane workTrip;
    @FXML //Ovviamente in tal caso si poteva fare un metodo che otteneva la stringa dal pulsante e poi compieva ciò che compiamo, senza 14 id, ma così ci obblighiamo a mantenere i pulsanti con la stessa stringa
    private Button p1,p2,p3,p4,p5,p6,p7,p11,p21,p31,p41,p51,p61,p71; //Serie 1 -> Viaggi / 11 -> Esplora

    //Dell'utente attuale
    private Utente utenteAttuale;
    private ListaViaggi listaViaggiAttuale;
    private Viaggio viaggioAttuale;
    private List<String> filtriViaggiAttivi;
    private List<String> filtriEsploraAttivi;
    private ViaggioEsplora[] viaggioEsploraGroq,viaggiStandard;

    //Elementi gestionali
    private String URL_BASE;
    private final GestoreHTTP gestoreHTTP=new GestoreHTTP();
    private ObjectMapper mapper=new ObjectMapper();
    private GroqClient groq;

    public void initialize(){
        //Variabili
        Dotenv dotenv= Dotenv.load();
        this.URL_BASE=dotenv.get("URL_BASE_SERVER");
        mapper.registerModule(new JavaTimeModule());
        this.groq=new GroqClient(Dotenv.load().get("GROQ_API_KEY"));
        //Grafica
        animaBottoni();
        this.filtriViaggiAttivi=new ArrayList<>();
        this.filtriEsploraAttivi=new ArrayList<>();
    }

    //Setter e getter
    public Utente getUtenteAttuale() { return utenteAttuale; }
    public void setUtenteAttuale(Utente utenteAttuale) {
        this.utenteAttuale = utenteAttuale;
        this.nomeUtente.setText(utenteAttuale.getNome().trim());
    }

    //gestione delle schermate e delle "animazioni" base
    @FXML
    private void animaBottoni(){
        //Animazione di cambio colore al sovrasto del mouse
        b1.setOnMouseMoved(event -> b1.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;"));
        b1.setOnMouseExited(event -> {
            if (homeBox.isVisible()) b1.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
            else b1.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");
        });
        b2.setOnMouseMoved(event -> b2.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;"));
        b2.setOnMouseExited(event -> {
            if (exploreBox.isVisible()) b2.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
            else b2.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");
        });
        b3.setOnMouseMoved(event -> b3.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;"));
        b3.setOnMouseExited(event -> {
            if (tripBox.isVisible()) b3.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
            else b3.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");
        });
        b4.setOnMouseMoved(event -> b4.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;"));
        b4.setOnMouseExited(event -> {
            if (profileBox.isVisible()) b4.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
            else b4.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");
        });

        //Filtri viaggi
        p1.setOnMouseMoved(event -> p1.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p1.setOnMouseExited(event -> {
            if (this.filtriViaggiAttivi.contains("Piedi")) p1.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p1.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p1.setOnAction(e->gestoreFiltriViaggi("Piedi"));
        p2.setOnMouseMoved(event -> p2.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p2.setOnMouseExited(event -> {
            if (this.filtriViaggiAttivi.contains("Bici")) p2.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p2.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p2.setOnAction(e->gestoreFiltriViaggi("Bici"));
        p3.setOnMouseMoved(event -> p3.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p3.setOnMouseExited(event -> {
            if (this.filtriViaggiAttivi.contains("Auto")) p3.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p3.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p3.setOnAction(e->gestoreFiltriViaggi("Auto"));
        p4.setOnMouseMoved(event -> p4.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p4.setOnMouseExited(event -> {
            if (this.filtriViaggiAttivi.contains("Mezzi pesanti (Camper,Truck)")) p4.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p4.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p4.setOnAction(e->gestoreFiltriViaggi("Mezzi pesanti (Camper,Truck)"));
        p5.setOnMouseMoved(event -> p5.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p5.setOnMouseExited(event -> {
            if (this.filtriViaggiAttivi.contains("Ricollegarsi alla natura")) p5.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p5.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p5.setOnAction(e->gestoreFiltriViaggi("Ricollegarsi alla natura"));
        p6.setOnMouseMoved(event -> p6.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p6.setOnMouseExited(event -> {
            if (this.filtriViaggiAttivi.contains("Rilassarsi")) p6.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p6.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p6.setOnAction(e->gestoreFiltriViaggi("Rilassarsi"));
        p7.setOnMouseMoved(event -> p7.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p7.setOnMouseExited(event -> {
            if (this.filtriViaggiAttivi.contains("Visitare")) p7.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p7.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p7.setOnAction(e->gestoreFiltriViaggi("Visitare"));

        //Filtri esplora
        p11.setOnMouseMoved(event -> p11.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p11.setOnMouseExited(event -> {
            if (this.filtriEsploraAttivi.contains("Piedi")) p11.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p11.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p11.setOnAction(e->gestoreFiltriEsplora("Piedi"));
        p21.setOnMouseMoved(event -> p21.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p21.setOnMouseExited(event -> {
            if (this.filtriEsploraAttivi.contains("Bici")) p21.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p21.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p21.setOnAction(e->gestoreFiltriEsplora("Bici"));
        p31.setOnMouseMoved(event -> p31.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p31.setOnMouseExited(event -> {
            if (this.filtriEsploraAttivi.contains("Auto")) p31.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p31.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p31.setOnAction(e->gestoreFiltriEsplora("Auto"));
        p41.setOnMouseMoved(event -> p41.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p41.setOnMouseExited(event -> {
            if (this.filtriEsploraAttivi.contains("Mezzi pesanti (Camper,Truck)")) p41.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p41.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p41.setOnAction(e->gestoreFiltriEsplora("Mezzi pesanti (Camper,Truck)"));
        p51.setOnMouseMoved(event -> p51.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p51.setOnMouseExited(event -> {
            if (this.filtriEsploraAttivi.contains("Ricollegarsi alla natura")) p51.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p51.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p51.setOnAction(e->gestoreFiltriEsplora("Ricollegarsi alla natura"));
        p61.setOnMouseMoved(event -> p61.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p61.setOnMouseExited(event -> {
            if (this.filtriEsploraAttivi.contains("Rilassarsi")) p61.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p61.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p61.setOnAction(e->gestoreFiltriEsplora("Rilassarsi"));
        p71.setOnMouseMoved(event -> p71.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;"));
        p71.setOnMouseExited(event -> {
            if (this.filtriEsploraAttivi.contains("Visitare")) p71.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else p71.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-padding: 8 16;");
        });
        p71.setOnAction(e->gestoreFiltriEsplora("Visitare"));
    }
    @FXML
    public void goToHome(){ gestSchermate(true,false,false,false); }
    @FXML
    public void goToTrip(){
        // Spinner iniziale (overlay)
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(40, 40);
        spinner.setStyle("-fx-progress-color: #3B82F6;");
        VBox overlaySpinner = new VBox(spinner);
        overlaySpinner.setAlignment(Pos.CENTER);
        overlaySpinner.setStyle("-fx-background-color: rgba(255,255,255,0.6);");
        overlaySpinner.setPrefSize(base.getWidth(), base.getHeight());

        base.getChildren().add(overlaySpinner);

        //Gestione filtri
        this.filtriViaggiAttivi.clear(); //Cancello i possibili rimasugli precedenti
        this.filtriViaggiAttivi.add("Piedi"); //piedi
        this.filtriViaggiAttivi.add("Bici"); //bici
        this.filtriViaggiAttivi.add("Auto"); //auto
        this.filtriViaggiAttivi.add("Mezzi pesanti (Camper,Truck)"); //mezzipesanti
        this.filtriViaggiAttivi.add("Ricollegarsi alla natura"); //riconnettersi
        this.filtriViaggiAttivi.add("Rilassarsi"); //rilassarsi
        this.filtriViaggiAttivi.add("Visitare"); //visitare

        // Task in background per inviare richiesta, avvio con un secondo thread il caricamento così posso mostare sulò thread principale ui lo spinner di caricamento, potrà non essere efficentissimo o il modo migliore ma è quello di cui sono capace ;-)
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    gestSchermate(false,true,false,false);
                    String risposta = gestoreHTTP.inviaRichiestaConParametri(URL_BASE + "getListaViaggi.php",mapper.writeValueAsString(utenteAttuale));
                    Messaggio<List<Viaggio>> m = mapper.readValue(risposta, new TypeReference<Messaggio<List<Viaggio>>>() {});
                    if (!m.getConfermaAzione()) {
                        Platform.runLater(() -> {
                            base.getChildren().remove(overlaySpinner);
                            goToHome();
                        });
                        return null;
                    }else{
                        Platform.runLater(() -> {
                            base.getChildren().remove(overlaySpinner);
                            listaViaggiAttuale =new ListaViaggi(m.getParametro1()); //Converto l'array di viaggi precedente (par 1 di m) nel nostro oggetto lista viaggi
                            popolaScroolPane();
                        });
                    }
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        base.getChildren().remove(overlaySpinner);
                        goToHome();
                    });
                }
                return null;
            }
        };

        new Thread(task).start();
    }
    @FXML
    public void goToProfile(){
        gestSchermate(false,false,true,false);
        t1.setText(utenteAttuale.getNome().trim());
        t2.setText(utenteAttuale.getEmail().trim());
        t3.setText(utenteAttuale.getPsw().trim());
        t4.setText(utenteAttuale.getBio());

        // Listener per abilitare/disabilitare il bottone di salvataggio così che l'utente non faccia salvataggi inutili
        ChangeListener<String> listenerCambiamenti = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            boolean almenoUnaModificaFatta = !t1.getText().equals(utenteAttuale.getNome().trim()) || !t2.getText().equals(utenteAttuale.getEmail().trim()) || !t3.getText().equals(utenteAttuale.getPsw().trim()) || !t4.getText().equals(utenteAttuale.getBio().trim());
            salva.setDisable(!almenoUnaModificaFatta);
        };
        t1.textProperty().addListener(listenerCambiamenti);
        t2.textProperty().addListener(listenerCambiamenti);
        t3.textProperty().addListener(listenerCambiamenti);
        t4.textProperty().addListener(listenerCambiamenti);

    }
    @FXML
    public void goToExplore() {
        // Spinner iniziale (overlay)
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(40, 40);
        spinner.setStyle("-fx-progress-color: #3B82F6;");
        VBox overlaySpinner = new VBox(spinner);
        overlaySpinner.setAlignment(Pos.CENTER);
        overlaySpinner.setStyle("-fx-background-color: rgba(255,255,255,0.6);");
        overlaySpinner.setPrefSize(base.getWidth(), base.getHeight());

        base.getChildren().add(overlaySpinner);

        //Gestione filtri
        this.filtriEsploraAttivi.clear(); //Cancello i possibili rimasugli precedenti
        this.filtriEsploraAttivi.add("Piedi"); //piedi
        this.filtriEsploraAttivi.add("Bici"); //bici
        this.filtriEsploraAttivi.add("Auto"); //auto
        this.filtriEsploraAttivi.add("Mezzi pesanti (Camper,Truck)"); //mezzipesanti
        this.filtriEsploraAttivi.add("Ricollegarsi alla natura"); //riconnettersi
        this.filtriEsploraAttivi.add("Rilassarsi"); //rilassarsi
        this.filtriEsploraAttivi.add("Visitare"); //visitare

        // Task in background per inviare richiesta, avvio con un secondo thread il caricamento così posso mostare sulò thread principale ui lo spinner di caricamento, potrà non essere efficentissimo o il modo migliore ma è quello di cui sono capace ;-)
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    gestSchermate(false,false,false,true);
                    if(listaViaggiAttuale==null){
                        String risposta = gestoreHTTP.inviaRichiestaConParametri(URL_BASE + "getListaViaggi.php",mapper.writeValueAsString(utenteAttuale));
                        Messaggio<List<Viaggio>> m = mapper.readValue(risposta, new TypeReference<Messaggio<List<Viaggio>>>() {});
                        if (!m.getConfermaAzione()) {
                            Platform.runLater(() -> {
                                base.getChildren().remove(overlaySpinner);
                                goToHome();
                            });
                            return null;
                        }
                        listaViaggiAttuale= new ListaViaggi(m.getParametro1());
                    }
                    String risposta2=groq.generaViaggiEsplora(mapper.writeValueAsString(listaViaggiAttuale)).toString();
                    viaggioEsploraGroq = mapper.readValue(risposta2,  ViaggioEsplora[].class );
                    if(viaggiStandard==null)
                        viaggiStandard=mapper.readValue(readJSONFromFile(),ViaggioEsplora[].class);
                    Platform.runLater(() -> {
                        base.getChildren().remove(overlaySpinner);
                        popolaEsplora(viaggioEsploraGroq,viaggiStandard);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        base.getChildren().remove(overlaySpinner);
                        goToHome();
                    });
                }
                return null;
            }
        };

        new Thread(task).start();
    }
    @FXML
    private void gestSchermate(boolean isHomeBoxOn, boolean isTripBoxOn, boolean isProfileBoxOn, boolean isExploreBoxOn){
        homeBox.setDisable(!isHomeBoxOn);
        homeBox.setVisible(isHomeBoxOn);
        //Controllo aggiuntivo data l'iniziale cambio colore del pulsante
        if (homeBox.isVisible()) b1.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
        else b1.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");

        profileBox.setDisable(!isProfileBoxOn);
        profileBox.setVisible(isProfileBoxOn);
        if (profileBox.isVisible()) b4.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
        else b4.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");

        tripBox.setDisable(!isTripBoxOn);
        tripBox.setVisible(isTripBoxOn);
        if (tripBox.isVisible()) b3.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
        else b3.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");

        exploreBox.setDisable(!isExploreBoxOn);
        exploreBox.setVisible(isExploreBoxOn);
        if (exploreBox.isVisible()) b2.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
        else b2.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");

        //Fisso
        intoTripBox.setDisable(true);
        intoTripBox.setVisible(false);
    }

    /*SEZIONE PROFILO*/
    public void salvaInfo(){
        try {
            salva.setDisable(true);
            String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"userSave.php",mapper.writeValueAsString(new UtenteAggiornato(this.utenteAttuale,new Utente(t1.getText(), t2.getText(),t4.getText(),t3.getText())))); //Invio le nuove info al server
            if (mapper.readValue(risposta, Messaggio.class).getConfermaAzione()) { //Tutto a buon fine
                animazioneBottone(salva,"-fx-background-color: #25be5d; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                this.utenteAttuale.aggiorna(t1.getText(), t2.getText(),t4.getText(),t3.getText()); //Salvo le nuove info
                this.nomeUtente.setText(utenteAttuale.getNome().trim()); //Aggiorno l'interfaccia
                goToProfile();
            } else //Problemi
                animazioneBottone(salva, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        } catch (Exception e) {
            animazioneBottone(salva,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        }
    }

    /*SEZIONE ESPLORA*/
    private void popolaEsplora(ViaggioEsplora[] viaggioEsploraGroq, ViaggioEsplora[] viaggioEsploraComune){
        try {
            for(ViaggioEsplora vg:viaggioEsploraGroq){
                scrollExplore.getChildren().add(createCardEsplora(vg.getViaggio(),vg.getCosaVedere(),urlDaUnsplash(vg.getViaggio().getNomeUnivoco()),true));
            }
            for(ViaggioEsplora vg:viaggioEsploraComune){
                if(scrollExplore.getChildren().contains(createCardEsplora(vg.getViaggio(),vg.getCosaVedere(),urlDaUnsplash(vg.getViaggio().getNomeUnivoco()),false)))
                    break;
                else
                    scrollExplore.getChildren().add(createCardEsplora(vg.getViaggio(),vg.getCosaVedere(),urlDaUnsplash(vg.getViaggio().getNomeUnivoco()),false));
            }
        } catch (Exception e) {
            goToHome();
            System.err.println(e.getMessage());
        }
    }
    private HBox createCardEsplora(Viaggio v,String desc,String urlimg,boolean isIAGenerated) {
        // Root
        HBox root = new HBox();
        root.setAlignment(Pos.CENTER_LEFT);
        root.setPrefSize(507, 300);
        root.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 16;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 10, 0, 0, 4);"
        );

        // Immagine
        ImageView imageView = new ImageView(new Image(urlimg, true)); // true = backgroundLoading
        imageView.setFitWidth(80);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        // Spaziatore tra immagine e testo
        Region spacer1 = new Region();
        spacer1.setPrefSize(45, 166);

        // Colonna di destra
        VBox rightBox = new VBox(4);
        rightBox.setPrefSize(318, 166);

        Label titolo = new Label("Destinazione :"+v.getNomeUnivoco()); //data la generazione del viaggio il nome univoco combacierà con la destinazione
        if(isIAGenerated) titolo.setText("Destinazione :"+v.getNomeUnivoco()+" SCELTA PER TE DA SALVINO");
        titolo.setPrefSize(285, 20);
        titolo.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

        Label tipo = new Label("Tipo di viaggio :"+v.getObiettivo()+" Mezzo:"+v.getMezzoUsato());
        tipo.setPrefSize(286, 34);
        tipo.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-text-fill: #4B5563; -fx-font-size: 7px;");

        Label tappe = new Label("Tappe del viaggio: "+v.getItinerario().ottieniStringaTappe());
        tappe.setPrefSize(286, 31);
        tappe.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-text-fill: #4B5563; -fx-font-size: 7px;");

        Label cosaVedere = new Label(desc);
        cosaVedere.setPrefSize(286, 46);
        cosaVedere.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-text-fill: #4B5563; -fx-font-size: 7px;");

        // Riga bottone in basso a destra
        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.BOTTOM_RIGHT);
        buttonRow.setPrefSize(286, 37);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Button btn = new Button("Aggiungi e modifica");
        btn.setPrefSize(130, 16);
        btn.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-text-fill: white;");
        btn.setOnAction(e->{
            goToTrip();
            scrollTrip.getChildren().add(creaViaggioBox(v,this.utenteAttuale.getNome()));
            //Spaziatore
            Pane s = new Pane();
            s.setPrefHeight(14);
            s.setMinHeight(14);
            s.setMaxHeight(14);
            s.setPrefWidth(147);
            s.setMinWidth(147);
            s.setMaxWidth(147);
            scrollTrip.getChildren().add(s);
        });
        buttonRow.getChildren().addAll(spacer2, btn);
        rightBox.getChildren().addAll(titolo, tipo, tappe, cosaVedere, buttonRow);
        root.getChildren().addAll(imageView, spacer1, rightBox);

        return root;
    }
    private void gestoreFiltriEsplora(String categoriaPremuta){
        if(!this.filtriEsploraAttivi.contains(categoriaPremuta))
            this.filtriEsploraAttivi.add(categoriaPremuta);
        else
            this.filtriEsploraAttivi.remove(categoriaPremuta);
        popolaEsplora(viaggioEsploraGroq,viaggiStandard);
    }

    /*SEZIONE VIAGGI*/
    private void gestoreFiltriViaggi(String categoriaPremuta){
        if(!this.filtriViaggiAttivi.contains(categoriaPremuta))
            this.filtriViaggiAttivi.add(categoriaPremuta);
        else
            this.filtriViaggiAttivi.remove(categoriaPremuta);
        popolaScroolPane();
    }
    @FXML
    private void popolaScroolPane(){
        scrollTrip.getChildren().clear();
        for(Viaggio v: listaViaggiAttuale.getList()){
            if(this.filtriViaggiAttivi.contains(v.getObiettivo()) || this.filtriViaggiAttivi.contains(v.getMezzoUsato())){
                scrollTrip.getChildren().add(creaViaggioBox(v,this.utenteAttuale.getNome())); //Escamotage perchè tanto non possono esserci più utenti per la stessa lista viaggi
                //Spaziatore
                Pane s = new Pane();
                s.setPrefHeight(14);
                s.setMinHeight(14);
                s.setMaxHeight(14);
                s.setPrefWidth(147);
                s.setMinWidth(147);
                s.setMaxWidth(147);
                scrollTrip.getChildren().add(s);
            }
        }
    }
    private HBox creaViaggioBox(Viaggio viaggioCorrente, String creatoreXRimozione) {
        // HBox principale
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(15);
        hBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");

        // Effetto ombra
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.05));
        dropShadow.setRadius(10);
        dropShadow.setOffsetY(4);
        hBox.setEffect(dropShadow);

        // Immagine
        ImageView imageView;
        if(viaggioCorrente.getMezzoUsato().equalsIgnoreCase("piedi")) imageView= new ImageView(new Image(getClass().getResourceAsStream("/praticaest1/praticaest1/img/i1.png")));
        else if(viaggioCorrente.getMezzoUsato().equalsIgnoreCase("bici")) imageView= new ImageView(new Image(getClass().getResourceAsStream("/praticaest1/praticaest1/img/i2.png")));
        else if(viaggioCorrente.getMezzoUsato().equalsIgnoreCase("auto")) imageView= new ImageView(new Image(getClass().getResourceAsStream("/praticaest1/praticaest1/img/i3.png")));
        else imageView= new ImageView(new Image(getClass().getResourceAsStream("/praticaest1/praticaest1/img/i4.png")));
        imageView.setFitHeight(36);
        imageView.setFitWidth(36);

        // VBox con le etichette
        VBox vBox = new VBox();
        vBox.setSpacing(4);

        Label titoloLabel = new Label(viaggioCorrente.getNomeUnivoco()+" / "+viaggioCorrente.getObiettivo());
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titoloLabel.setOnMouseClicked(e ->{
            //Accesso alla schermata interna
            tripBox.setDisable(true);
            tripBox.setVisible(false);
            intoTripBox.setVisible(true);
            intoTripBox.setDisable(false);
            //Setto nome ed entro in modalità budget
            this.viaggioAttuale=viaggioCorrente;
            nomeViaggio.setText(viaggioCorrente.getNomeUnivoco());
            sezBudget();
        });

        Label removeLabel = new Label("Elimina");
        removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
        removeLabel.setUnderline(true);
        removeLabel.setOnMouseClicked(e->{ //Rimozione e ricarica
            try {
                listaViaggiAttuale.removeElemento(viaggioCorrente);
                String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"reloadListaViaggi.php",mapper.writeValueAsString(new MessaggioDati<Utente,ListaViaggi>(this.utenteAttuale, listaViaggiAttuale)));
                Messaggio<ListaViaggi> m=mapper.readValue(risposta, Messaggio.class);
                if (m.getConfermaAzione())
                    popolaScroolPane();
                else
                    animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            } catch (Exception ex) {
                animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            }
        });

        vBox.getChildren().addAll(titoloLabel, removeLabel);

        // Aggiunta dei nodi all'HBox
        hBox.getChildren().addAll(imageView, vBox);

        return hBox;
    }
    @FXML
    public void aggiungiNuovoViaggio() {
        VBox baseScroll=scrollTrip;
        // Crea il contenitore principale trasparente
        StackPane overlay = new StackPane();
        overlay.setPrefSize(this.base.getWidth(), this.base.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        // Crea il VBox centrale del popup
        VBox popupBox = new VBox(15);
        popupBox.setAlignment(Pos.TOP_RIGHT);
        popupBox.setPadding(new Insets(20));
        popupBox.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 8;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0.0, 0, 5);"
        );
        popupBox.setPrefWidth(400);
        popupBox.setMaxWidth(400);
        popupBox.setPrefHeight(300);
        popupBox.setMaxHeight(300);

        // Bottone "X" per chiudere
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
        closeButton.setOnAction(e -> {
            this.base.getChildren().remove(overlay);
            baseScroll.setDisable(false);
            aggiungiNuovoViaggio.setDisable(false);
        });

        HBox b0 = new HBox(closeButton);
        b0.setAlignment(Pos.TOP_RIGHT);
        //Elementi
        // Campo: Nome viaggio
        Label l1 = new Label("Inserisci nome del viaggio (Attento che sia univoco)");
        l1.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
        TextField nome = new TextField();
        nome.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
        nome.setPromptText("Nome viaggio");
        VBox b1 = new VBox(5, l1, nome);
        b1.setAlignment(Pos.TOP_LEFT);

        // Campo: Budget massimo
        Label l2 = new Label("Budget massimo da spendere (Puoi modificarlo anche in seguito)");
        l2.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
        TextField budget = new TextField();
        budget.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
        budget.setPromptText("Es. 1000");
        VBox b2 = new VBox(5, l2, budget);
        b2.setAlignment(Pos.TOP_LEFT);

        // Campo: Destinazione e data
        Label l3 = new Label("Destinazione dell’itinerario");
        l3.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
        TextField destinazione = new TextField();
        destinazione.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
        destinazione.setPromptText("Es. Roma, Parigi, Tokyo...");
        VBox b3 = new VBox(5, l3, destinazione);
        b3.setAlignment(Pos.TOP_LEFT);
        Label l4 = new Label("Data di arrivo alla destinazione");
        l4.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
        DatePicker dataArrivo = new DatePicker();
        dataArrivo.setPromptText("Seleziona una data...");
        dataArrivo.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
        VBox b6 = new VBox(5, l4, dataArrivo);
        b4.setAlignment(Pos.TOP_LEFT);

        // Campo: Mezzo di trasporto
        Label lMezzo = new Label("Mezzo di trasporto preferito  (Attento non potrai modificarlo a seguire)");
        lMezzo.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
        ChoiceBox<String> mezzoScelto = new ChoiceBox<>();
        mezzoScelto.getItems().addAll("Piedi", "Bici", "Auto", "Mezzi pesanti (Camper,Truck)");
        mezzoScelto.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
        VBox boxMezzo = new VBox(5, lMezzo, mezzoScelto);
        boxMezzo.setAlignment(Pos.TOP_LEFT);

        // Campo: Obiettivo del viaggio
        Label lObiettivo = new Label("Obiettivo del viaggio (Attento non potrai modificarlo a seguire)");
        lObiettivo.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
        ChoiceBox<String> obiettivoScelto = new ChoiceBox<>();
        obiettivoScelto.getItems().addAll("Ricollegarsi alla natura", "Rilassarsi", "Visitare");
        obiettivoScelto.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
        VBox boxObiettivo = new VBox(5, lObiettivo, obiettivoScelto);
        boxObiettivo.setAlignment(Pos.TOP_LEFT);

        // Pulsante Salva
        Button saveButton = new Button("Salva");
        saveButton.setStyle(
                "-fx-background-color: #3B82F6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8 16;" +
                        "-fx-text-fill: white;"
        );

        saveButton.setOnAction(e -> {
            // Spinner iniziale
            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setMaxSize(40, 40);
            spinner.setStyle("-fx-progress-color: #3B82F6;");
            VBox overlaySpinner = new VBox(spinner);
            overlaySpinner.setAlignment(Pos.CENTER);
            overlaySpinner.setStyle("-fx-background-color: rgba(255,255,255,0.6);");
            overlaySpinner.setPrefSize(base.getWidth(), base.getHeight());

            base.getChildren().add(overlaySpinner);
            baseScroll.setDisable(true);
            aggiungiNuovoViaggio.setDisable(true);

            // Task in background per inviare richiesta
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        // Crea nuovo viaggio e invia richiesta
                        listaViaggiAttuale.addElemento(
                                new Viaggio(
                                        nome.getText().trim(),
                                        mezzoScelto.getValue(),
                                        obiettivoScelto.getValue(),
                                        new Budget(Double.parseDouble(budget.getText().trim())),
                                        new Itinerario(destinazione.getText().trim(), dataArrivo.getValue()),
                                        new ListaElementi()
                                )
                        );
                        String risposta = gestoreHTTP.inviaRichiestaConParametri(URL_BASE + "reloadListaViaggi.php", mapper.writeValueAsString(new MessaggioDati<Utente, ListaViaggi>(utenteAttuale, listaViaggiAttuale)));
                        Messaggio<ListaViaggi> m = mapper.readValue(risposta, Messaggio.class);

                        Platform.runLater(() -> { //DOPO aver caricato lo spinner posso accedere alle altre impostazioni
                            base.getChildren().remove(overlaySpinner);
                            baseScroll.setDisable(false);
                            aggiungiNuovoViaggio.setDisable(false);

                            if (m.getConfermaAzione()) {
                                base.getChildren().remove(overlay);
                                baseScroll.setDisable(false);
                                aggiungiNuovoViaggio.setDisable(false);
                                popolaScroolPane();
                            } else {
                                animazioneBottone(
                                        saveButton,
                                        "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;",
                                        "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;"
                                );
                            }
                        });

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Platform.runLater(() -> {
                            base.getChildren().remove(overlaySpinner);
                            baseScroll.setDisable(false);
                            aggiungiNuovoViaggio.setDisable(false);
                            animazioneBottone(
                                    saveButton,
                                    "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;",
                                    "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;"
                            );
                        });
                    }
                    return null;
                }
            };

            new Thread(task).start();
        });


        HBox b4 = new HBox(saveButton);
        b4.setAlignment(Pos.CENTER_RIGHT);

        // Inserimento
        popupBox.getChildren().addAll(b0, b1,boxObiettivo,boxMezzo, b2, b3,b6, b4);
        overlay.getChildren().add(popupBox);
        this.base.getChildren().add(overlay);
        baseScroll.setDisable(true);
        aggiungiNuovoViaggio.setDisable(true);
    }


    @FXML
    public void sezBudget(){
        workTrip.getChildren().clear();
        workTrip.getChildren().setAll(creaGraficaBudget()); //Aggiunta della grafica spezzata
    }
    @FXML
    private SplitPane creaGraficaBudget() {
        // === LABELS ===
        Label titolo = new Label("Budget");
        titolo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label spesoLabel = new Label("Budget Speso: " + viaggioAttuale.getBudget().getBudgetSpeso());
        if(viaggioAttuale.getBudget().getBudgetSpeso()<viaggioAttuale.getBudget().getBudgetIniziale())
            spesoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: green;");
        else if(viaggioAttuale.getBudget().getBudgetSpeso()>viaggioAttuale.getBudget().getBudgetIniziale()) {
            spesoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: red;");
        } else
            spesoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: orange;");
        spesoLabel.setWrapText(true);

        Label pattuitoLabel = new Label("Budget Pattuito: " + viaggioAttuale.getBudget().getBudgetIniziale());
        pattuitoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        pattuitoLabel.setWrapText(true);

        Label incrementoLabel = new Label("Vuoi incrementare il budget pattuito?");
        incrementoLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        incrementoLabel.setWrapText(true);

        // === SPINNER IMPORTO ===
        Spinner<Integer> importoSpinner = new Spinner<>();
        importoSpinner.setPrefWidth(130);
        importoSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0, 10));

        // === BOTTONI "+" e "-" ===
        Button plusButton = new Button("+");
        plusButton.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-text-fill: white;");
        plusButton.setOnAction(e -> {
            int val = importoSpinner.getValue();
            if (val <= 0) {
                animazioneBottone(plusButton, "-fx-background-color: #BE2538;", "-fx-background-color: #3B82F6;");
            } else {
                viaggioAttuale.getBudget().aggiungiNuovoBudget(val);
                if (!salvaViaggioCorrente()) {
                    viaggioAttuale.getBudget().rimuoviBudget(val);
                    animazioneBottone(plusButton, "-fx-background-color: #BE2538;", "-fx-background-color: #3B82F6;");
                } else
                    sezBudget();
            }
        });

        Button minusButton = new Button("-");
        minusButton.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 6; -fx-text-fill: black;");
        minusButton.setOnAction(e -> {
            int val = importoSpinner.getValue();
            if (val <= 0) {
                animazioneBottone(minusButton, "-fx-background-color: #BE2538;", "-fx-background-color: #E5E7EB;");
            } else {
                viaggioAttuale.getBudget().rimuoviBudget(val);
                if (!salvaViaggioCorrente()) {
                    viaggioAttuale.getBudget().aggiungiNuovoBudget(val);
                    animazioneBottone(minusButton, "-fx-background-color: #BE2538;", "-fx-background-color: #E5E7EB;");
                } else
                    sezBudget();
            }
        });

        HBox controlloBudgetBox = new HBox(10, plusButton, minusButton);
        controlloBudgetBox.setAlignment(Pos.CENTER);

        VBox destraBox = new VBox();

        // === BOTTONE NUOVA SPESA ===
        Button aggiungiSpesa = new Button("+ Spesa");
        aggiungiSpesa.setAlignment(Pos.CENTER);
        aggiungiSpesa.setWrapText(true);
        aggiungiSpesa.setPrefSize(131, 44);
        aggiungiSpesa.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-text-fill: white;");
        aggiungiSpesa.setOnAction(e -> mostraPopupNuovaSpesa(aggiungiSpesa));

        // === VBOX SINISTRA ===
        VBox sinistraBox = new VBox(10, titolo, spesoLabel, pattuitoLabel, incrementoLabel, importoSpinner, controlloBudgetBox, aggiungiSpesa);
        sinistraBox.setPadding(new Insets(10));
        sinistraBox.setPrefWidth(250);
        sinistraBox.setAlignment(Pos.TOP_LEFT);
        sinistraBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(sinistraBox, Priority.ALWAYS);

        // === VBOX DESTRA ===
        destraBox.setPadding(new Insets(10));
        destraBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        popolaListaAcquisti(destraBox,aggiungiSpesa);
        VBox.setVgrow(destraBox, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(sinistraBox, destraBox);
        splitPane.setDividerPositions(0.3);
        splitPane.setPrefSize(800, 600); // Imposta una dimensione iniziale decente
        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Lascia che cresca

        // Dopo che lo SplitPane è stato aggiunto alla scena così evitamo problemi di caricamento
        for (Node divider : splitPane.lookupAll(".split-pane-divider")) {
            divider.setMouseTransparent(true); // Blocca il trascinamento
            divider.setVisible(false);
        }
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        AnchorPane.setTopAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        return splitPane;
    }
    private void mostraPopupNuovaSpesa(Button bottoneDInnesco) {
        AnchorPane baseScroll = workTrip;

        StackPane overlay = new StackPane();
        overlay.setPrefSize(this.base.getWidth(), this.base.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        VBox popupBox = new VBox(10);
        popupBox.setPadding(new Insets(20));
        popupBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        popupBox.setPrefWidth(400);
        popupBox.setMaxWidth(400);
        popupBox.setPrefHeight(300);
        popupBox.setMaxHeight(300);
        popupBox.setAlignment(Pos.TOP_CENTER);

        Button chiudi = new Button("X");
        chiudi.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
        chiudi.setOnAction(e -> {
            this.base.getChildren().remove(overlay);
            baseScroll.setDisable(false);
            bottoneDInnesco.setDisable(false);
        });

        Label motivazioneLabel = new Label("Motivazione:");
        TextField motivo = new TextField();
        motivo.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");

        Label spesaLabel = new Label("Importo:");
        Spinner<Double> spesa = new Spinner<>();
        spesa.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000.0, 0.0, 10.0));

        Button inserisci = new Button("Inserisci");
        inserisci.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white;");
        inserisci.setOnAction(ez -> {
            try {
                viaggioAttuale.getBudget().aggiungiSpesa(spesa.getValue());
                viaggioAttuale.getBudget().aggiungiRendicontazione(motivo.getText(),spesa.getValue());
                if (salvaViaggioCorrente()) {
                    this.base.getChildren().remove(overlay);
                    baseScroll.setDisable(false);
                    bottoneDInnesco.setDisable(false);
                    sezBudget();
                } else {
                    viaggioAttuale.getBudget().rimuoviSpesa(spesa.getValue());
                    viaggioAttuale.getBudget().rimuoviRendicontazione(motivo.getText(),spesa.getValue());
                    animazioneBottone(inserisci, "-fx-background-color: #BE2538;", "-fx-background-color: #3B82F6;");
                }
            } catch (Exception ex) {
                animazioneBottone(inserisci, "-fx-background-color: #BE2538;", "-fx-background-color: #3B82F6;");
            }
        });

        HBox topBar = new HBox(chiudi);
        topBar.setAlignment(Pos.TOP_RIGHT);
        HBox bottomBar = new HBox(inserisci);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);

        popupBox.getChildren().addAll(topBar, motivazioneLabel, motivo, spesaLabel, spesa, bottomBar);
        overlay.getChildren().add(popupBox);
        this.base.getChildren().add(overlay);
        baseScroll.setDisable(true);
        bottoneDInnesco.setDisable(true);
    }

    @FXML
    private void popolaListaAcquisti(VBox vBox, Button innesco){
        vBox.getChildren().clear();
        for (MotivoSpesa coppiaMotivoSpesa : viaggioAttuale.getBudget().getTieniConto()) {
            //Spaziatore
            Pane s = new Pane();
            s.setPrefHeight(14);
            s.setMinHeight(14);
            s.setMaxHeight(14);
            s.setPrefWidth(147);
            s.setMinWidth(147);
            s.setMaxWidth(147);
            vBox.getChildren().add(s);

            vBox.getChildren().add(creaAcquistoMotivoBox(vBox,coppiaMotivoSpesa,innesco));
        }
    }
    private HBox creaAcquistoMotivoBox(VBox listaGenerale,MotivoSpesa coppiaMotivoSpesa, Button innesco) {
        // HBox principale
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(15);
        hBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");

        // Effetto ombra
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.05));
        dropShadow.setRadius(10);
        dropShadow.setOffsetY(4);
        hBox.setEffect(dropShadow);

        // VBox con le etichette
        VBox vBox = new VBox();
        vBox.setSpacing(4);

        Label titoloLabel = new Label(coppiaMotivoSpesa.getMotivazione()+"\s"+coppiaMotivoSpesa.getCifra()+"\s \uD83D\uDCB6");
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label removeLabel = new Label("Rimuovi Spesa");
        removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
        removeLabel.setOnMouseClicked(e->{ //Rimozione e ricarica
            try {
                //Rimozione della cifra e della rendicontazione
               viaggioAttuale.getBudget().rimuoviSpesa(coppiaMotivoSpesa.getCifra());
               viaggioAttuale.getBudget().rimuoviRendicontazione(coppiaMotivoSpesa.getMotivazione(),coppiaMotivoSpesa.getCifra());
               if (salvaViaggioCorrente()) {
                   sezBudget();
               }else{
                   viaggioAttuale.getBudget().aggiungiSpesa(coppiaMotivoSpesa.getCifra());
                   viaggioAttuale.getBudget().aggiungiRendicontazione(coppiaMotivoSpesa.getMotivazione(),coppiaMotivoSpesa.getCifra());
               }
              } catch (Exception ex) {
                animazioneBottone(innesco,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            }
        });

        vBox.getChildren().addAll(titoloLabel, removeLabel);
        hBox.getChildren().add(vBox);

        return hBox;
    }


    @FXML
    public void sezItinerario(){
        workTrip.getChildren().clear();
        workTrip.getChildren().setAll(creaGraficaItinerario());
    }
    @FXML
    private SplitPane creaGraficaItinerario() {
        Label l0 = new Label("Itinerario");
        l0.setPrefSize(100, 25); // dimensioni più compatte
        l0.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label l1 = new Label("Tappe attuali : " + viaggioAttuale.getItinerario().getTappe().size());
        l1.setPrefSize(120, 20);
        l1.setWrapText(true);
        l1.setStyle("-fx-font-size: 14px; -fx-font-weight: normal;");


        VBox dxVBox = new VBox();

        Button aggiungiTappa = new Button("+ Tappa");
        aggiungiTappa.setPrefSize(120, 30);
        aggiungiTappa.setWrapText(true);
        aggiungiTappa.setAlignment(Pos.CENTER);
        aggiungiTappa.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 6 12;");
        aggiungiTappa.setTextFill(Color.WHITE);
        aggiungiTappa.setOnAction(e -> {
            AnchorPane baseScroll=workTrip;
            // Crea il contenitore principale trasparente
            StackPane overlay = new StackPane();
            overlay.setPrefSize(this.base.getWidth(), this.base.getHeight());
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

            // Crea il VBox centrale del popup
            VBox popupBox = new VBox(10);
            popupBox.setAlignment(Pos.TOP_RIGHT);
            popupBox.setPadding(new Insets(20));
            popupBox.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                            "-fx-background-radius: 8;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0.0, 0, 5);"
            );
            popupBox.setPrefWidth(400);
            popupBox.setMaxWidth(400);
            popupBox.setPrefHeight(300);
            popupBox.setMaxHeight(300);

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                this.base.getChildren().remove(overlay);
                baseScroll.setDisable(false);
                aggiungiTappa.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            //Elementi
            Label l11 = new Label("Inserisci destinazione o nome della Tappa:");
            l11.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextField nome = new TextField();
            nome.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            nome.setPromptText("Perugia");
            VBox b1 = new VBox(5, l11, nome);
            b1.setAlignment(Pos.TOP_LEFT);
            Label l21 = new Label("Inserisci la data d'arrivo o partenza");
            l21.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            DatePicker datePicker=new DatePicker();
            datePicker.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            VBox b2 = new VBox(5, l21, datePicker);
            b2.setAlignment(Pos.TOP_LEFT);

            // Pulsante Inserisci
            Button inserisciBottone = new Button("Inserisci");
            inserisciBottone.setStyle(
                    "-fx-background-color: #3B82F6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 8 16;" +
                            "-fx-text-fill: white;"
            );

            inserisciBottone.setOnAction(ezz -> {
                try {
                    viaggioAttuale.getItinerario().aggiungiTappa(new Tappa(nome.getText(), datePicker.getValue()));
                    if (salvaViaggioCorrente()) {
                        this.base.getChildren().remove(overlay);
                        baseScroll.setDisable(false);
                        aggiungiTappa.setDisable(false);
                        sezItinerario();
                    }else {
                        viaggioAttuale.getItinerario().rimuoviTappa(new Tappa(nome.getText(), datePicker.getValue()));
                        animazioneBottone(inserisciBottone, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                    }
                } catch (Exception ex) {
                    animazioneBottone(inserisciBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                }
            });

            HBox b5 = new HBox(inserisciBottone);
            b5.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            popupBox.getChildren().addAll(b0, b1, b2, b5);
            overlay.getChildren().add(popupBox);
            this.base.getChildren().add(overlay);
            baseScroll.setDisable(true);
            aggiungiTappa.setDisable(true);

        });

        // VBox sinistra con tutte le label e bottoni
        VBox sxVBox = new VBox(8); // spazio verticale 8 px tra elementi
        sxVBox.setPrefWidth(140);
        sxVBox.setPadding(new Insets(10)); // padding interno per distanziare contenuti
        sxVBox.getChildren().addAll(l0, l1, aggiungiTappa);

        // VBox destra
        dxVBox.setPrefSize(545, 200);
        dxVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        creaGraficaDxTappe(dxVBox);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(sxVBox, dxVBox);
        splitPane.setDividerPositions(0.3);
        splitPane.setPrefSize(800, 600); // Imposta una dimensione iniziale decente
        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Lascia che cresca
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        AnchorPane.setTopAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        return splitPane;
    }
    @FXML
    private void creaGraficaDxTappe(VBox vBox){
        vBox.getChildren().clear();
        //Creazione mappa
        Label l0 = new Label("Mappa dell'itinerario");
        l0.setPrefHeight(28); // più compatta
        l0.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        MapView mappa = creaMappa();
        mappa.setPrefHeight(400); // <-- aumenta altezza preferita
        mappa.setMinHeight(300);  // <-- evita che venga schiacciata troppo
        mappa.setMaxHeight(Double.MAX_VALUE); // permette crescita
        mappa.setPrefWidth(600); // opzionale, se vuoi una larghezza fissa
        mappa.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");

        Button apriInGoogleMaps = new Button("Vedi in GMaps");
        apriInGoogleMaps.setStyle(
                "-fx-background-color: #3B82F6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8 16;" +
                        "-fx-text-fill: white;"
        );
        apriInGoogleMaps.setOnAction(e -> {
            String url = creaURLGoogleMaps(viaggioAttuale.getItinerario().getTappe());
            try {
                java.awt.Desktop.getDesktop().browse(new URI(url)); //Apro nel browser di sistema
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Spacer flessibile
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox hBox = new HBox(10, l0, spacer, apriInGoogleMaps);
        hBox.setPadding(new Insets(10));
        hBox.setAlignment(Pos.CENTER_LEFT);

        VBox contenitoreMappa = new VBox(10, hBox, mappa);
        contenitoreMappa.setPadding(new Insets(10));
        VBox.setVgrow(mappa, Priority.ALWAYS);

        //Creazione Calendario
        Label l1 = new Label("Date selezionate (Clicca per vedere di più)");
        l1.setPrefHeight(32);
        l1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        viaggioAttuale.getItinerario().ordinaTappe(); //Ordiniamo sempre non si sa mai
        CalendarioMensile calendario=new CalendarioMensile(YearMonth.from(viaggioAttuale.getItinerario().getDateTappe().getFirst()),viaggioAttuale.getItinerario(),base,workTrip,groq);
        VBox c2 = new VBox(10, l1, calendario);
        c2.setPadding(new Insets(10));
        VBox.setVgrow(calendario, Priority.ALWAYS);

        //Creazione lista gestionale tappe
        Label l2 = new Label("Modifica le Tappe Scelte");
        l2.setPrefHeight(32);
        l2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        VBox c3 = new VBox(10, l2);
        c3.setPadding(new Insets(10));
        //Aggiunta
        vBox.getChildren().addAll(contenitoreMappa,creaSpaziatore(false),c2,creaSpaziatore(false),c3);

        viaggioAttuale.getItinerario().ordinaTappe(); //Le ordiniamo cronologicamente prima dell'inserimento
        for (Tappa tappa : viaggioAttuale.getItinerario().getTappe()){
            //Spaziatore
            Pane s = new Pane();
            s.setPrefHeight(14);
            s.setMinHeight(14);
            s.setMaxHeight(14);
            s.setPrefWidth(147);
            s.setMinWidth(147);
            s.setMaxWidth(147);
            vBox.getChildren().add(s);

            vBox.getChildren().add(creaAggiuntaTappaBox(tappa));
        }
    }
    private HBox creaAggiuntaTappaBox(Tappa tappa) {
        // HBox principale
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(15);
        hBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");

        // Effetto ombra
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.05));
        dropShadow.setRadius(10);
        dropShadow.setOffsetY(4);
        hBox.setEffect(dropShadow);

        // VBox con le etichette
        VBox vBox = new VBox();
        vBox.setSpacing(4);

        Label titoloLabel = new Label(tappa.getNome()+"\s"+tappa.getData());
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-strikethrough: "+tappa.getData().isBefore(LocalDate.now())+";");
        titoloLabel.setOnMouseClicked(e -> {
            Pane baseScroll = workTrip;
            // Crea il contenitore principale trasparente
            AnchorPane overlay = new AnchorPane();
            overlay.setPrefSize(base.getWidth(), base.getHeight());
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

            // Crea il VBox centrale del popup
            VBox popupBox = new VBox(10);
            popupBox.setPadding(new Insets(20));
            popupBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
            popupBox.setPrefWidth(400);
            popupBox.setMaxWidth(400);
            popupBox.setPrefHeight(300);
            popupBox.setMaxHeight(300);

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                base.getChildren().remove(overlay);
                baseScroll.setDisable(false);
                titoloLabel.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            Label labelNome = new Label("Luogo:");
            TextField campoNome = new TextField(tappa.getNome());
            campoNome.setPromptText("Nome del luogo");
            campoNome.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");
            VBox boxNome = new VBox(5, labelNome, campoNome);
            Label labelData = new Label("Giorno:");
            DatePicker campoData = new DatePicker(tappa.getData());
            campoData.setPromptText("Seleziona una data");
            campoData.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");
            VBox boxData = new VBox(5, labelData, campoData);

            Label labelLongitudine = new Label("Longitudine (In caso di modifiche alla destinazione verrà modifica a seguito):");
            TextField campoLongitudine = new TextField(String.valueOf(tappa.getLongitudine()));
            campoLongitudine.setDisable(true);
            VBox boxLongitudine = new VBox(5, labelLongitudine, campoLongitudine);
            Label labelLatitudine = new Label("Latitudine (In caso di modifiche alla destinazione verrà modifica a seguito):");
            TextField campoLatitudine = new TextField(String.valueOf(tappa.getLatitudine()));
            campoLatitudine.setDisable(true);
            VBox boxLatitudine = new VBox(5, labelLatitudine, campoLatitudine);
            Button salva = new Button("Salva");
            salva.setStyle(
                    "-fx-background-color: #3B82F6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 8 16;" +
                            "-fx-text-fill: white;"
            );
            salva.setDisable(true);


            // --- Spinner per feedback di caricamento ---
            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setMaxSize(40, 40);
            spinner.setStyle("-fx-progress-color: #3B82F6;");
            spinner.setVisible(false); // Inizialmente invisibile
            spinner.managedProperty().bind(spinner.visibleProperty());

            HBox actionBox = new HBox(10, spinner, salva);
            actionBox.setAlignment(Pos.CENTER_RIGHT);

            // Listener per abilitare/disabilitare il pulsante Salva
            Runnable checkChanges = () -> {
                boolean changed = false;
                if (!campoNome.getText().equals(tappa.getNome())) {
                    if (campoNome.getText().contains(" ")){
                        tappa.setNome(campoNome.getText().trim()); //Modifica a seguire anche long e lat
                        campoLatitudine.setText(String.valueOf(tappa.getLatitudine()));
                        campoLongitudine.setText(String.valueOf(tappa.getLongitudine()));
                    } else
                        tappa.setOnlyNome(campoNome.getText().trim());
                    changed = true;
                }
                if (campoData.getValue() != null && !campoData.getValue().equals(tappa.getData())) {
                    tappa.setData(campoData.getValue());
                    changed = true;
                }
                salva.setDisable(!changed); // Abilita se changed è true, disabilita altrimenti
            };

            campoNome.textProperty().addListener((obs, oldVal, newVal) -> checkChanges.run());
            campoNome.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    tappa.setNome(campoNome.getText().trim()); //Modifica a seguire anche long e lat
                    campoLatitudine.setText(String.valueOf(tappa.getLatitudine()));
                    campoLongitudine.setText(String.valueOf(tappa.getLongitudine()));
                    salva.setDisable(false);
                }
            });
            campoData.valueProperty().addListener((obs, oldVal, newVal) -> checkChanges.run());
            salva.setOnAction(ezz -> {
                spinner.setVisible(true);
                salva.setDisable(true);
                closeButton.setDisable(true);
                campoNome.setDisable(true);
                campoData.setDisable(true);
                campoLongitudine.setDisable(true);
                campoLatitudine.setDisable(true);

                // Operazione di salvataggio in un Task in background
                Task<Boolean> saveTask = new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return salvaViaggioCorrente();
                    }
                };
                saveTask.setOnSucceeded(event -> {
                    Platform.runLater(() -> {
                        // Nascondi spinner e riabilita elementi
                        spinner.setVisible(false);
                        closeButton.setDisable(false);
                        campoNome.setDisable(false);
                        campoData.setDisable(false);
                        checkChanges.run();

                        if (saveTask.getValue()) {
                            base.getChildren().remove(overlay);
                            baseScroll.setDisable(false);
                            titoloLabel.setDisable(false);
                            sezItinerario();
                        } else {
                            animazioneBottone(salva, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                        }
                    });
                });
                saveTask.setOnFailed(event -> {
                    Platform.runLater(() -> {
                        // Nascondi spinner e riabilita elementi
                        spinner.setVisible(false);
                        closeButton.setDisable(false);
                        campoNome.setDisable(false);
                        campoData.setDisable(false);
                        salva.setDisable(false);
                        animazioneBottone(salva, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                    });
                });

                new Thread(saveTask).start();
            });

            HBox b4 = new HBox(salva);
            b4.setAlignment(Pos.CENTER_RIGHT);

            // Composizione finale
            popupBox.getChildren().addAll(b0, boxNome, boxData, boxLongitudine, boxLatitudine, b4,actionBox);
            popupBox.setLayoutX((base.getWidth() - popupBox.getPrefWidth()) / 2);
            popupBox.setLayoutY(100);
            overlay.getChildren().add(popupBox);
            base.getChildren().add(overlay);
            baseScroll.setDisable(true);
            titoloLabel.setDisable(true);
        });

        Label removeLabel = new Label("Rimuovi tappa");
        if(viaggioAttuale.getItinerario().getTappe().size()==1) { //C'è solo questa tappa
            removeLabel.setText("Rimuovi tappa (Prima inserisci un'altra tappa)");
            removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px; -fx-strikethrough:true;");
        } else
            removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
        removeLabel.setOnMouseClicked(e->{//Rimozione e ricarica
            if (viaggioAttuale.getItinerario().getTappe().size()!=1){
                try {
                    viaggioAttuale.getItinerario().rimuoviTappa(tappa);
                    if (salvaViaggioCorrente())
                        sezItinerario(); //riaggiorno anche mappa e calendari
                    else
                        viaggioAttuale.getItinerario().aggiungiTappa(tappa);
                } catch (Exception ex) {
                    removeLabel.setStyle("-fx-text-fill: #ff0505; -fx-font-size: 12px;");
                    PauseTransition pausa = new PauseTransition(Duration.seconds(3));
                    pausa.setOnFinished(ez -> { removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;"); });
                    pausa.play();
                }
            } else{
                removeLabel.setStyle("-fx-text-fill: #ff0505; -fx-font-size: 12px;");
                PauseTransition pausa = new PauseTransition(Duration.seconds(3));
                pausa.setOnFinished(ez -> { removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;"); });
                pausa.play();
            }
        });

        vBox.getChildren().addAll(titoloLabel, removeLabel);
        hBox.getChildren().add(vBox);

        return hBox;
    }
    private MapView creaMappa() {
        viaggioAttuale.getItinerario().ordinaTappe();
        List<Tappa> tappe = viaggioAttuale.getItinerario().getTappe();

        MapView mapView = new MapView();
        mapView.setZoom(6);
        // Centra la mappa sulla prima tappa
        if (!tappe.isEmpty()) {
            mapView.setCenter(new MapPoint(tappe.getFirst().getLatitudine(), tappe.getFirst().getLongitudine()));
        } else {
            mapView.setCenter(new MapPoint(45.666, 9.666)); // Bergamo predefinita anche se non succederà mai che ci siano 0 tappe contemporaneamente
        }

        if (tappe.size() >= 2) {
            PercorsoMappa percorsoMappa = new PercorsoMappa(tappe);

            // Ora, usiamo il GraphHopperService per ottenere un percorso dettagliato
            GraphHopperService graphHopperService = new GraphHopperService();
            try {
                List<MapPoint> puntiDettagliati = graphHopperService.getRoute(tappe, viaggioAttuale.getMezzoUsato());
                if (!puntiDettagliati.isEmpty()) {
                    percorsoMappa.setPuntiPercorsoDettagliati(puntiDettagliati); // Passa i punti dettagliati alla mappa
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("Errore durante il recupero del percorso da GraphHopper: " + e.getMessage());
            }

            mapView.addLayer(percorsoMappa);
        }
        return mapView;
    }

    @FXML
    public void sezElementi(){
        workTrip.getChildren().clear();
        workTrip.getChildren().setAll(creaGraficaElementi());
    }
    @FXML
    private SplitPane creaGraficaElementi() {
        Label l0 = new Label("Valigie");
        l0.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label l1 = new Label("Elementi aggiunti: " + viaggioAttuale.getListaElementi().getElementiTot());
        l1.setWrapText(true);
        l1.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Label l3 = new Label("Elementi acquistati: " + viaggioAttuale.getListaElementi().getElementiAcquistati());
        l3.setWrapText(true);
        if(viaggioAttuale.getListaElementi().getElementiAcquistati()<viaggioAttuale.getListaElementi().getElementiTot())
            l3.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: red;");
        else if(viaggioAttuale.getListaElementi().getElementiAcquistati()==viaggioAttuale.getListaElementi().getElementiTot()) {
            l3.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: green;");
        }


        VBox dxVBox = new VBox();
        Button aggiungiNuovoElemento = new Button("+ Elemento");
        aggiungiNuovoElemento.setWrapText(true);
        aggiungiNuovoElemento.setAlignment(Pos.CENTER);
        aggiungiNuovoElemento.setPrefSize(131, 44);
        aggiungiNuovoElemento.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        aggiungiNuovoElemento.setTextFill(Color.WHITE);
        aggiungiNuovoElemento.setOnAction(e -> {
            AnchorPane baseScroll=workTrip;
            // Crea il contenitore principale trasparente
            StackPane overlay = new StackPane();
            overlay.setPrefSize(this.base.getWidth(), this.base.getHeight());
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

            // Crea il VBox centrale del popup
            VBox popupBox = new VBox(10);
            popupBox.setAlignment(Pos.TOP_RIGHT);
            popupBox.setPadding(new Insets(20));
            popupBox.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                            "-fx-background-radius: 8;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0.0, 0, 5);"
            );
            popupBox.setPrefWidth(400);
            popupBox.setMaxWidth(400);
            popupBox.setPrefHeight(300);
            popupBox.setMaxHeight(300);

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                this.base.getChildren().remove(overlay);
                baseScroll.setDisable(false);
                aggiungiNuovoElemento.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            //Elementi
            Label l11 = new Label("Inserisci l'elemento");
            l11.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextField nome = new TextField();
            nome.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            nome.setPromptText("Pomata");
            VBox b1 = new VBox(5, l11, nome);
            b1.setAlignment(Pos.TOP_LEFT);
            Label l21 = new Label("Inserisci la quantità");
            l21.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            Spinner<Integer> quantita = new Spinner<>();
            quantita.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            quantita.setPromptText("100.0");
            quantita.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 25, 0, 1));
            VBox b2 = new VBox(5, l21, quantita);
            b2.setAlignment(Pos.TOP_LEFT);
            Label l111 = new Label("Inserisci il luogo d'acquisto");
            l111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextField luogo = new TextField();
            luogo.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            luogo.setPromptText("Farmacia");
            VBox b3 = new VBox(5, l111, luogo);
            b3.setAlignment(Pos.TOP_LEFT);
            Label l1111 = new Label("Inserisci una breve descrizione");
            l1111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextArea descrizione = new TextArea();
            descrizione.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            descrizione.setPromptText("Per irritazione al canale rettico");
            VBox b4 = new VBox(5, l1111, descrizione);
            b4.setAlignment(Pos.TOP_LEFT);

            // Pulsante Inserisci
            Button inserisciBottone = new Button("Inserisci");
            inserisciBottone.setStyle(
                    "-fx-background-color: #3B82F6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 8 16;" +
                            "-fx-text-fill: white;"
            );

            inserisciBottone.setOnAction(ezz -> {
                try {
                    viaggioAttuale.getListaElementi().addElemento(new Elemento(nome.getText(), descrizione.getText(), luogo.getText(), quantita.getValue()));
                    if (salvaViaggioCorrente()) {
                        this.base.getChildren().remove(overlay);
                        baseScroll.setDisable(false);
                        aggiungiNuovoElemento.setDisable(false);
                        sezElementi();
                    }else {
                        animazioneBottone(inserisciBottone, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                        viaggioAttuale.getListaElementi().removeElemento(new Elemento(nome.getText(), descrizione.getText(), luogo.getText(), quantita.getValue()));
                    }
                } catch (Exception ex) {
                    animazioneBottone(inserisciBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                }
            });

            HBox b5 = new HBox(inserisciBottone);
            b5.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            popupBox.getChildren().addAll(b0, b1, b2, b3, b4,b5);
            overlay.getChildren().add(popupBox);
            this.base.getChildren().add(overlay);
            baseScroll.setDisable(true);
            aggiungiNuovoElemento.setDisable(true);

        });

        // VBox sinistra con tutte le label e bottoni
        VBox sxVBox = new VBox(10, l0, l1, l3, aggiungiNuovoElemento);
        sxVBox.setPadding(new Insets(10));
        sxVBox.setPrefWidth(250);
        sxVBox.setAlignment(Pos.TOP_LEFT);
        sxVBox.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(sxVBox, Priority.ALWAYS);

        // VBox destra
        dxVBox.setPrefSize(545, 200);
        dxVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        popolaListaElementi(dxVBox);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(sxVBox, dxVBox);
        splitPane.setDividerPositions(0.3);
        splitPane.setPrefSize(800, 600); // Imposta una dimensione iniziale decente
        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Lascia che cresca
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        AnchorPane.setTopAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        return splitPane;
    }
    @FXML
    private void popolaListaElementi(VBox vBox){
        vBox.getChildren().clear();
        //Inserire elementi consigliati ia
        try {
            Elemento[] elementiConsigliati = mapper.readValue(groq.generaTreElementi(mapper.writeValueAsString(viaggioAttuale)).toString(), Elemento[].class);
            vBox.getChildren().add(creaAggiuntaElementoScorrere(elementiConsigliati));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //Inserimento degli altri elementi dell'utente
        for (Elemento elemento : viaggioAttuale.getListaElementi().getList()){
            //Spaziatore
            Pane s = new Pane();
            s.setPrefHeight(14);
            s.setMinHeight(14);
            s.setMaxHeight(14);
            s.setPrefWidth(147);
            s.setMinWidth(147);
            s.setMaxWidth(147);
            vBox.getChildren().add(s);

            vBox.getChildren().add(creaAggiuntaElementoBox(elemento));
        }
    }
    @FXML
    private HBox creaAggiuntaElementoBox(Elemento elemento) {
        // HBox principale
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setSpacing(15);
        hBox.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");

        // Effetto ombra
        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.05));
        dropShadow.setRadius(10);
        dropShadow.setOffsetY(4);
        hBox.setEffect(dropShadow);

        // VBox con le etichette
        VBox vBox = new VBox();
        vBox.setSpacing(4);

        Label titoloLabel = new Label(elemento.getNome()+"\s Quantità: "+elemento.getQuantita());
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-strikethrough: "+elemento.isAcquistato()+";");
        titoloLabel.setOnMouseClicked(e ->{
            AnchorPane baseScroll=workTrip;
            StackPane overlay = new StackPane();
            overlay.setPrefSize(this.base.getWidth(), this.base.getHeight());
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

            // Crea il VBox centrale del popup
            VBox popupBox = new VBox(10);
            popupBox.setAlignment(Pos.TOP_RIGHT);
            popupBox.setPadding(new Insets(20));
            popupBox.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                            "-fx-background-radius: 8;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0.0, 0, 5);"
            );
            popupBox.setPrefWidth(400);
            popupBox.setMaxWidth(400);
            popupBox.setPrefHeight(300);
            popupBox.setMaxHeight(300);

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                this.base.getChildren().remove(overlay);
                baseScroll.setDisable(false);
                titoloLabel.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            //Elementi
            Label l1 = new Label("Nome:");
            TextField c1 = new TextField(elemento.getNome());
            c1.setPromptText("Pomata");
            c1.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");
            VBox bb1 = new VBox(5, l1, c1);
            Label l2 = new Label("Luogo d'Acquisto:");

            TextField c2 = new TextField(elemento.getLuogoAcquisto());
            c2.setPromptText("Farmacia");
            c2.setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");
            VBox bb2 = new VBox(5, l2, c2);

            Label l3 = new Label("Quantità:");
            Spinner<Integer> c3 = new Spinner<>();
            c3.setPrefWidth(130);
            c3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 25, elemento.getQuantita(), 1));
            VBox bb3 = new VBox(5, l3, c3);

            Label l4 = new Label("Descrizione");
            TextArea c4 = new TextArea(elemento.getDescrizione());
            c4.setDisable(true);
            VBox bb4 = new VBox(5, l4, c4);
            CheckBox ck=new CheckBox("Acquistato");
            ck.setSelected(elemento.isAcquistato());
            Button salva = new Button("Salva");
            ck.setOnAction(ez->{
                try {
                    viaggioAttuale.getListaElementi().setElementoAcquistato(elemento,ck.isSelected());
                    if (salvaViaggioCorrente()){
                        this.base.getChildren().remove(overlay);
                        baseScroll.setDisable(false);
                        titoloLabel.setDisable(false);
                        sezElementi();
                    } else {
                        animazioneBottone(salva, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                        elemento.setAcquistato(!ck.isSelected());
                    }
                } catch (Exception ex) {
                    elemento.setAcquistato(!ck.isSelected());
                    animazioneBottone(salva, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                }
            });
            VBox bb5 = new VBox(5,ck);
            bb5.setAlignment(Pos.TOP_LEFT);

            // Pulsante Inserisci
            salva.setStyle(
                    "-fx-background-color: #3B82F6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 8 16;" +
                            "-fx-text-fill: white;"
            );

            // --- Spinner per feedback di caricamento ---
            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setMaxSize(40, 40);
            spinner.setStyle("-fx-progress-color: #3B82F6;");
            spinner.setVisible(false); // Inizialmente invisibile
            spinner.managedProperty().bind(spinner.visibleProperty());

            HBox actionBox = new HBox(10, spinner, salva);
            actionBox.setAlignment(Pos.CENTER_RIGHT);

            // Listener per abilitare/disabilitare il pulsante Salva
            Runnable checkChanges = () -> {
                boolean changed = false;
                if (!c1.getText().trim().equals(elemento.getNome())) {
                    elemento.setNome(c1.getText().trim());
                    changed = true;
                }
                if (!c2.getText().trim().equals(elemento.getLuogoAcquisto())) {
                    elemento.setLuogoAcquisto(c2.getText().trim());
                    changed = true;
                }
                if (c3.getValue()!=elemento.getQuantita()) {
                    elemento.setQuantita(c3.getValue());
                    changed = true;
                }
                if (!c4.getText().trim().equals(elemento.getDescrizione())) {
                    elemento.setDescrizione(c4.getText().trim());
                    changed = true;
                }
                this.salva.setDisable(!changed); // Abilita se changed è true, disabilita altrimenti
            };

            c1.textProperty().addListener((obs, oldVal, newVal) -> checkChanges.run());
            c2.textProperty().addListener((obs, oldVal, newVal) -> checkChanges.run());
            c3.valueProperty().addListener((obs, oldVal, newVal) -> checkChanges.run());
            c4.textProperty().addListener((obs, oldVal, newVal) -> checkChanges.run());
            salva.setOnAction(ezz -> {
                spinner.setVisible(true);
                this.salva.setDisable(true);
                closeButton.setDisable(true);
                c1.setDisable(true);
                c2.setDisable(true);
                c3.setDisable(true);
                c4.setDisable(true);

                // Operazione di salvataggio in un Task in background
                Task<Boolean> saveTask = new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return salvaViaggioCorrente();
                    }
                };
                saveTask.setOnSucceeded(event -> {
                    Platform.runLater(() -> {
                        // Nascondi spinner e riabilita elementi
                        spinner.setVisible(false);
                        closeButton.setDisable(false);
                        c1.setDisable(false);
                        c2.setDisable(false);
                        c3.setDisable(false);
                        c4.setDisable(false);
                        checkChanges.run();

                        if (saveTask.getValue()) {
                            this.base.getChildren().remove(overlay);
                            baseScroll.setDisable(false);
                            sezElementi();
                        } else {
                            animazioneBottone(salva, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                        }
                    });
                });
                saveTask.setOnFailed(event -> {
                    Platform.runLater(() -> {
                        // Nascondi spinner e riabilita elementi
                        spinner.setVisible(false);
                        closeButton.setDisable(false);
                        c1.setDisable(false);
                        c2.setDisable(false);
                        c3.setDisable(false);
                        c4.setDisable(false);
                        salva.setDisable(false);
                        animazioneBottone(salva, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                    });
                });

                new Thread(saveTask).start();
            });

            HBox b5 = new HBox(salva);
            b5.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            popupBox.getChildren().addAll(b0,bb1,bb2,bb3,bb4,bb5,b5);
            overlay.getChildren().add(popupBox);
            this.base.getChildren().add(overlay);
            baseScroll.setDisable(true);
            titoloLabel.setDisable(true);
        });
        Label removeLabel = new Label("Rimuovi");
        removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
        removeLabel.setOnMouseClicked(e->{ //Rimozione e ricarica
            try {
                viaggioAttuale.getListaElementi().removeElemento(elemento);
                if (salvaViaggioCorrente()) {
                    sezElementi();
                }else
                    viaggioAttuale.getListaElementi().addElemento(elemento);
            } catch (Exception ex) {
                animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            }
        });

        vBox.getChildren().addAll(titoloLabel, removeLabel);
        hBox.getChildren().add(vBox);

        return hBox;
    }
    @FXML
    private HBox creaAggiuntaElementoScorrere(Elemento[] elementi) {
        HBox root = new HBox(12);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 16;");
        root.setMinHeight(80);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.05));
        dropShadow.setRadius(10);
        dropShadow.setOffsetY(4);
        root.setEffect(dropShadow);

        // --- Stato corrente ---
        IntegerProperty index = new SimpleIntegerProperty(0);

        // --- Frecce ---
        Button prev = new Button("<");
        prev.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
        Button next = new Button(">");
        next.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");

        prev.setOnAction(e -> {
            if (index.get() > 0) index.set(index.get() - 1);
        });
        next.setOnAction(e -> {
            if (index.get() < elementi.length - 1) index.set(index.get() + 1);
        });

        // Disabilita frecce ai bordi
        prev.disableProperty().bind(index.lessThanOrEqualTo(0));
        next.disableProperty().bind(index.greaterThanOrEqualTo(elementi.length - 1));

        // --- Centro (contenuto elemento) ---
        VBox center = new VBox(4);
        center.setAlignment(Pos.CENTER_LEFT);

        Label titoloLabel = new Label();
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label l3 = new Label("SALVINO CONSIGLIA ⬆");
        l3.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");

        // Dettagli / Edit
        Label editLabel = new Label("Dettagli / Aggiungi");
        editLabel.setStyle("-fx-text-fill: #3B82F6; -fx-font-size: 12px; -fx-underline: true;");

        // Aggiorna UI in base all'indice
        Runnable refresh = () -> {
            if (elementi.length == 0) {
                titoloLabel.setText("Nessun elemento");
                l3.setDisable(true);
                editLabel.setDisable(true);
                return;
            }
            Elemento el = elementi[index.get()];
            titoloLabel.setText(el.getNome());
            titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-strikethrough: " + el.isAcquistato() + ";");

            l3.setDisable(false);
            editLabel.setDisable(false);
        };

        // Azioni
        editLabel.setOnMouseClicked(e -> {
            if (elementi.length == 0) return;
            Elemento el = elementi[index.get()];
            AnchorPane baseScroll=workTrip;
            // Crea il contenitore principale trasparente
            StackPane overlay = new StackPane();
            overlay.setPrefSize(this.base.getWidth(), this.base.getHeight());
            overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

            // Crea il VBox centrale del popup
            VBox popupBox = new VBox(10);
            popupBox.setAlignment(Pos.TOP_RIGHT);
            popupBox.setPadding(new Insets(20));
            popupBox.setStyle(
                    "-fx-background-color: #FFFFFF;" +
                            "-fx-background-radius: 8;" +
                            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0.0, 0, 5);"
            );
            popupBox.setPrefWidth(400);
            popupBox.setMaxWidth(400);
            popupBox.setPrefHeight(300);
            popupBox.setMaxHeight(300);

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                this.base.getChildren().remove(overlay);
                baseScroll.setDisable(false);
                editLabel.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            //Elementi
            Label l11 = new Label("Inserisci l'elemento");
            l11.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextField nome = new TextField(el.getNome());
            nome.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            nome.setPromptText("Pomata");
            VBox b1 = new VBox(5, l11, nome);
            b1.setAlignment(Pos.TOP_LEFT);
            Label l21 = new Label("Inserisci la quantità");
            l21.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            Spinner<Integer> quantita = new Spinner<>();
            quantita.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            quantita.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 25, el.getQuantita(), 1));
            VBox b2 = new VBox(5, l21, quantita);
            b2.setAlignment(Pos.TOP_LEFT);
            Label l111 = new Label("Inserisci il luogo d'acquisto");
            l111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextField luogo = new TextField(el.getLuogoAcquisto());
            luogo.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            luogo.setPromptText("Farmacia");
            VBox b3 = new VBox(5, l111, luogo);
            b3.setAlignment(Pos.TOP_LEFT);
            Label l1111 = new Label("Inserisci una breve descrizione");
            l1111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextArea descrizione = new TextArea(el.getDescrizione());
            descrizione.setStyle("-fx-background-radius: 8; -fx-padding: 10; -fx-background-color: white;");
            descrizione.setPromptText("Per irritazione al canale rettico");
            VBox b4 = new VBox(5, l1111, descrizione);
            b4.setAlignment(Pos.TOP_LEFT);

            // Pulsante Inserisci
            Button inserisciBottone = new Button("Inserisci");
            inserisciBottone.setStyle(
                    "-fx-background-color: #3B82F6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 8 16;" +
                            "-fx-text-fill: white;"
            );

            inserisciBottone.setOnAction(ezz -> {
                try {
                    viaggioAttuale.getListaElementi().addElemento(new Elemento(nome.getText(), descrizione.getText(), luogo.getText(), quantita.getValue()));
                    if (salvaViaggioCorrente()) {
                        this.base.getChildren().remove(overlay);
                        baseScroll.setDisable(false);
                        editLabel.setDisable(false);
                        sezElementi();
                    }else {
                        animazioneBottone(inserisciBottone, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                        viaggioAttuale.getListaElementi().removeElemento(new Elemento(nome.getText(), descrizione.getText(), luogo.getText(), quantita.getValue()));
                    }
                } catch (Exception ex) {
                    animazioneBottone(inserisciBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                }
            });

            HBox b5 = new HBox(inserisciBottone);
            b5.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            popupBox.getChildren().addAll(b0, b1, b2, b3, b4,b5);
            overlay.getChildren().add(popupBox);
            this.base.getChildren().add(overlay);
            baseScroll.setDisable(true);
            editLabel.setDisable(true);

        });

        center.getChildren().addAll(titoloLabel, editLabel, l3);
        index.addListener((obs, oldV, newV) -> refresh.run());
        refresh.run();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        root.getChildren().addAll(prev, center, spacer, next);
        return root;
    }



    /*METODI GENERALI*/
    @FXML
    private void animazioneBottone(Button b,String stylePost, String stylePre){
        b.setStyle(stylePost);
        PauseTransition pausa = new PauseTransition(Duration.seconds(3));
        pausa.setOnFinished(e -> { b.setStyle(stylePre); });
        pausa.play();
    }
    private boolean salvaViaggioCorrente(){
        try {
            String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"reloadViaggio.php",mapper.writeValueAsString(new MessaggioDati<Utente,Viaggio>(this.utenteAttuale, this.viaggioAttuale)));
            Messaggio<ListaViaggi> m=mapper.readValue(risposta, Messaggio.class);
            return m.getConfermaAzione();
        } catch (Exception ex) {
            return false;
        }
    }
    private Pane creaSpaziatore(boolean isBig){
        Pane s = new Pane();
        double altezza = isBig ? 49 : 29;
        s.setPrefHeight(altezza);
        s.setMinHeight(altezza);
        s.setMaxHeight(altezza);
        s.setPrefWidth(147);
        s.setMinWidth(147);
        s.setMaxWidth(147);
        return s;
    }
    private String creaURLGoogleMaps(List<Tappa> tappe) {
        StringBuilder url = new StringBuilder("https://www.google.com/maps/dir/");
        for (Tappa t : tappe) {
            url.append(t.getLatitudine()).append(",").append(t.getLongitudine()).append("/");
        }
        return url.toString();
    }
    private String urlDaUnsplash(String soggettoFotoRicercata) throws IOException, InterruptedException {
        // 1) Leggo la key dal .env
        String accessKey = Dotenv.load().get("UNSPLASH_ACCESS_KEY");
        if (accessKey == null || accessKey.isBlank()) {
            throw new IllegalStateException("UNSPLASH_ACCESS_KEY non trovato nel file .env");
        }

        // 2) Costruisco la richiesta come specificato dalle docs di unsplash
        String endpoint = "https://api.unsplash.com/search/photos?per_page=1&query="
                + URLEncoder.encode(soggettoFotoRicercata, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Client-ID " + accessKey)
                .header("Accept-Version", "v1")
                .GET()
                .build();

        // 3) Invio e leggo la risposta
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Errore Unsplash: HTTP " + response.statusCode() + " - " + response.body());
        }

        // 4) Parsing JSON e ritorno dell'URL
        JsonNode risposta = new ObjectMapper().readTree(response.body()).path("results"); //Vedi risposta tipo sottostante per capire
        if (!risposta.isArray() || risposta.isEmpty()) return null;
        JsonNode normale = risposta.get(0).path("urls").path("regular");
        return normale.isMissingNode() ? null : normale.asText();

        /**
         * {
         *   "total": 3451,
         *   "total_pages": 3451,
         *   "results": [
         *     {
         *       "id": "eOLpJytrbsQ",
         *       "created_at": "2014-11-18T14:35:36-05:00",
         *       "width": 4000,
         *       "height": 3000,
         *       "color": "#A7A2A1",
         *       "likes": 286,
         *       "user": {
         *         "id": "Ul0QVz12Goo",
         *         "username": "ugmonk",
         *         "name": "Jeff Sheldon",
         *         "first_name": "Jeff",
         *         "last_name": "Sheldon",
         *         "portfolio_url": "http://ugmonk.com/",
         *         "profile_image": {
         *           "small": "https://images.unsplash.com/profile-1441298803695-accd94000cac?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=32&w=32&s=7cfe3b93750cb0c93e2f7caec08b5a41",
         *           "medium": "https://images.unsplash.com/profile-1441298803695-accd94000cac?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=64&w=64&s=5a9dc749c43ce5bd60870b129a40902f",
         *           "large": "https://images.unsplash.com/profile-1441298803695-accd94000cac?ixlib=rb-0.3.5&q=80&fm=jpg&crop=faces&cs=tinysrgb&fit=crop&h=128&w=128&s=32085a077889586df88bfbe406692202"
         *         },
         *         "links": {
         *           "self": "https://api.unsplash.com/users/ugmonk",
         *           "html": "http://unsplash.com/@ugmonk",
         *           "photos": "https://api.unsplash.com/users/ugmonk/photos",
         *           "likes": "https://api.unsplash.com/users/ugmonk/likes"
         *         }
         *       },
         *       "urls": {
         *         "raw": "https://images.unsplash.com/photo-1416339306562-f3d12fefd36f",
         *         "full": "https://hd.unsplash.com/photo-1416339306562-f3d12fefd36f",
         *         "regular": "https://images.unsplash.com/photo-1416339306562-f3d12fefd36f?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=1080&fit=max&s=92f3e02f63678acc8416d044e189f515",
         *         "small": "https://images.unsplash.com/photo-1416339306562-f3d12fefd36f?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=400&fit=max&s=263af33585f9d32af39d165b000845eb",
         *         "thumb": "https://images.unsplash.com/photo-1416339306562-f3d12fefd36f?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=8aae34cf35df31a592f0bef16e6342ef"
         *       },
         *       "links": {
         *         "self": "https://api.unsplash.com/photos/eOLpJytrbsQ",
         *         "html": "http://unsplash.com/photos/eOLpJytrbsQ",
         *         "download": "http://unsplash.com/photos/eOLpJytrbsQ/download"
         *       }
         *     }
         *   ]
         * }*/
    }
    private String readJSONFromFile(){
        String s="";
        try (BufferedReader rd=new BufferedReader(new FileReader("WAYpp/CODE/pratica-est-1/src/main/resources/praticaest1/praticaest1/information/viaggiStandard.json"))){
            String next;
            while ((next=rd.readLine())!=null)
                s=s+next;
        } catch (Exception e) {
           System.err.println(e.getMessage());
        }
        return s;
    }

}


//Classi usate per lo scambio di messaggi con server http
class Messaggio<T>{
    private Boolean confermaAzione;
    private T parametro1;
    private Integer parametro2;

    public Messaggio() {}
    public Messaggio(Boolean confermaAzione, T parametro1, Integer parametro2) {
        this.confermaAzione = confermaAzione;
        this.parametro1 = parametro1;
        this.parametro2 = parametro2;
    }

    public Boolean getConfermaAzione() { return confermaAzione; }
    public void setConfermaAzione(Boolean confermaAzione) { this.confermaAzione = confermaAzione; }
    public T getParametro1() { return parametro1; }
    public void setParametro1(T parametro1) { this.parametro1 = parametro1; }
    public Integer getParametro2() { return parametro2; }
    public void setParametro2(Integer parametro2) { this.parametro2 = parametro2; }
}
class MessaggioDati<T,K>{
    private T p1;
    private K p2;

    public MessaggioDati() {}
    public MessaggioDati(T p1, K p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public T getP1() { return p1; }
    public void setP1(T p1) { this.p1 = p1; }
    public K getP2() { return p2; }
    public void setP2(K p2) { this.p2 = p2; }
}

class UtenteAggiornato{
    private Utente vecchio;
    private Utente nuovo;

    public UtenteAggiornato() {}
    public UtenteAggiornato(Utente vecchio, Utente nuovo) {
        this.vecchio = vecchio;
        this.nuovo = nuovo;
    }

    public Utente getVecchio() { return vecchio; }
    public void setVecchio(Utente vecchio) { this.vecchio = vecchio; }
    public Utente getNuovo() { return nuovo; }
    public void setNuovo(Utente nuovo) { this.nuovo = nuovo; }
}
class ViaggioEsplora{
    private Viaggio viaggio;
    private String cosaVedere;
    private String urlImmagine;

    public ViaggioEsplora(){}
    public ViaggioEsplora(Viaggio viaggio, String cosaVedere, String urlImmagine) {
        this.viaggio = viaggio;
        this.cosaVedere = cosaVedere;
        this.urlImmagine = urlImmagine;
    }

    public Viaggio getViaggio() { return viaggio; }
    public void setViaggio(Viaggio viaggio) { this.viaggio = viaggio; }
    public String getCosaVedere() { return cosaVedere; }
    public void setCosaVedere(String cosaVedere) { this.cosaVedere = cosaVedere; }
    public String getUrlImmagine() { return urlImmagine; }
    public void setUrlImmagine(String urlImmagine) { this.urlImmagine = urlImmagine; }
}