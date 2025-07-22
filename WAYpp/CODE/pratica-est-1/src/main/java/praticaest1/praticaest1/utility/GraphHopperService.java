package praticaest1.praticaest1.utility;

import com.gluonhq.maps.MapPoint;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;
import praticaest1.praticaest1.obj.Tappa;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GraphHopperService { //Codice preso in parte soprattutto nel decodificatore e nelle richietse da forum e doc di GraphHopper
    private String API_KEY_GHH;
    private final String BASE_URL = "https://graphhopper.com/api/1/route?";

    public GraphHopperService(){
        Dotenv dotenv= Dotenv.load();
        API_KEY_GHH= dotenv.get("API_KEY_GHH");
    }

    public List<MapPoint> getRoute(List<Tappa> tappe) throws IOException, InterruptedException {
        List<MapPoint> fullRoute = new ArrayList<>();

        if (tappe == null || tappe.size() < 2) {
            return fullRoute;
        }

        int maxTappeAssieme = 5;

        // Scorri le tappe in blocchi sovrapposti (ovvero coincide la tappa ultima prima) di max 5 tappe limite pian free
        for (int i = 0; i < tappe.size() - 1; i += (maxTappeAssieme - 1)) {
            // Calcola l'indice finale del gruppetto di tappe: massimo i + 5, ma non oltre la fine della lista
            int end = Math.min(i + maxTappeAssieme, tappe.size());

            // Estrae una sottolista delle tappe per fare la richiesta
            List<Tappa> subList = tappe.subList(i, end); // Es: se i=0 e maxTappeAssieme=5, prende tappe[0] fino tappe[4] (escluso end)

            // Serve almeno 2 punti per fare una richiesta valida a GraphHopper
            if (subList.size() >= 2) {
                fullRoute.addAll(getRouteGruppettoTappe(subList));
            }
        }


        return fullRoute;
    }

    private List<MapPoint> getRouteGruppettoTappe(List<Tappa> tappe) throws IOException, InterruptedException {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append("key=").append(API_KEY_GHH);
        urlBuilder.append("&points_encoded=true");
        urlBuilder.append("&locale=it");

        for (Tappa tappa : tappe) {
            urlBuilder.append("&point=").append(tappa.getLatitudine()).append(",").append(tappa.getLongitudine());
        }

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder.toString()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return parseGraphHopperResponse(response.body());
        } else {
            System.err.println("Errore GraphHopper API: " + response.statusCode() + " - " + response.body());
            throw new IOException("Failed to get route from GraphHopper: " + response.body());
        }
    }


    public List<MapPoint> parseGraphHopperResponse(String jsonResponse) {
        List<MapPoint> routePoints = new ArrayList<>();
        JSONObject obj = new JSONObject(jsonResponse);

        if (obj.has("paths")) {
            JSONArray paths = obj.getJSONArray("paths");
            if (paths.length() > 0) {
                JSONObject firstPath = paths.getJSONObject(0);
                String encodedPolyline = firstPath.getString("points");

                for (double[] coords : PolylineDecoder.decodePolyline(encodedPolyline)) {
                    routePoints.add(new MapPoint(coords[0], coords[1]));
                }

            }
        }
        return routePoints;
    }
}
