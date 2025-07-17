package praticaest1.praticaest1.utility;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import java.time.*;

public class CalendarioMensile extends VBox {
    private YearMonth meseCorrente;
    private final GridPane grigliaCalendario = new GridPane();
    private final Label nomeMese = new Label();

    public CalendarioMensile(YearMonth meseIniziale) {
        this.meseCorrente = meseIniziale;

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
            giorno.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-background-radius: 5;");

            grigliaCalendario.add(giorno, col, riga);

            col++;
            if (col == 7) {
                col = 0;
                riga++;
            }
            giornoCorrente++;
        }
    }
}
