package praticaest1.praticaest1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import praticaest1.praticaest1.obj.*;
import praticaest1.praticaest1.utility.*;

import java.io.IOException;

public class HomeController {
    @FXML
    private AnchorPane profileBox,tripBox,homeBox;
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
    /*SEZIONE VIAGGI*/
    @FXML
    private VBox scrollTrip;

    //Elementi gestionali
    private Utente utente;
    private String URL_BASE;
    private final GestoreHTTP gestoreHTTP=new GestoreHTTP();
    private final ObjectMapper mapper=new ObjectMapper();

    public void initialize(){
        //Variabili
        Dotenv dotenv= Dotenv.load();
        this.URL_BASE=dotenv.get("URL_BASE_SERVER");
        //Grafica
        animaBottoni();
    }

    //Setter e getter
    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) {
        this.utente = utente;
        this.nomeUtente.setText(utente.getNome().trim());
    }

    //gestione delle schermate e delle "animazioni" base
    @FXML
    private void animaBottoni(){
        //Animazione di cambio colore al sovrasto del mouse
        b1.setOnMouseMoved(event -> b1.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;"));
        b1.setOnMouseExited(event -> b1.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;"));
        b2.setOnMouseMoved(event -> b2.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;"));
        b2.setOnMouseExited(event -> b2.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;"));
        b3.setOnMouseMoved(event -> b3.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;"));
        b3.setOnMouseExited(event -> b3.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;"));
        b4.setOnMouseMoved(event -> b4.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;"));
        b4.setOnMouseExited(event -> b4.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;"));
    }
    @FXML
    public void goToHome(){ gestSchermate(true,false,false); }
    @FXML
    public void goToTrip(){
        try {
            gestSchermate(false,true,false);
            String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"getListaViaggi.php",mapper.writeValueAsString(this.utente));
            Messaggio<ListaViaggi> m=mapper.readValue(risposta, Messaggio.class); //Lo converto nella variabile messaggio
            if (!m.getConfermaAzione())
                animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else{
                popolaScroolPane(m.getParametro1());
            }
        } catch (Exception e) {
            animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        }
    }
    @FXML
    public void goToProfile(){
        gestSchermate(false,false,true);
        t1.setText(utente.getNome().trim());
        t2.setText(utente.getEmail().trim());
        t3.setText(utente.getPsw().trim());
        t4.setText(utente.getBio());

        // Listener per abilitare/disabilitare il bottone di salvataggio così che l'utente non faccia salvataggi inutili
        ChangeListener<String> listenerCambiamenti = (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            boolean almenoUnaModificaFatta = !t1.getText().equals(utente.getNome().trim()) || !t2.getText().equals(utente.getEmail().trim()) || !t3.getText().equals(utente.getPsw().trim()) || !t4.getText().equals(utente.getBio().trim());
            salva.setDisable(!almenoUnaModificaFatta);
        };
        t1.textProperty().addListener(listenerCambiamenti);
        t2.textProperty().addListener(listenerCambiamenti);
        t3.textProperty().addListener(listenerCambiamenti);
        t4.textProperty().addListener(listenerCambiamenti);

    }
    @FXML
    private void gestSchermate(boolean isHomeBoxOn, boolean isTripBoxOn, boolean isProfileBoxOn){
        if (isHomeBoxOn){
            homeBox.setDisable(false);
            homeBox.setVisible(true);
            b1.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
        } else {
            homeBox.setDisable(true);
            homeBox.setVisible(false);
            b1.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");
        }
        if (isProfileBoxOn){
            profileBox.setDisable(false);
            profileBox.setVisible(true);
            b4.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
        } else {
            profileBox.setDisable(true);
            profileBox.setVisible(false);
            b4.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");
        }
        if (isTripBoxOn){
            tripBox.setDisable(false);
            tripBox.setVisible(true);
            b3.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
        } else {
            tripBox.setDisable(true);
            tripBox.setVisible(false);
            b3.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");
        }
    }

    /*SEZIONE PROFILO*/
    public void salvaInfo(){
        try {
            salva.setDisable(true);
            String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"userSave.php",mapper.writeValueAsString(new UtenteAggiornato(this.utente,new Utente(t1.getText(), t2.getText(),t4.getText(),t3.getText())))); //Invio le nuove info al server
            if (mapper.readValue(risposta, Messaggio.class).getConfermaAzione()) { //Tutto a buon fine
                animazioneBottone(salva,"-fx-background-color: #25be5d; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                this.utente.aggiorna(t1.getText(), t2.getText(),t4.getText(),t3.getText()); //Salvo le nuove info
                this.nomeUtente.setText(utente.getNome().trim()); //Aggiorno l'interfaccia
                goToProfile();
            } else //Problemi
                animazioneBottone(salva,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        } catch (Exception e) {
            animazioneBottone(salva,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        }
    }

    /*SEZIONE VIAGGI*/
    @FXML
    private void popolaScroolPane(ListaViaggi listaViaggi){
        scrollTrip.getChildren().clear();
        for(Viaggio v:listaViaggi.getList()){
            scrollTrip.getChildren().add(creaViaggioBox(listaViaggi,v,this.utente.getNome())); //Escamotage perchè tanto non possono esserci più utenti per la stessa lista viaggi
        }
    }
    private HBox creaViaggioBox(ListaViaggi listaViaggi,Viaggio viaggioCorrente, String creatoreXRimozione) {
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
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/img/plane-icon.png")));
        imageView.setFitHeight(36);
        imageView.setFitWidth(36);

        // VBox con le etichette
        VBox vBox = new VBox();
        vBox.setSpacing(4);

        Label titoloLabel = new Label(viaggioCorrente.getNomeUnivoco()); //TODO: INSERIRE AZIONI MENU VERO E PROPRIO
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label removeLabel = new Label(creatoreXRimozione+" cancella");
        removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
        removeLabel.setOnMouseClicked(e->{ //Rimozione e ricarica
            try {
                listaViaggi.removeElemento(viaggioCorrente);
                String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"reloadListaViaggi.php",mapper.writeValueAsString(new MessaggioDati<Utente,ListaViaggi>(this.utente,listaViaggi)));
                Messaggio<ListaViaggi> m=mapper.readValue(risposta, Messaggio.class);
                if (m.getConfermaAzione())
                    popolaScroolPane(m.getParametro1());
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
    //TODO: INSERIRE RICHIESTA CREAZIONE NUOVO VIAGGIO, creare fxml dinamico con nome

    /*METODI GENERALI*/
    @FXML
    private void animazioneBottone(Button b,String stylePost, String stylePre){
        b.setStyle(stylePost);
        PauseTransition pausa = new PauseTransition(Duration.seconds(3));
        pausa.setOnFinished(e -> { b.setStyle(stylePre); });
        pausa.play();
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