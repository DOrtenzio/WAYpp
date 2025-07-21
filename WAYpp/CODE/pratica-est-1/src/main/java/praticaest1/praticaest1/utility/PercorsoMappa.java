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

public class PercorsoMappa extends MapLayer {
    private List<Tappa> tappe;
    private final List<Node> markerNodi = new ArrayList<>();
    private final Polyline polyline = new Polyline();

    public PercorsoMappa(List<Tappa> tappe) {
        this.tappe = tappe;
        polyline.setStroke(Color.BLUE);
        polyline.setStrokeWidth(3);
        getChildren().add(polyline);
        // Chiama setTappe per inizializzare i marker e la polilinea con le tappe fornite
        setTappe(tappe);
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

    @Override
    protected void layoutLayer() {
        polyline.getPoints().clear();

        if (tappe != null && !tappe.isEmpty()) {
            for (int i = 0; i < tappe.size(); i++) {
                Tappa t = tappe.get(i);
                Point2D punto = getMapPoint(t.getLatitudine(), t.getLongitudine());

                if (i < markerNodi.size()) {
                    Node marker = markerNodi.get(i);
                    marker.setTranslateX(punto.getX());
                    marker.setTranslateY(punto.getY());
                }
                polyline.getPoints().addAll(punto.getX(), punto.getY());
            }
        } else {
            getChildren().removeAll(markerNodi);
            markerNodi.clear();
            polyline.getPoints().clear();
        }
    }
}