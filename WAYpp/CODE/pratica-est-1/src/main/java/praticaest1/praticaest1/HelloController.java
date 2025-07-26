package praticaest1.praticaest1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;
import praticaest1.praticaest1.obj.Utente;
import praticaest1.praticaest1.utility.GestoreHTTP;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class HelloController {
    @FXML
    private AnchorPane logBox,regBox,forgotBox,anchorBase; //Box o visioni alternative
    @FXML
    private VBox codiceBox,emailBox,passwordBox;
    @FXML
    private TextField logEmail,logPassword,regNome,regEmail,regPassword1,regPassword2,emailV,codiceV,passwordV; //Elementi di lettura standard
    @FXML
    private Button b1,b2,b3;
    //Gestori di richieste http
    private String URL_BASE;
    private final GestoreHTTP gestoreHTTP=new GestoreHTTP();
    private ObjectMapper mapper=new ObjectMapper();
    private Integer codice;
    private UserLogin infoUtenteResetPasw;

    public void initialize(){
        Dotenv dotenv= Dotenv.load();
        this.URL_BASE=dotenv.get("URL_BASE_SERVER");
        mapper.registerModule(new JavaTimeModule());
    }

    //Scambio schermate
    @FXML
    public void goToLogin(){
        regBox.setDisable(true);
        regBox.setVisible(false);
        forgotBox.setDisable(true);
        forgotBox.setVisible(false);
        logBox.setDisable(false);
        logBox.setVisible(true);
    }
    @FXML
    public void goToRegister(){
        logBox.setDisable(true);
        logBox.setVisible(false);
        forgotBox.setDisable(true);
        forgotBox.setVisible(false);
        regBox.setDisable(false);
        regBox.setVisible(true);
    }
    @FXML
    public void goToForgotPassword(){
        logBox.setDisable(true);
        logBox.setVisible(false);
        regBox.setDisable(true);
        regBox.setVisible(false);
        forgotBox.setDisable(false);
        forgotBox.setVisible(true);
        //Setto codice box di default non visibile
        codiceBox.setVisible(false); //nascosta
        codiceBox.setDisable(true);
        passwordBox.setVisible(false); //nascosta
        passwordBox.setDisable(true);
        emailBox.setVisible(true); //Visibile
        emailBox.setDisable(false);
        this.infoUtenteResetPasw=null; //resetto
    }

    @FXML
    public void goToHome(Utente utenteConAccesso){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/praticaest1/praticaest1/home-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 700);
            Stage stage = (Stage) anchorBase.getScene().getWindow();
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResource("/praticaest1/praticaest1/img/logos64-icon.png").toString()));
            HomeController controller=fxmlLoader.getController();
            controller.setUtenteAttuale(utenteConAccesso);
            stage.setScene(scene);
        } catch (Exception e) {
            segnalaErrore(b1);
        }
    }

    //Azioni effettive
    @FXML
    public void login(){
        if(logEmail.getText().isBlank() || logPassword.getText().isBlank())
            segnalaErrore(b1);
        else{
            //Invio al server
            try {
                String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"login.php",mapper.writeValueAsString(new UserLogin(logEmail.getText(),logPassword.getText())));
                //Sarà sempre restituito un oggetto Utente variano i campi per capire se il login è accettato
                Utente userP=mapper.readValue(risposta, Utente.class);
                userP.setPsw(logPassword.getText().trim()); //Evito di fare l'unback hash
                if(userP.getEmail()==null && userP.getPsw()==null) {
                    segnalaErrore(b1); //Significa che il server ci sta dicendo che l'utente non esiste
                }else{
                    //Cambio schermata
                    goToHome(userP);
                }
            } catch (Exception e) {
                segnalaErrore(b1);
            }
        }
    }
    @FXML
    public void forgotPassword(){
        try {
            if(codiceBox.isDisable() && !emailBox.isDisable()){ //Fase 1
                if(emailV.getText().isBlank())
                    segnalaErrore(b3);
                else{ //Verifica email
                    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
                    JSONObject json = new JSONObject() //Così non creo la richiesta a mano magari sbagliando virgole o altro
                            .put("api_key", Dotenv.load().get("MAILEROO_CHECK_API_KEY"))
                            .put("email_address", emailV.getText().trim());

                    HttpRequest richiestaDiVerifica = HttpRequest.newBuilder()
                            .uri(URI.create("https://verify.maileroo.net/check"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8)) //Ovviamente dobbiamo definire la codifica
                            .build();
                    HttpResponse<String> risposta = client.send(richiestaDiVerifica, HttpResponse.BodyHandlers.ofString());
                    if(new JSONObject(risposta.body()).getBoolean("success")){ //Ricordiamoci che viene inviato un json come risposta, ovviamente potremmo fare una classe a doc ed usare il mapper ma non ho voglia per questo passaggio veloce
                        codiceBox.setVisible(true);
                        codiceBox.setDisable(false);

                        this.codice=(int)(Math.random() * 900000) + 100000;
                        String boundary = "----MailerooBoundary" + System.currentTimeMillis();

                        String body = "--" + boundary + "\r\n" +
                                "Content-Disposition: form-data; name=\"from\"\r\n\r\n" +
                                "\"SALVINO di WAYpp\" <" + Dotenv.load().get("MAILEROO_EMAIL_CHECKED") + ">\r\n" +
                                "--" + boundary + "\r\n" +
                                "Content-Disposition: form-data; name=\"to\"\r\n\r\n" +
                                emailV.getText().trim() + "\r\n" +
                                "--" + boundary + "\r\n" +
                                "Content-Disposition: form-data; name=\"subject\"\r\n\r\n" +
                                "Ripristino Password\r\n" +
                                "--" + boundary + "\r\n" +
                                "Content-Disposition: form-data; name=\"plain\"\r\n\r\n" +
                                "Codice di verifica: " + this.codice + "\r\n" +
                                "--" + boundary + "\r\n" +
                                "Content-Disposition: form-data; name=\"html\"\r\n\r\n" +
                                "<!DOCTYPE html>\n" +
                                "<html lang=\"it\">\n" +
                                "<head>\n" +
                                "    <meta charset=\"UTF-8\" />\n" +
                                "    <title>Ripristino Password - WAYpp</title>\n" +
                                "</head>\n" +
                                "<body style=\"font-family: Arial, sans-serif; color: #333;\">\n" +
                                "    <h2 style=\"color: #2a9df4;\">Ripristino Password - WAYpp</h2>\n" +
                                "    \n" +
                                "    <p>Ciao,</p>\n" +
                                "    \n" +
                                "    <p>Hai richiesto il ripristino della password per il tuo account su <strong>WAYpp</strong>, l'app che ti aiuta a organizzare i tuoi viaggi in modo semplice e smart.</p>\n" +
                                "    \n" +
                                "    <p>Se non sei stato tu a richiederlo, non preoccuparti: ignora pure questa email, nessuna modifica sarà effettuata sul tuo account.</p>\n" +
                                "    \n" +
                                "    <p>Grazie alla nuova intelligenza artificiale <strong>Salvino</strong>, WAYpp rende l’organizzazione dei viaggi ancora più facile e personalizzata, aiutandoti a scoprire mete uniche, pianificare itinerari e tenere tutto sotto controllo, anche nei momenti più impegnativi.</p>\n" +
                                "    \n" +
                                "    <h3>Il tuo codice di verifica è:</h3>\n" +
                                "    <p style=\"font-size: 24px; font-weight: bold; background-color: #f0f0f0; padding: 10px; display: inline-block; letter-spacing: 3px;\">\n" +
                                "        "+codice+"\n" +
                                "    </p>\n" +
                                "    \n" +
                                "    <p>Inserisci questo codice nell'app per completare il ripristino della password.</p>\n" +
                                "    \n" +
                                "    <p>Sei pronto a ripartire per la tua prossima avventura? Ricorda: <em>\"Il mondo è un libro, e chi non viaggia ne legge solo una pagina.\"</em></p>\n" +
                                "    \n" +
                                "    <p>Un caro saluto,<br />\n" +
                                "    Il team di WAYpp</p>\n" +
                                "</body>\n" +
                                "</html>\r\n" +
                                "--" + boundary + "--";

                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://smtp.maileroo.com/send"))
                                .header("X-API-Key", Dotenv.load().get("MAILEROO_SEND_API_KEY"))
                                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                                .build();

                        HttpResponse<String> risposta2 = HttpClient.newHttpClient()
                                .send(request, HttpResponse.BodyHandlers.ofString());
                        if (risposta2.statusCode() / 100 != 2) {
                            throw new RuntimeException("HTTP " + risposta2.statusCode() + " - " + risposta2.body());
                        }else{
                            if(!new JSONObject(risposta2.body()).getBoolean("success")) //Errore in invio o altro
                                segnalaErrore(b3);
                            else
                                this.infoUtenteResetPasw=new UserLogin(emailV.getText().trim(),"null");
                        }
                    }else
                        segnalaErrore(b3);
                }
            } else if (!codiceBox.isDisable() && !emailBox.isDisable()){ //FASE 2 - controllo codice
                if (Integer.parseInt(codiceV.getText().trim())!=this.codice){
                    segnalaErrore(b3);
                } else {
                    emailBox.setDisable(true); //Nascosta
                    emailBox.setVisible(false);
                    codiceBox.setDisable(true); //Nascosta
                    codiceBox.setVisible(false);
                    passwordBox.setDisable(false); //Visibile
                    passwordBox.setVisible(true);
                }
            } else if(codiceBox.isDisable() && emailBox.isDisable()){ //FASE 3 - Cambio password
                try {
                    this.infoUtenteResetPasw.setPassword(passwordV.getText().trim());
                    String risposta=gestoreHTTP.inviaRichiestaConParametri(URL_BASE+"userForgotPassword.php",mapper.writeValueAsString(this.infoUtenteResetPasw)); //Invio le nuove info al server
                    if (mapper.readValue(risposta, Utente.class).getNome()!=null) { //Tutto a buon fine
                        this.infoUtenteResetPasw=null; //resetto
                        goToLogin();
                    } else //Problemi
                        segnalaErrore(b3);
                } catch (Exception e) {
                    segnalaErrore(b3);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    @FXML
    public void registrati() {
        if (regEmail.getText().isBlank() || regNome.getText().isBlank() || regPassword1.getText().isBlank() || regPassword2.getText().isBlank() || !regPassword1.getText().trim().equalsIgnoreCase(regPassword2.getText().trim())){
            segnalaErrore(b2);
        }else{
            //Invio al server
            try {
                String risposta = gestoreHTTP.inviaRichiestaConParametri(URL_BASE + "register.php", mapper.writeValueAsString(new UserRegister(regNome.getText(), regEmail.getText(), regPassword1.getText())));
                //Sarà sempre restituito un oggetto Utente variano i campi per capire se la registrazione è accettata
                Utente userP = mapper.readValue(risposta, Utente.class);
                if (userP.getNome() == null && userP.getEmail() == null && userP.getPsw() == null){
                    segnalaErrore(b2); //Significa che il server ci sta dicendo che ci sono problemi con la registrazione
                }else{
                    //Cambio schermata
                    goToLogin();
                }
            } catch (Exception e) {
                segnalaErrore(b2);
            }
        }
    }

    //Errore
    @FXML
    private void segnalaErrore(Button button){
        button.setStyle("-fx-background-color: #BE2538; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 0; -fx-font-weight: bold;");
        PauseTransition pausa = new PauseTransition(Duration.seconds(3));
        pausa.setOnFinished(e -> {
            button.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 0; -fx-font-weight: bold;");
            if(button.getText().equalsIgnoreCase("Continua"))
                goToForgotPassword();
        });
        pausa.play();
    }
}
class UserLogin{
    private String email,password;
    public UserLogin() {}
    public UserLogin(String email, String password) {
        this.email = email.trim();
        this.password = password.trim();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
class UserRegister{
    private String nome,email,psw;

    public UserRegister() {}
    public UserRegister(String nome, String email, String psw) {
        this.nome = nome.trim();
        this.email = email.trim();
        this.psw = psw.trim();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPsw() { return psw; }
    public void setPsw(String psw) { this.psw = psw; }
}
