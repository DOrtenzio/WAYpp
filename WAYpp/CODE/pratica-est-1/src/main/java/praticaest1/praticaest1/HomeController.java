package praticaest1.praticaest1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gluonhq.maps.*;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import praticaest1.praticaest1.obj.*;
import praticaest1.praticaest1.utility.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class HomeController {
    @FXML
    private AnchorPane profileBox,tripBox,homeBox,intoTripBox;
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
    @FXML
    private Label nomeViaggio;
    @FXML
    private Pane workTrip;

    //Dell'utente attuale
    private Utente utenteAttuale;
    private ListaViaggi listaViaggiAttuale;
    private Viaggio viaggioAttuale;
    //Elementi gestionali
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
            String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"getListaViaggi.php",mapper.writeValueAsString(this.utenteAttuale));
            Messaggio<ListaViaggi> m=mapper.readValue(risposta, Messaggio.class); //Lo converto nella variabile messaggio
            if (!m.getConfermaAzione())
                animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else{
                this.listaViaggiAttuale =m.getParametro1();
                popolaScroolPane();
            }
        } catch (Exception e) {
            animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        }
    }
    @FXML
    public void goToProfile(){
        gestSchermate(false,false,true);
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
            String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"userSave.php",mapper.writeValueAsString(new UtenteAggiornato(this.utenteAttuale,new Utente(t1.getText(), t2.getText(),t4.getText(),t3.getText())))); //Invio le nuove info al server
            if (mapper.readValue(risposta, Messaggio.class).getConfermaAzione()) { //Tutto a buon fine
                animazioneBottone(salva,"-fx-background-color: #25be5d; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                this.utenteAttuale.aggiorna(t1.getText(), t2.getText(),t4.getText(),t3.getText()); //Salvo le nuove info
                this.nomeUtente.setText(utenteAttuale.getNome().trim()); //Aggiorno l'interfaccia
                goToProfile();
            } else //Problemi
                animazioneBottone(salva,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        } catch (Exception e) {
            animazioneBottone(salva,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        }
    }

    /*SEZIONE VIAGGI*/
    @FXML
    private void popolaScroolPane(){
        scrollTrip.getChildren().clear();
        for(Viaggio v: listaViaggiAttuale.getList()){
            scrollTrip.getChildren().add(creaViaggioBox(v,this.utenteAttuale.getNome())); //Escamotage perchè tanto non possono esserci più utenti per la stessa lista viaggi
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
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/img/plane-icon.png")));
        imageView.setFitHeight(36);
        imageView.setFitWidth(36);

        // VBox con le etichette
        VBox vBox = new VBox();
        vBox.setSpacing(4);

        Label titoloLabel = new Label(viaggioCorrente.getNomeUnivoco());
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titoloLabel.setOnMouseClicked(e ->{
            //Accesso alla schermata interna
            tripBox.setDisable(true);
            tripBox.setVisible(false);
            intoTripBox.setVisible(true);
            intoTripBox.setDisable(false);
            //Setto nome ed entro in modalità budget
            nomeViaggio.setText(viaggioCorrente.getNomeUnivoco());
            sezBudget();
        });

        Label removeLabel = new Label(creatoreXRimozione+" cancella");
        removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
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
        VBox base=scrollTrip;
        // Crea il contenitore principale trasparente
        AnchorPane overlay = new AnchorPane();
        overlay.setPrefSize(base.getWidth(), base.getHeight());
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

        // Bottone "X" per chiudere
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
        closeButton.setOnAction(e -> {
            base.getChildren().remove(overlay);
            base.setDisable(false);
        });

        HBox b0 = new HBox(closeButton);
        b0.setAlignment(Pos.TOP_RIGHT);
        //Elementi
        // Campo: Nome viaggio
        Label l1 = new Label("Inserisci nome del viaggio (Attento che sia univoco)");
        l1.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
        TextField nome = new TextField();
        nome.setPromptText("Nome viaggio");
        VBox b1 = new VBox(5, l1, nome);
        b1.setAlignment(Pos.TOP_LEFT);

        // Campo: Budget massimo
        Label l2 = new Label("Budget massimo da spendere (Puoi modificarlo anche in seguito)");
        l2.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
        TextField budget = new TextField();
        budget.setPromptText("Es. 1000");
        VBox b2 = new VBox(5, l2, budget);
        b2.setAlignment(Pos.TOP_LEFT);

        // Campo: Destinazione
        Label l3 = new Label("Destinazione dell’itinerario");
        l3.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
        TextField destinazione = new TextField();
        destinazione.setPromptText("Es. Roma, Parigi, Tokyo...");
        VBox b3 = new VBox(5, l3, destinazione);
        b3.setAlignment(Pos.TOP_LEFT);

        // Pulsante Salva
        Button saveButton = new Button("Salva");
        saveButton.setStyle(
                "-fx-background-color: #3B82F6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 8 16;" +
                        "-fx-text-fill: white;"
        );

        saveButton.setOnAction(e -> {
            try {
                listaViaggiAttuale.addElemento(new Viaggio(nome.getText().trim(),new Budget(Double.parseDouble(budget.getText().trim())),new Itinerario(destinazione.getText().trim()),new ListaElementi()));
                String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"reloadListaViaggi.php",mapper.writeValueAsString(new MessaggioDati<Utente,ListaViaggi>(this.utenteAttuale, listaViaggiAttuale)));
                Messaggio<ListaViaggi> m=mapper.readValue(risposta, Messaggio.class);
                if (m.getConfermaAzione()) {
                    base.setDisable(false);
                    popolaScroolPane();
                }else
                    animazioneBottone(saveButton,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            } catch (Exception ex) {
                animazioneBottone(saveButton,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            }
        });

        HBox b4 = new HBox(saveButton);
        b4.setAlignment(Pos.CENTER_RIGHT);

        // Inserimento
        // Composizione finale
        popupBox.getChildren().addAll(b0, b1, b2, b3, b4);
        popupBox.setLayoutX((base.getWidth() - popupBox.getPrefWidth()) / 2);
        popupBox.setLayoutY(100);
        overlay.getChildren().add(popupBox);
        base.getChildren().add(overlay);
        base.setDisable(true);
    }


    @FXML
    public void sezBudget(){
        workTrip.getChildren().clear();
        workTrip.getChildren().add(creaGraficaBudget()); //Aggiunta della grafica spezzata
    }
    @FXML
    private SplitPane creaGraficaBudget() {
        // Label: "Budget"
        Label l0 = new Label("Budget");
        l0.setPrefSize(131, 32);
        l0.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Spacer Pane
        Pane s1 = new Pane();
        s1.setPrefSize(147, 49);

        // Label: "Budget Speso"
        Label l1 = new Label("Budget Speso : "+viaggioAttuale.getBudget().getBudgetSpeso());
        l1.setPrefSize(131, 32);
        l1.setLayoutX(10);
        l1.setLayoutY(10);
        l1.setWrapText(true);
        l1.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Spacer Pane
        Pane s2 = new Pane();
        s2.setPrefSize(147, 29);
        s2.setLayoutX(10);
        s2.setLayoutY(42);

        // Label: "Budget Pattuito"
        Label l3 = new Label("Budget Pattuito : "+viaggioAttuale.getBudget().getBudgetIniziale());
        l3.setPrefSize(131, 32);
        l3.setLayoutX(10);
        l3.setLayoutY(10);
        l3.setWrapText(true);
        l3.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Label informativa
        Label l4 = new Label("Vuoi incrementare il Budget pattuito?");
        l4.setPrefSize(131, 32);
        l4.setLayoutX(10);
        l4.setLayoutY(10);
        l4.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        l4.setWrapText(true);

        //Spinner importo
        Spinner<Integer> importo = new Spinner<>();
        importo.setPromptText("0.0");
        importo.setPrefSize(131,32);

        // HBox con due pulsanti "+" e "-"
        Button plusButton = new Button("+");
        plusButton.setPrefSize(43, 33);
        plusButton.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        plusButton.setTextFill(Color.WHITE);
        plusButton.setOnAction(e -> {
            if(importo.getValue()<=0.0)
                animazioneBottone(plusButton,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else{
                viaggioAttuale.getBudget().aggiungiNuovoBudget(importo.getValue());
                if (!salvaViaggioCorrente())
                    animazioneBottone(plusButton,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            }

        });

        Button minusButton = new Button("-");
        minusButton.setPrefSize(43, 33);
        minusButton.setLayoutX(62);
        minusButton.setLayoutY(10);
        minusButton.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 6; -fx-padding: 8 16;");
        minusButton.setTextFill(Color.WHITE);
        minusButton.setOnAction(e -> {
            if (importo.getValue()<=0.0)
                animazioneBottone(minusButton,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #f9fafb; -fx-background-radius: 6; -fx-padding: 8 16;");
            else{
                viaggioAttuale.getBudget().rimuoviBudget(importo.getValue());
                if (!salvaViaggioCorrente())
                    animazioneBottone(minusButton,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #f9fafb; -fx-background-radius: 6; -fx-padding: 8 16;");
            }
        });

        VBox dxVBox = new VBox();

        // HBox con due pulsanti "+" e "-"
        Button aggiungiNuovaSpesa = new Button("Aggiungi nuova spesa");
        aggiungiNuovaSpesa.setPrefSize(131, 33);
        aggiungiNuovaSpesa.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        aggiungiNuovaSpesa.setTextFill(Color.WHITE);
        aggiungiNuovaSpesa.setOnAction(e -> {
            Pane base=workTrip;
            // Crea il contenitore principale trasparente
            AnchorPane overlay = new AnchorPane();
            overlay.setPrefSize(base.getWidth(), base.getHeight());
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

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                base.getChildren().remove(overlay);
                base.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            //Elementi
            Label l11 = new Label("Inserisci la motivazione della spesa");
            l11.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextField motivo = new TextField();
            motivo.setPromptText("Nome viaggio");
            VBox b1 = new VBox(5, l11, motivo);
            b1.setAlignment(Pos.TOP_LEFT);
            Label l21 = new Label("Inserisci la spesa");
            l21.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            Spinner<Double> spesa = new Spinner<>();
            spesa.setPromptText("100.0");
            VBox b2 = new VBox(5, l21, spesa);
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
                    viaggioAttuale.getBudget().aggiungiSpesa(spesa.getValue());
                    if (salvaViaggioCorrente()) {
                        base.setDisable(false);
                        base.getChildren().remove(overlay);
                        dxVBox.getChildren().add(creaAcquistoMotivoBox(dxVBox,new Pair<String,Double>(motivo.getText(),spesa.getValue())));
                    }else
                        animazioneBottone(inserisciBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                } catch (Exception ex) {
                    animazioneBottone(inserisciBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                }
            });

            HBox b4 = new HBox(inserisciBottone);
            b4.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            // Composizione finale
            popupBox.getChildren().addAll(b0, b1, b2, b3, b4);
            popupBox.setLayoutX((base.getWidth() - popupBox.getPrefWidth()) / 2);
            popupBox.setLayoutY(100);
            overlay.getChildren().add(popupBox);
            base.getChildren().add(overlay);
            base.setDisable(true);

        });


        HBox buttonBox = new HBox(plusButton, minusButton);
        buttonBox.setPrefSize(147, 31);
        buttonBox.setAlignment(Pos.CENTER);

        // VBox sinistra con tutte le label e bottoni
        VBox sxVBox = new VBox();
        sxVBox.setPrefSize(100, 200);
        sxVBox.getChildren().addAll(l0, s1, l1, s2, l3, s2, l4,s2, importo, buttonBox,s1,aggiungiNuovaSpesa);

        // VBox destra
        dxVBox.setPrefSize(545, 200);
        dxVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        popolaListaAcquisti(dxVBox);

        // SplitPane
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefSize(200, 160);
        splitPane.setDividerPositions(0.29797979797979796);
        splitPane.getItems().addAll(sxVBox, dxVBox);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        return splitPane;
    }
    @FXML
    private void popolaListaAcquisti(VBox vBox){
        vBox.getChildren().clear();
        for (Pair<String,Double> coppiaMotivoSpesa : viaggioAttuale.getBudget().getTieniConto()) vBox.getChildren().add(creaAcquistoMotivoBox(vBox,coppiaMotivoSpesa));
    }
    private HBox creaAcquistoMotivoBox(VBox listaGenerale,Pair<String,Double> coppiaMotivoSpesa) {
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

        Label titoloLabel = new Label(coppiaMotivoSpesa.getKey());
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label removeLabel = new Label("Rimuovi Spesa");
        removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
        removeLabel.setOnMouseClicked(e->{ //Rimozione e ricarica
            try {
                //Rimozione della cifra e della rendicontazione
               viaggioAttuale.getBudget().rimuoviSpesa(coppiaMotivoSpesa.getValue());
               viaggioAttuale.getBudget().rimuoviRendicontazione(coppiaMotivoSpesa.getKey(),coppiaMotivoSpesa.getValue());
               if (salvaViaggioCorrente())
                   listaGenerale.getChildren().remove(hBox);
              } catch (Exception ex) {
                animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            }
        });

        vBox.getChildren().addAll(titoloLabel, removeLabel);
        hBox.getChildren().add(vBox);

        return hBox;
    }


    @FXML
    public void sezItinerario(){
        workTrip.getChildren().clear();
        workTrip.getChildren().add(creaGraficaItinerario());
    }
    @FXML
    private SplitPane creaGraficaItinerario() {
        Label l0 = new Label("Itinerario");
        l0.setPrefSize(131, 32);
        l0.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Spacer Pane
        Pane s1 = new Pane();
        s1.setPrefSize(147, 49);

        Label l1 = new Label("Tappe attuali : "+viaggioAttuale.getItinerario().getTappe().size());
        l1.setPrefSize(131, 32);
        l1.setLayoutX(10);
        l1.setLayoutY(10);
        l1.setWrapText(true);
        l1.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Spacer Pane
        Pane s2 = new Pane();
        s2.setPrefSize(147, 29);
        s2.setLayoutX(10);
        s2.setLayoutY(42);

        VBox dxVBox = new VBox();

        Button aggiungiNuovoElemento = new Button("Aggiungi Tappa");
        aggiungiNuovoElemento.setPrefSize(131, 33);
        aggiungiNuovoElemento.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        aggiungiNuovoElemento.setTextFill(Color.WHITE);
        aggiungiNuovoElemento.setOnAction(e -> {
            Pane base=workTrip;
            // Crea il contenitore principale trasparente
            AnchorPane overlay = new AnchorPane();
            overlay.setPrefSize(base.getWidth(), base.getHeight());
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

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                base.getChildren().remove(overlay);
                base.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            //Elementi
            Label l11 = new Label("Inserisci destinazione o nome della Tappa:");
            l11.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextField nome = new TextField();
            nome.setPromptText("Perugia");
            VBox b1 = new VBox(5, l11, nome);
            b1.setAlignment(Pos.TOP_LEFT);
            Label l21 = new Label("Inserisci la data d'arrivo o partenza");
            l21.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            DatePicker datePicker=new DatePicker();
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
                        base.getChildren().remove(overlay);
                        base.setDisable(false);
                    }else
                        animazioneBottone(inserisciBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                } catch (Exception ex) {
                    animazioneBottone(inserisciBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                }
            });

            HBox b5 = new HBox(inserisciBottone);
            b5.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            // Composizione finale
            popupBox.getChildren().addAll(b0, b1, b2, b5);
            popupBox.setLayoutX((base.getWidth() - popupBox.getPrefWidth()) / 2);
            popupBox.setLayoutY(100);
            overlay.getChildren().add(popupBox);
            base.getChildren().add(overlay);
            base.setDisable(true);

        });

        // VBox sinistra con tutte le label e bottoni
        VBox sxVBox = new VBox();
        sxVBox.setPrefSize(100, 200);
        sxVBox.getChildren().addAll(l0, s1, l1, s1, aggiungiNuovoElemento);

        // VBox destra
        dxVBox.setPrefSize(545, 200);
        dxVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        creaGraficaDxTappe(dxVBox);

        // SplitPane
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefSize(200, 160);
        splitPane.setDividerPositions(0.29797979797979796);
        splitPane.getItems().addAll(sxVBox, dxVBox);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        return splitPane;
    }
    @FXML
    private void creaGraficaDxTappe(VBox vBox){
        vBox.getChildren().clear();
        //Creazione mappa
        Label l0 = new Label("Mappa dell'itinerario");
        l0.setPrefSize(131, 32);
        l0.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        MapView mappa=creaMappa();

        //Creazione Calendario
        Label l1 = new Label("Date selezionate");
        l1.setPrefSize(131, 32);
        l1.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        viaggioAttuale.getItinerario().ordinaTappe(); //Ordiniamo sempre non si sa mai
        CalendarioMensile calendario=new CalendarioMensile(YearMonth.from(viaggioAttuale.getItinerario().getDateTappe().getFirst()));

        //Creazione lista gestionale tappe
        Label l2 = new Label("Tappe scelte");
        l2.setPrefSize(131, 32);
        l2.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Spacer Pane
        Pane s2 = new Pane();
        s2.setPrefSize(147, 29);
        s2.setLayoutX(10);
        s2.setLayoutY(42);

        //Aggiunta
        vBox.getChildren().addAll(l0,mappa,s2,l1,calendario,s2,l2);

        viaggioAttuale.getItinerario().ordinaTappe(); //Le ordiniamo cronologicamente prima dell'inserimento
        for (Tappa tappa : viaggioAttuale.getItinerario().getTappe()) vBox.getChildren().add(creaAggiuntaTappaBox(vBox,tappa));
    }
    private HBox creaAggiuntaTappaBox(VBox listaGenerale,Tappa tappa) {
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

        Label titoloLabel = new Label(tappa.getNome());
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-strikethrough: "+tappa.getData().isBefore(LocalDate.now())+";");
        titoloLabel.setOnMouseClicked(e ->{
            Pane base=workTrip;
            // Crea il contenitore principale trasparente
            AnchorPane overlay = new AnchorPane();
            overlay.setPrefSize(base.getWidth(), base.getHeight());
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

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                base.getChildren().remove(overlay);
                base.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            //Elementi
            Label l11 = new Label("Luogo: "+tappa.getNome());
            l11.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            VBox b1 = new VBox(5, l11);
            b1.setAlignment(Pos.TOP_LEFT);
            Label l21 = new Label("Giorno: "+tappa.getData());
            l21.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            VBox b2 = new VBox(5, l21);
            b2.setAlignment(Pos.TOP_LEFT);
            Label l111 = new Label("Longitudine: "+tappa.getLongitudine());
            l111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            VBox b3 = new VBox(5, l111);
            b3.setAlignment(Pos.TOP_LEFT);
            Label l1111 = new Label("Latitudine: "+tappa.getLatitudine());
            l1111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            Button chiudiBottone = new Button("Chiudi");
            chiudiBottone.setStyle(
                    "-fx-background-color: #3B82F6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 8 16;" +
                            "-fx-text-fill: white;"
            );

            chiudiBottone.setOnAction(ezz -> {
                base.getChildren().remove(overlay);
                base.setDisable(false);
            });

            HBox b5 = new HBox(chiudiBottone);
            b5.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            // Composizione finale
            popupBox.getChildren().addAll(b0, b1, b2, b3, b4, b5);
            popupBox.setLayoutX((base.getWidth() - popupBox.getPrefWidth()) / 2);
            popupBox.setLayoutY(100);
            overlay.getChildren().add(popupBox);
            base.getChildren().add(overlay);
            base.setDisable(true);
        });
        Label removeLabel = new Label("Elimina");
        removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
        removeLabel.setOnMouseClicked(e->{ //Rimozione e ricarica
            try {
                viaggioAttuale.getItinerario().rimuoviTappa(tappa);
                if (salvaViaggioCorrente())
                    listaGenerale.getChildren().remove(hBox);
            } catch (Exception ex) {
                animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            }
        });

        vBox.getChildren().addAll(titoloLabel, removeLabel);
        hBox.getChildren().add(vBox);

        return hBox;
    }
    private MapView creaMappa() {
        //ordiniamo cronologicamente per sicurezza
        viaggioAttuale.getItinerario().ordinaTappe();
        List<Tappa> tappe=viaggioAttuale.getItinerario().getTappe();

        //Creiamo la mappa centrata sulla destinazione/Partenza
        MapView mapView = new MapView();
        mapView.setZoom(6);
        mapView.setCenter(new MapPoint(tappe.getFirst().getLatitudine(), tappe.getFirst().getLongitudine()));
        if (tappe.size()>=2) {
            // Aggiunge il layer con marker e percorso, vedi questo github : https://github.com/gluonhq/maps/tree/main
            mapView.addLayer(new PercorsoMappa(tappe));
        }
        return mapView;
    }

    @FXML
    public void sezElementi(){
        workTrip.getChildren().clear();
        workTrip.getChildren().add(creaGraficaElementi());
    }
    @FXML
    private SplitPane creaGraficaElementi() {
        Label l0 = new Label("Valigie");
        l0.setPrefSize(131, 32);
        l0.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Spacer Pane
        Pane s1 = new Pane();
        s1.setPrefSize(147, 49);

        Label l1 = new Label("Elementi aggiunti : "+viaggioAttuale.getListaElementi().getElementiTot());
        l1.setPrefSize(131, 32);
        l1.setLayoutX(10);
        l1.setLayoutY(10);
        l1.setWrapText(true);
        l1.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Spacer Pane
        Pane s2 = new Pane();
        s2.setPrefSize(147, 29);
        s2.setLayoutX(10);
        s2.setLayoutY(42);

        Label l3 = new Label("Elementi acquisti : "+viaggioAttuale.getListaElementi().getElementiAcquistati());
        l3.setPrefSize(131, 32);
        l3.setLayoutX(10);
        l3.setLayoutY(10);
        l3.setWrapText(true);
        l3.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        VBox dxVBox = new VBox();

        Button aggiungiNuovoElemento = new Button("Aggiungi nuovo elemento");
        aggiungiNuovoElemento.setPrefSize(131, 33);
        aggiungiNuovoElemento.setStyle("-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
        aggiungiNuovoElemento.setTextFill(Color.WHITE);
        aggiungiNuovoElemento.setOnAction(e -> {
            Pane base=workTrip;
            // Crea il contenitore principale trasparente
            AnchorPane overlay = new AnchorPane();
            overlay.setPrefSize(base.getWidth(), base.getHeight());
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

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                base.getChildren().remove(overlay);
                base.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            //Elementi
            Label l11 = new Label("Inserisci l'elemento");
            l11.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextField nome = new TextField();
            nome.setPromptText("Pomata");
            VBox b1 = new VBox(5, l11, nome);
            b1.setAlignment(Pos.TOP_LEFT);
            Label l21 = new Label("Inserisci la quantità");
            l21.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            Spinner<Integer> quantita = new Spinner<>();
            quantita.setPromptText("100.0");
            VBox b2 = new VBox(5, l21, quantita);
            b2.setAlignment(Pos.TOP_LEFT);
            Label l111 = new Label("Inserisci il luogo d'acquisto");
            l111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextField luogo = new TextField();
            luogo.setPromptText("Farmacia");
            VBox b3 = new VBox(5, l111, luogo);
            b3.setAlignment(Pos.TOP_LEFT);
            Label l1111 = new Label("Inserisci una breve descrizione");
            l1111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            TextArea descrizione = new TextArea();
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
                        base.getChildren().remove(overlay);
                        base.setDisable(false);
                    }else
                        animazioneBottone(inserisciBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                } catch (Exception ex) {
                    animazioneBottone(inserisciBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                }
            });

            HBox b5 = new HBox(inserisciBottone);
            b5.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            // Composizione finale
            popupBox.getChildren().addAll(b0, b1, b2, b3, b4, b5);
            popupBox.setLayoutX((base.getWidth() - popupBox.getPrefWidth()) / 2);
            popupBox.setLayoutY(100);
            overlay.getChildren().add(popupBox);
            base.getChildren().add(overlay);
            base.setDisable(true);

        });

        // VBox sinistra con tutte le label e bottoni
        VBox sxVBox = new VBox();
        sxVBox.setPrefSize(100, 200);
        sxVBox.getChildren().addAll(l0, s1, l1, s2, l3, s1, aggiungiNuovoElemento);

        // VBox destra
        dxVBox.setPrefSize(545, 200);
        dxVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        popolaListaElementi(dxVBox);

        // SplitPane
        SplitPane splitPane = new SplitPane();
        splitPane.setPrefSize(200, 160);
        splitPane.setDividerPositions(0.29797979797979796);
        splitPane.getItems().addAll(sxVBox, dxVBox);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        return splitPane;
    }
    @FXML
    private void popolaListaElementi(VBox vBox){
        vBox.getChildren().clear();
        for (Elemento elemento : viaggioAttuale.getListaElementi().getList()) vBox.getChildren().add(creaAggiuntaElementoBox(vBox,elemento));
    }
    private HBox creaAggiuntaElementoBox(VBox listaGenerale,Elemento elemento) {
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

        Label titoloLabel = new Label(elemento.getNome());
        titoloLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-strikethrough: "+elemento.isAcquistato()+";");
        titoloLabel.setOnMouseClicked(e ->{
            Pane base=workTrip;
            // Crea il contenitore principale trasparente
            AnchorPane overlay = new AnchorPane();
            overlay.setPrefSize(base.getWidth(), base.getHeight());
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

            // Bottone "X" per chiudere
            Button closeButton = new Button("X");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
            closeButton.setOnAction(ez -> {
                base.getChildren().remove(overlay);
                base.setDisable(false);
            });

            HBox b0 = new HBox(closeButton);
            b0.setAlignment(Pos.TOP_RIGHT);
            //Elementi
            Label l11 = new Label("Nome: "+elemento.getNome());
            l11.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            VBox b1 = new VBox(5, l11);
            b1.setAlignment(Pos.TOP_LEFT);
            Label l21 = new Label("Quantità: "+elemento.getQuantita());
            l21.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            VBox b2 = new VBox(5, l21);
            b2.setAlignment(Pos.TOP_LEFT);
            Label l111 = new Label("Descrizione: "+elemento.getDescrizione());
            l111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            VBox b3 = new VBox(5, l111);
            b3.setAlignment(Pos.TOP_LEFT);
            Label l1111 = new Label("Luogo D'Acquisto: "+elemento.getLuogoAcquisto());
            l1111.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            CheckBox ck=new CheckBox("Acquistato");
            Button chiudiBottone = new Button("Chiudi");
            ck.setOnAction(ez->{
                elemento.setAcquistato(ck.isSelected());
                if (salvaViaggioCorrente()){
                    base.getChildren().remove(overlay);
                    base.setDisable(false);
                } else
                    animazioneBottone(chiudiBottone,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            });
            VBox b4 = new VBox(5, l1111,ck);
            b4.setAlignment(Pos.TOP_LEFT);

            // Pulsante Inserisci
            chiudiBottone.setStyle(
                    "-fx-background-color: #3B82F6;" +
                            "-fx-background-radius: 6;" +
                            "-fx-padding: 8 16;" +
                            "-fx-text-fill: white;"
            );

            chiudiBottone.setOnAction(ezz -> {
                base.getChildren().remove(overlay);
                base.setDisable(false);
            });

            HBox b5 = new HBox(chiudiBottone);
            b5.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            // Composizione finale
            popupBox.getChildren().addAll(b0, b1, b2, b3, b4, b5);
            popupBox.setLayoutX((base.getWidth() - popupBox.getPrefWidth()) / 2);
            popupBox.setLayoutY(100);
            overlay.getChildren().add(popupBox);
            base.getChildren().add(overlay);
            base.setDisable(true);
        });
        Label removeLabel = new Label("Rimuovi");
        removeLabel.setStyle("-fx-text-fill: #4B5563; -fx-font-size: 12px;");
        removeLabel.setOnMouseClicked(e->{ //Rimozione e ricarica
            try {
                viaggioAttuale.getListaElementi().removeElemento(elemento);
                if (salvaViaggioCorrente())
                    listaGenerale.getChildren().remove(hBox);
            } catch (Exception ex) {
                animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            }
        });

        vBox.getChildren().addAll(titoloLabel, removeLabel);
        hBox.getChildren().add(vBox);

        return hBox;
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