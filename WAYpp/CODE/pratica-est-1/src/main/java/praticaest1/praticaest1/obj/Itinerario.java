package praticaest1.praticaest1.obj;
import java.time.LocalDate;
import java.util.*;

public class Itinerario {
    private String nome;
    private List<Tappa> tappe;

    public Itinerario() {}
    public Itinerario(String nome) {
        this.nome = nome;
        this.tappe = new ArrayList<>();
    }

    //Getter e setter
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public void setTappe(List<Tappa> tappe) { this.tappe = tappe; }

    //Gestione Tappe
    public void aggiungiTappa(Tappa tappa) { tappe.add(tappa); }
    public void rimuoviTappa(Tappa tappa) { tappe.remove(tappa); }
    public List<Tappa> getTappe() { return tappe; }

    //Calendario associato a tappe
    public List<LocalDate> getDateTappe(){
        List<LocalDate> dateTappe=new ArrayList<>();
        for (Tappa t:this.tappe) dateTappe.add(t.getData());
        return dateTappe;
    }
    public void ordinaTappe(){ Collections.sort(this.tappe); }
}

