package praticaest1.praticaest1.obj;
import java.time.LocalDate;
import java.util.*;

public class Itinerario {
    private String nome;
    private List<Tappa> tappe;

    public Itinerario(String nome) {
        this.nome = nome;
        this.tappe = new ArrayList<>();
    }

    //Gestione Tappe
    public void aggiungiTappa(Tappa tappa) { tappe.add(tappa); }
    public List<Tappa> getTappe() { return tappe; }

    //Calendario associato a tappe
    public List<LocalDate> getDateTappe(){
        List<LocalDate> dateTappe=new ArrayList<>();
        for (Tappa t:this.tappe) dateTappe.add(t.getData());
        return dateTappe;
    }
}

