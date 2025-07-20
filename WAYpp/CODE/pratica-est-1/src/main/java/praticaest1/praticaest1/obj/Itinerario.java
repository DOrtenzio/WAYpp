package praticaest1.praticaest1.obj;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Itinerario {
    private String nome;
    private List<Tappa> tappe;
    public Itinerario(){}
    public Itinerario(String nome, LocalDate dataPrimaTappa) {
        this.nome = nome;
        this.tappe = new ArrayList<>();
        //Aggiungo la prima tappa
        this.aggiungiTappa(new Tappa(nome,dataPrimaTappa));
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
    @JsonIgnore
    public List<LocalDate> getDateTappe(){
        List<LocalDate> dateTappe=new ArrayList<>();
        for (Tappa t:this.tappe) dateTappe.add(t.getData());
        return dateTappe;
    }
    public void ordinaTappe(){ Collections.sort(this.tappe); }
}

