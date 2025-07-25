package praticaest1.praticaest1;

import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.nio.charset.StandardCharsets;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;


public class GroqClient {
    /**
     * Premessa per te che guardi, ovviamente questa soluzione è terribile in privacy e tutto perchè se non paghi lo sai, che, sei tu il prodotto,
     * ma dati gli obbiettivi didattici, l'improntamento dell'uso massivo delle richieste http e soprattutto dopo
     * svariati tentativi nell'uso di DJL o modelli con Translator ect. custom bastai su modelli ONNX o PyTorch tutti falliti, data la mancanza di modelli compatibili
     * vista anche la chiusura in luglio del progetto ModelZOO di ONNX e dati i diversi problemi che DJL ancora riporta, non mi rimaneva altro che ciò. Inoltre costringere l'utente a scaricare
     * ollama ed un modello decente di almeno 5/6 o più giga sarebbe terribile.
     * */
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL   = "meta-llama/llama-4-scout-17b-16e-instruct";

    private final HttpClient http;
    private final String apiKey;

    public GroqClient(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key mancante. Imposta la variabile d'ambiente GROQ_API_KEY ");
        }
        this.apiKey = apiKey;
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    //METODI RICHIESTA E RISPOSTA TRAMITE GROQ CLOUD
    /** Struttura fornita DA GROQ CLOUD/OPENAI
     * {
     *   "id": "chatcmpl-123",
     *   "object": "chat.completion",
     *   "choices": [
     *     {
     *       "index": 0,
     *       "message": {
     *         "role": "assistant",
     *         "content": "La risposta generata dal modello..."
     *       },
     *       "finish_reason": "stop"
     *     }
     *   ]
     * }
     * */

    public JSONArray generaTreElementi(String jsonInput) throws Exception {
        // Prompt “standard” costruito dinamicamente
        String promptIstruzioni = """
            Agisci come un assistente che riceve in input un JSON di viaggi con questa struttura (array di oggetti).
            Genera ESATTAMENTE 3 nuovi elementi da aggiungere a `listaElementi.list`, coerenti con:
            - il viaggio descritto,
            - il mezzoUsato,
            - l’obiettivo,
            - i luoghi e le tappe (nomi, date, coordinate).
            
            Vincoli:
            1. Non proporre elementi già presenti (controlla i duplicati confrontando il campo `nome` in modo case-insensitive).
            2. Ogni elemento deve avere solo questi campi:
               - "nome"
               - "descrizione"
               - "luogoAcquisto"
               - "isAcquistato" (sempre false)
            3. Nessun altro campo.
            4. Il risultato deve essere SOLO un array JSON valido con 3 oggetti, senza testo aggiuntivo.
            
            Tuo input:
            """;

        String fullPrompt = promptIstruzioni + jsonInput + "\n\nRestituisci SOLO l'array JSON.";

        // Corpo JSON della richiesta
        JSONObject corpoJson = new JSONObject()
                .put("model", MODEL)
                .put("messages", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("content", fullPrompt)
                        )
                );

        HttpRequest richiestaHTTP = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpoJson.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = http.send(richiestaHTTP, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2) {
            throw new RuntimeException("Chiamata fallita: HTTP " + response.statusCode() + " - " + response.body());
        }

        JSONObject json = new JSONObject(response.body());
        JSONArray choices = json.optJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("Risposta senza choices: " + response.body());
        }

        // Percorso tipico: choices[0].message.content
        JSONObject first = choices.getJSONObject(0);
        JSONObject message = first.getJSONObject("message");
        String content = message.getString("content").trim();

        // A volte i modelli aggiungono testo extra: isolo la parte tra [ ... ]
        int start = content.indexOf('[');
        int end = content.lastIndexOf(']');
        if (start == -1 || end == -1 || end < start) {
            throw new RuntimeException("Non ho trovato un array JSON valido nella risposta: " + content);
        }

        String jsonArrayText = content.substring(start, end + 1);
        JSONArray result = new JSONArray(jsonArrayText);

        //Validazione minima
        if (result.length() != 3) {
            throw new RuntimeException("L'array restituito non contiene 3 elementi: " + result.length());
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            GroqClient client = new GroqClient(Dotenv.load().get("GROQ_API_KEY"));
            /**
            String prompt;
            if (args.length > 0) {
                prompt = String.join(" ", args);
            } else {
                System.out.print("Prompt> ");
                prompt = new java.util.Scanner(System.in).useDelimiter("\\A").nextLine();
            }

            String risposta = client.richiesta(prompt);
            System.out.println("Risposta:\n" + risposta);
            */

            String inputViaggio = "[{\"nomeUnivoco\":\"test\",\"mezzoUsato\":\"Piedi\",\"obiettivo\":\"Rilassarsi\",\"budget\":{\"budgetIniziale\":1000,\"budgetSpeso\":0,\"tieniConto\":[]},\"itinerario\":{\"nome\":\"Roma\",\"tappe\":[{\"nome\":\"Roma\",\"latitudine\":41.8933203,\"longitudine\":12.4829321,\"data\":[2025,7,31]},{\"nome\":\"Curno\",\"latitudine\":45.6915142,\"longitudine\":9.6109659,\"data\":[2025,7,25]}]},\"listaElementi\":{\"list\":[{\"id\":1,\"nome\":\"Pomata\",\"descrizione\":\"dolori linguine post camminata\",\"luogoAcquisto\":\"farmacia\",\"isAcquistato\":false,\"lista_elementi_id\":1}],\"elementiTot\":1,\"elementiAcquistati\":0}}]";
            JSONArray elementi = client.generaTreElementi(inputViaggio);
            System.out.println(elementi.toString(2));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Errore: " + e.getMessage());
            System.exit(1);
        }
    }
}
