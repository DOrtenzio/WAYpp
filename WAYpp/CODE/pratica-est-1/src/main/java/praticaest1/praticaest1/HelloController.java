package praticaest1.praticaest1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import praticaest1.praticaest1.obj.Utente;
import praticaest1.praticaest1.utility.GestoreHTTP;

public class HelloController {
    @FXML
    private AnchorPane logBox,regBox,anchorBase; //Box o visioni alternative
    @FXML
    private TextField logEmail,logPassword,regNome,regEmail,regPassword1,regPassword2; //Elementi di lettura standard
    @FXML
    private Button b1,b2;
    //Gestori di richieste http
    private String URL_BASE;
    private final GestoreHTTP gestoreHTTP=new GestoreHTTP();
    private final ObjectMapper mapper=new ObjectMapper();

    public void initialize(){
        Dotenv dotenv= Dotenv.load();
        this.URL_BASE=dotenv.get("URL_BASE_SERVER");
    }

    //Scambio schermate
    @FXML
    public void goToLogin(){
        regBox.setDisable(true);
        regBox.setVisible(false);
        logBox.setDisable(false);
        logBox.setVisible(true);
    }
    @FXML
    public void goToRegister(){
        logBox.setDisable(true);
        logBox.setVisible(false);
        regBox.setDisable(false);
        regBox.setVisible(true);
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
