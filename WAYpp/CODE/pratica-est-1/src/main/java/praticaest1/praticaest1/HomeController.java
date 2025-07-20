package praticaest1.praticaest1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gluonhq.maps.*;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
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
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class HomeController {
    @FXML
    private AnchorPane profileBox,tripBox,homeBox,intoTripBox,base;
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
    private AnchorPane workTrip;

    //Dell'utente attuale
    private Utente utenteAttuale;
    private ListaViaggi listaViaggiAttuale;
    private Viaggio viaggioAttuale;
    //Elementi gestionali
    private String URL_BASE;
    private final GestoreHTTP gestoreHTTP=new GestoreHTTP();
    private ObjectMapper mapper=new ObjectMapper();

    public void initialize(){
        //Variabili
        Dotenv dotenv= Dotenv.load();
        this.URL_BASE=dotenv.get("URL_BASE_SERVER");
        mapper.registerModule(new JavaTimeModule());
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
        b1.setOnMouseExited(event -> {
            if (homeBox.isVisible()) b1.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;");
            else b1.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;");
        });
        b2.setOnMouseMoved(event -> b2.setStyle("-fx-background-color: #E5E7EB; -fx-background-radius: 8; -fx-padding: 8;"));
        b2.setOnMouseExited(event -> {
            b2.setStyle("-fx-background-color: #f9fafb; -fx-background-radius: 8; -fx-padding: 8;"); //TODO: cambia dopo la realizzazione di esplora
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
    }
    @FXML
    public void goToHome(){ gestSchermate(true,false,false); }
    @FXML
    public void goToTrip(){
        try {
            gestSchermate(false,true,false);
            String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"getListaViaggi.php",mapper.writeValueAsString(this.utenteAttuale));
            Messaggio<List<Viaggio>> m=mapper.readValue(risposta, new TypeReference<Messaggio<List<Viaggio>>>() {}); //Lo converto nella variabile messaggio
            if (!m.getConfermaAzione())
                animazioneBottone(aggiungiNuovoViaggio,"-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;","-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
            else{
                this.listaViaggiAttuale =new ListaViaggi(m.getParametro1()); //Converto l'array di viaggi nel nostro oggetto lista viaggi
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

    /*SEZIONE VIAGGI*/
    @FXML
    private void popolaScroolPane(){
        scrollTrip.getChildren().clear();
        for(Viaggio v: listaViaggiAttuale.getList()){
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
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream("/praticaest1/praticaest1/img/plane-icon.png")));
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
            this.viaggioAttuale=viaggioCorrente;
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
                                        new Budget(Double.parseDouble(budget.getText().trim())),
                                        new Itinerario(destinazione.getText().trim(), dataArrivo.getValue()),
                                        new ListaElementi()
                                )
                        );

                        String risposta = gestoreHTTP.inviaRichiestaConParametri(
                                URL_BASE + "reloadListaViaggi.php",
                                mapper.writeValueAsString(new MessaggioDati<Utente, ListaViaggi>(utenteAttuale, listaViaggiAttuale))
                        );
                        Messaggio<ListaViaggi> m = mapper.readValue(risposta, Messaggio.class);

                        Platform.runLater(() -> { //DOPO aver caricato lo spinner posso accedere alle altre impostazioni
                            base.getChildren().remove(overlaySpinner);
                            baseScroll.setDisable(false);
                            aggiungiNuovoViaggio.setDisable(false);

                            if (m.getConfermaAzione()) {
                                base.getChildren().remove(overlaySpinner);
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
        popupBox.getChildren().addAll(b0, b1, b2, b3,b6, b4);
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
        aggiungiSpesa.setOnAction(e -> mostraPopupNuovaSpesa(aggiungiSpesa,destraBox));

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
    private void mostraPopupNuovaSpesa(Button bottoneDInnesco, VBox vBox) {
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
        sxVBox.getChildren().addAll(l0, creaSpaziatore(false), l1, creaSpaziatore(true), aggiungiTappa);

        // VBox destra
        dxVBox.setPrefSize(545, 200);
        dxVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        creaGraficaDxTappe(dxVBox);

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(sxVBox, dxVBox);
        splitPane.setDividerPositions(0.3);
        splitPane.setPrefSize(800, 600); // Imposta una dimensione iniziale decente
        splitPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Lascia che cresca

        // Dopo che lo SplitPane è stato aggiunto alla scena così evitamo problemi di caricamento
        Platform.runLater(() -> {
            for (Node divider : splitPane.lookupAll(".split-pane-divider")) {
                divider.setMouseTransparent(true); // Blocca il trascinamento
                divider.setVisible(false);
            }
        });
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

        VBox contenitoreMappa = new VBox(10, l0, mappa);
        contenitoreMappa.setPadding(new Insets(10));
        VBox.setVgrow(mappa, Priority.ALWAYS);

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

        //Aggiunta
        vBox.getChildren().addAll(contenitoreMappa,creaSpaziatore(false),l1,calendario,creaSpaziatore(false),l2);

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

            vBox.getChildren().add(creaAggiuntaTappaBox(vBox,tappa));
        }
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

        Label titoloLabel = new Label(tappa.getNome()+"\s"+tappa.getData());
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
            popupBox.setMaxWidth(400);
            popupBox.setPrefHeight(300);
            popupBox.setMaxHeight(300);

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
                else
                    viaggioAttuale.getItinerario().aggiungiTappa(tappa);
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

            vBox.getChildren().add(creaAggiuntaElementoBox(vBox,elemento));
        }
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
            ck.setSelected(elemento.isAcquistato());
            Button chiudiBottone = new Button("Chiudi");
            ck.setOnAction(ez->{ //TODO: CHANGE CON MODIFICA
                try {
                    viaggioAttuale.getListaElementi().setElementoAcquistato(elemento,ck.isSelected());
                    if (salvaViaggioCorrente()){
                        this.base.getChildren().remove(overlay);
                        baseScroll.setDisable(false);
                        titoloLabel.setDisable(false);
                        sezElementi();
                    } else {
                        animazioneBottone(chiudiBottone, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                        elemento.setAcquistato(!ck.isSelected());
                    }
                } catch (Exception ex) {
                    elemento.setAcquistato(!ck.isSelected());
                    animazioneBottone(chiudiBottone, "-fx-background-color: #BE2538; -fx-background-radius: 6; -fx-padding: 8 16;", "-fx-background-color: #3B82F6; -fx-background-radius: 6; -fx-padding: 8 16;");
                }
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
            chiudiBottone.setOnAction(ez -> {
                this.base.getChildren().remove(overlay);
                baseScroll.setDisable(false);
                titoloLabel.setDisable(false);
            });

            HBox b5 = new HBox(chiudiBottone);
            b5.setAlignment(Pos.CENTER_RIGHT);

            // Inserimento
            popupBox.getChildren().addAll(b0, b1, b2, b3, b4, b5);
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
            System.out.println(risposta);
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