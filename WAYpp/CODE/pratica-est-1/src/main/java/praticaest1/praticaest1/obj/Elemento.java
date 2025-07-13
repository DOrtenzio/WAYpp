package praticaest1.praticaest1.obj;

public class Elemento {
    private String nome,descrizione,luogoAcquisto;
    private int quantita;
    private boolean isAcquistato;
    //Costruttore
    public Elemento(String nome, String descrizione, String luogoAcquisto, int quantita) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.luogoAcquisto = luogoAcquisto;
        this.quantita = quantita;
        this.isAcquistato=false;
    }
    //getter e setter
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public String getLuogoAcquisto() { return luogoAcquisto; }
    public void setLuogoAcquisto(String luogoAcquisto) { this.luogoAcquisto = luogoAcquisto; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
    public boolean isAcquistato() { return isAcquistato; }
    public void setAcquistato(boolean acquistato) { isAcquistato = acquistato; }
    //equals e toString
    @Override
    public boolean equals(Object o) {
        if(o instanceof Elemento){
            Elemento e=(Elemento) o;
            return e.getNome().equalsIgnoreCase(this.nome) && this.quantita==e.getQuantita();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Elemento{" +
                "nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", luogoAcquisto='" + luogoAcquisto + '\'' +
                ", quantita=" + quantita +
                ", isAcquistato=" + isAcquistato +
                '}';
    }
}
