package praticaest1.praticaest1.obj;

import java.util.Objects;

public class Utente {
    private String nome,email,bio,psw;

    public Utente(){}
    public Utente(String nome, String email, String bio, String psw) {
        this.nome = nome.trim();
        this.email = email.trim();
        this.bio = bio;
        this.psw = psw.trim();
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPsw() { return psw; }
    public void setPsw(String psw) { this.psw = psw; }

    @Override
    public String toString() {
        return "Utente{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", psw='" + psw + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Utente){
            Utente utente=(Utente) o;
            return Objects.equals(getNome(), utente.getNome()) && Objects.equals(getEmail(), utente.getEmail()) && Objects.equals(getBio(), utente.getBio()) && Objects.equals(getPsw(), utente.getPsw());
        }
        return false;
    }

    public void aggiorna(String nome, String email, String bio, String psw){
        this.nome = nome.trim();
        this.email = email.trim();
        this.bio = bio;
        this.psw = psw.trim();
    }
}
