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

public class PercorsoMappa  extends MapLayer {
    private final List<Tappa> tappe;
    private final List<Node> markerNodi = new ArrayList<>();//tappe
    private final Polyline polyline = new Polyline(); //Tracciato mappa

    public PercorsoMappa(List<Tappa> tappe) {
        this.tappe = tappe;

        for (int i = 0; i < tappe.size(); i++) {
            Tappa t = tappe.get(i);

            Color colore;
            if (i == 0) colore = Color.GREEN; //Partenza
            else if (i == tappe.size() - 1) colore = Color.BLUE; //destinazione
            else colore = Color.RED; //Intermedio

            Circle marker = new Circle(6, colore);

            //  piccola finestra di testo a scomparsa con nome tappa
            Tooltip tooltip = new Tooltip(t.getNome());
            Tooltip.install(marker, tooltip);

            markerNodi.add(marker);
            getChildren().add(marker);
        }

        // Linea del percorso
        polyline.setStroke(Color.DARKORANGE);
        polyline.setStrokeWidth(3);
        getChildren().add(polyline);
    }

    @Override
    protected void layoutLayer() {
        polyline.getPoints().clear();

        for (int i = 0; i < tappe.size(); i++) {
            Tappa t = tappe.get(i);
            Point2D punto = getMapPoint(t.getLatitudine(), t.getLongitudine());

            // Posiziona marker
            Node marker = markerNodi.get(i);
            marker.setTranslateX(punto.getX());
            marker.setTranslateY(punto.getY());

            // Aggiunge alla polyline
            polyline.getPoints().addAll(punto.getX(), punto.getY());
        }
    }
}
