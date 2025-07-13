package praticaest1.praticaest1.utility;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;

public class GestoreHTTP {
    private final HttpClient client;

    public GestoreHTTP() {
        client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    }

    // Metodo GET ---> 	https://esempio.com/api/utenti.php?minEta=18&maxEta=30
    public String inviaRichiesta(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // Metodo POST ---> URL: https://esempio.com/api/login.php Body: {"username":"mario","password":"segreta123"}
    public String inviaRichiestaConParametri(String url, String parametriJson) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(parametriJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

}