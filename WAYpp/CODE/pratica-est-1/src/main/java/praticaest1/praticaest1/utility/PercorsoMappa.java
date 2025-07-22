package praticaest1.praticaest1.utility;

import com.gluonhq.maps.MapLayer;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import praticaest1.praticaest1.obj.Tappa;

import java.util.ArrayList;
import java.util.List;
import com.gluonhq.maps.MapPoint; // Importa MapPoint

public class PercorsoMappa extends MapLayer {
    private List<Tappa> tappe;
    private List<MapPoint> puntiPercorsoDettagliati; // Nuova lista per i punti di GraphHopper
    private final List<Node> markerNodi = new ArrayList<>();
    private final Polyline polyline = new Polyline();

    public PercorsoMappa(List<Tappa> tappe) {
        polyline.setStroke(Color.BLUE);
        polyline.setStrokeWidth(3);
        getChildren().add(polyline);

        setTappe(tappe); // Inizializza i marker con le tappe
    }

    public void setTappe(List<Tappa> newTappe) {
        getChildren().removeAll(markerNodi);
        markerNodi.clear();

        this.tappe = newTappe;

        if (this.tappe != null && !this.tappe.isEmpty()) {
            for (int i = 0; i < this.tappe.size(); i++) {
                Tappa t = this.tappe.get(i);

                Color colore;
                if (i == 0 || i == this.tappe.size() - 1) {
                    colore = Color.YELLOW;
                } else {
                    colore = Color.GREEN;
                }

                Circle marker = new Circle(6, colore);
                Tooltip tooltip = new Tooltip(t.getNome());
                Tooltip.install(marker, tooltip);

                markerNodi.add(marker);
                getChildren().add(marker);
            }
        }
        markDirty();
    }

    // Nuovo metodo per impostare i punti del percorso dettagliato
    public void setPuntiPercorsoDettagliati(List<MapPoint> newPunti) {
        this.puntiPercorsoDettagliati = newPunti;
        markDirty(); // Segnaliamo alla mappa che deve essere ridisegnata per aggiornare la polilinea
    }

    @Override
    protected void layoutLayer() {
        polyline.getPoints().clear();

        // Se abbiamo punti percorso dettagliati, usiamo quelli per la polilinea
        if (puntiPercorsoDettagliati != null && !puntiPercorsoDettagliati.isEmpty()) {
            for (MapPoint p : puntiPercorsoDettagliati) {
                Point2D punto = getMapPoint(p.getLatitude(), p.getLongitude());
                polyline.getPoints().addAll(punto.getX(), punto.getY());
            }
        }
        // Altrimenti, se non ci sono punti dettagliati, ma ci sono tappe, disegniamo una polilinea semplificata tra le tappe
        else if (tappe != null && !tappe.isEmpty()) {
            for (Tappa t : tappe) {
                Point2D punto = getMapPoint(t.getLatitudine(), t.getLongitudine());
                polyline.getPoints().addAll(punto.getX(), punto.getY());
            }
        }
        // Aggiorna la posizione dei marker (questo rimane invariato)
        if (tappe != null && !tappe.isEmpty()) {
            for (int i = 0; i < tappe.size(); i++) {
                Tappa t = tappe.get(i);
                Point2D punto = getMapPoint(t.getLatitudine(), t.getLongitudine());
                if (i < markerNodi.size()) {
                    Node marker = markerNodi.get(i);
                    marker.setTranslateX(punto.getX());
                    marker.setTranslateY(punto.getY());
                }
            }
        } else {
            getChildren().removeAll(markerNodi);
            markerNodi.clear();
        }
    }
}