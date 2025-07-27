package praticaest1.praticaest1.utility;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import praticaest1.praticaest1.GroqClient;
import praticaest1.praticaest1.obj.Itinerario;
import praticaest1.praticaest1.obj.Tappa;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class CalendarioMensile extends VBox {
    private YearMonth meseCorrente;
    private final GridPane grigliaCalendario = new GridPane();
    private final Label nomeMese = new Label();
    //Elementi utili per l'interattività con il nostro scopo
    private final Itinerario itinerario;
    private List<Tappa> tappeMeseCorrente;
    private final AnchorPane base,baseScroll;
    private final GroqClient groq;

    public CalendarioMensile(YearMonth meseIniziale, Itinerario itinerario, AnchorPane base, AnchorPane baseScroll,GroqClient groq) {
        this.meseCorrente = meseIniziale;
        this.itinerario=itinerario;
        this.base=base;
        this.baseScroll=baseScroll;
        this.tappeMeseCorrente=new ArrayList<Tappa>();
        this.groq=groq;

        setSpacing(10);
        setPadding(new Insets(15));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-background-radius: 10;");

        nomeMese.setFont(Font.font("Arial", 18));
        nomeMese.setStyle("-fx-font-weight: bold;");
        aggiornaTitolo();

        HBox barraNavigazione = creaBarraNavigazione();
        getChildren().addAll(barraNavigazione, grigliaCalendario);
        aggiornaCalendario();
    }

    private HBox creaBarraNavigazione() {
        Button btnPrecedente = new Button("<");
        Button btnSuccessivo = new Button(">");
        styleBottoni(btnPrecedente);
        styleBottoni(btnSuccessivo);

        btnPrecedente.setOnAction(e -> {
            meseCorrente = meseCorrente.minusMonths(1);
            aggiornaTitolo();
            aggiornaCalendario();
        });

        btnSuccessivo.setOnAction(e -> {
            meseCorrente = meseCorrente.plusMonths(1);
            aggiornaTitolo();
            aggiornaCalendario();
        });

        HBox box = new HBox(10, btnPrecedente, nomeMese, btnSuccessivo);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void styleBottoni(Button btn) {
        btn.setFont(Font.font("Arial", 14));
        btn.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-background-radius: 5; -fx-border-radius: 5;");
    }

    private void aggiornaTitolo() {
        nomeMese.setText(meseCorrente.getMonth().toString().substring(0, 1).toUpperCase()
                + meseCorrente.getMonth().toString().substring(1).toLowerCase()
                + " " + meseCorrente.getYear()); //Risultato simile: Febbraio 1025
        //Aggiorno lista
        this.tappeMeseCorrente.clear();
        for(Tappa tappa: itinerario.getTappe()){
            if (tappa.getData().getMonth().equals(meseCorrente.getMonth()) && tappa.getData().getYear()== meseCorrente.getYear())
                this.tappeMeseCorrente.add(tappa);
        }
    }

    private void aggiornaCalendario() {
        grigliaCalendario.getChildren().clear();
        grigliaCalendario.setHgap(5);
        grigliaCalendario.setVgap(5);
        grigliaCalendario.setAlignment(Pos.CENTER);

        String[] giorni = {"Lun", "Mar", "Mer", "Gio", "Ven", "Sab", "Dom"};

        for (int i = 0; i < giorni.length; i++) {
            Label label = new Label(giorni[i]);
            label.setFont(Font.font("Arial", 13));
            label.setStyle("-fx-font-weight: bold;");
            label.setPrefWidth(35);
            label.setAlignment(Pos.CENTER);
            grigliaCalendario.add(label, i, 0);
        }

        LocalDate primoGiorno = meseCorrente.atDay(1);
        int startCol = primoGiorno.getDayOfWeek().getValue() % 7; // Lunedì = 1, Domenica = 7 → %7 per domenica=0
        int giornoCorrente = 1;
        int giorniNelMese = meseCorrente.lengthOfMonth();

        int riga = 1;
        int col = startCol;

        while (giornoCorrente <= giorniNelMese) {
            Label giorno = new Label(String.valueOf(giornoCorrente));
            giorno.setPrefSize(35, 35);
            giorno.setAlignment(Pos.CENTER);
            giorno.setFont(Font.font(12));
            if (tappeMeseCorrente.isEmpty())
                giorno.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-background-radius: 5;");
            else{
                boolean isChange=false;
                for(Tappa tappa: tappeMeseCorrente){
                    if (tappa.getData().getDayOfMonth()==giornoCorrente){
                        giorno.setStyle("-fx-background-color: #f29f22; -fx-border-color: #f29f22; -fx-background-radius: 5; -fx-text-fill:white;");
                        giorno.setOnMouseClicked(e -> {
                            ProgressIndicator spinner = new ProgressIndicator();
                            spinner.setMaxSize(40, 40);
                            spinner.setStyle("-fx-progress-color: #3B82F6;");
                            VBox overlaySpinner = new VBox(spinner);
                            overlaySpinner.setAlignment(Pos.CENTER);
                            overlaySpinner.setStyle("-fx-background-color: rgba(255,255,255,0.6);");
                            overlaySpinner.setPrefSize(base.getWidth(), base.getHeight());
                            base.getChildren().add(overlaySpinner);

                            Task<Void> task = new Task<>() {
                                @Override
                                protected Void call() throws Exception {
                                    try {
                                        Platform.runLater(() -> {
                                            try {
                                                base.getChildren().remove(overlaySpinner);
                                                creaPopBox(giorno,tappa);
                                            } catch (Exception ex) {
                                                Platform.runLater(() -> {
                                                    base.getChildren().remove(overlaySpinner);
                                                });
                                            }
                                        });
                                    } catch (Exception ex) {
                                        Platform.runLater(() -> {
                                            base.getChildren().remove(overlaySpinner);
                                        });
                                    }
                                    return null;
                                }
                            };

                            new Thread(task).start();
                        });
                        isChange=true;
                        break;
                    }
                }
                if(!isChange)
                    giorno.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-background-radius: 5;");
            }

            grigliaCalendario.add(giorno, col, riga);

            col++;
            if (col == 7) {
                col = 0;
                riga++;
            }
            giornoCorrente++;
        }
    }
    private void creaPopBox(Label giorno, Tappa tappa) throws Exception{
        StackPane overlay = new StackPane();
        overlay.setPrefSize(this.base.getWidth(), this.base.getHeight());
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

        VBox popupBox = new VBox(10);
        popupBox.setPadding(new Insets(20));
        popupBox.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        popupBox.setPrefWidth(400);
        popupBox.setMaxWidth(400);
        popupBox.setPrefHeight(300);
        popupBox.setMaxHeight(500);
        popupBox.setAlignment(Pos.TOP_CENTER);

        Button chiudi = new Button("X");
        chiudi.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-text-fill: #6B7280;");
        chiudi.setOnAction(ez -> {
            this.base.getChildren().remove(overlay);
            baseScroll.setDisable(false);
            giorno.setDisable(false);
        });
        Label l1=new Label("Dettagli viaggio: ");
        l1.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label l2 = new Label("Destinazione: ");
        l2.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label l3=new Label(tappa.getNome());
        l3.setStyle("-fx-font-size: 12px;");
        Label l4 = new Label("Data di arrivo: ");
        l4.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label l5=new Label(tappa.getData().toString());
        l5.setStyle("-fx-font-size: 12px;");
        //Consiglio Ia
        Label l6=new Label("SALVINO Consiglia:");
        l6.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label l7=new Label(groq.generaRisposta("Destinazione: "+tappa.getNome()+" Date: "+tappa.getData().toString()));
        l7.setStyle("-fx-font-size: 12px;");
        l7.setWrapText(true);
        VBox.setVgrow(l7, Priority.ALWAYS);

        Button chiudi2 = new Button("Chiudi");
        chiudi2.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white;");
        chiudi2.setOnAction(ez -> {
            this.base.getChildren().remove(overlay);
            baseScroll.setDisable(false);
            giorno.setDisable(false);
        });

        HBox topBar = new HBox(chiudi);
        topBar.setAlignment(Pos.TOP_RIGHT);
        HBox bottomBar = new HBox(chiudi2);
        bottomBar.setAlignment(Pos.CENTER_RIGHT);

        popupBox.getChildren().addAll(topBar,l1,l2,l3,l4,l5,l6,l7,bottomBar);
        overlay.getChildren().add(popupBox);
        this.base.getChildren().add(overlay);
        baseScroll.setDisable(true);
        giorno.setDisable(true);
    }
}

