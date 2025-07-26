package praticaest1.praticaest1.utility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalizzatoreGeo {
    public static String riceviLatLong(String luogoRicercato) throws IOException, InterruptedException {
        Dotenv dotenv= Dotenv.load();
        GestoreHTTP gestoreHTTP=new GestoreHTTP();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        //Uso il mapper per comodità visto che lo so già usare ma se volete potete cambiarlo con ciò che preferite,
        //inoltre ricordatevi di aggiungere le dipendenze jackson-databind a maven
        String responseString=gestoreHTTP.inviaRichiesta("https://geocode.maps.co/search?q="+luogoRicercato+"&api_key="+dotenv.get("API_GEO_KEY"));
        List<RisultatoGeo> risultati = mapper.readValue(responseString, new TypeReference<List<RisultatoGeo>>() {});
        if (risultati.isEmpty()) return "&";
        Thread.sleep(1000); //Pausa di 1 sec per l'api
        return risultati.getFirst().getLat()+"&"+risultati.getFirst().getLon();
    }
}
@JsonIgnoreProperties(ignoreUnknown = true)
class RisultatoGeo{
    private String lat;
    private String lon;

    public RisultatoGeo() {}
    public RisultatoGeo(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    // Getters e setters
    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }
    public String getLon() { return lon; }
    public void setLon(String lon) { this.lon = lon; }
}
