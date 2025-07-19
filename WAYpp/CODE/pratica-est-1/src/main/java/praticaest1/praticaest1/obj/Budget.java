package praticaest1.praticaest1.obj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import praticaest1.praticaest1.utility.*;
import java.util.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Budget {
    private double budgetIniziale,budgetSpeso;
    private List<MotivoSpesa> tieniConto; //Motivazione,Spesa
    //Costruttore
    public Budget(){}
    public Budget(double budgetIniziale) {
        this.budgetIniziale = budgetIniziale;
        this.budgetSpeso = 0.0;
        this.tieniConto = new ArrayList<MotivoSpesa>();
    }
    //Getter
    public double getBudgetIniziale() { return budgetIniziale; }
    public double getBudgetSpeso() { return budgetSpeso; }
    public List<MotivoSpesa> getTieniConto() { return tieniConto; }
    public void setBudgetIniziale(double budgetIniziale) { this.budgetIniziale = budgetIniziale; }
    public void setBudgetSpeso(double budgetSpeso) { this.budgetSpeso = budgetSpeso; }
    public void setTieniConto(List<MotivoSpesa> tieniConto) { this.tieniConto = tieniConto; }

    //Metodi di lavoro
    public void aggiungiNuovoBudget(double nuovoBudget){
        if (nuovoBudget<=0.0)
            throw new NumberFormatException("Budget da aggiungere negativo o nullo");
        else
            this.budgetIniziale+=nuovoBudget;
    }
    public void aggiungiSpesa(double spesa){
        if (spesa<=0.0)
            throw new NumberFormatException("Budget spesa negativo o nullo");
        else
            this.budgetSpeso+=spesa;
    }
    public void rimuoviBudget(double budgetDaRimuovere){
        if (budgetDaRimuovere<=0.0)
            throw new NumberFormatException("Budget da rimuovere negativo o nullo");
        else
            this.budgetIniziale-=budgetDaRimuovere;
    }
    public void rimuoviSpesa(double spesaDaRimuovere){
        if (spesaDaRimuovere<=0.0)
            throw new NumberFormatException("Budget da rimuovere negativo o nullo");
        else
            this.budgetSpeso-=spesaDaRimuovere;
    }
    //Rendicontazione
    public void aggiungiRendicontazione(String motivazione,Double cifra){ this.tieniConto.add(new MotivoSpesa(motivazione,cifra)); }
    public void rimuoviRendicontazione(String motivazione,Double cifra){ this.tieniConto.remove( new MotivoSpesa(motivazione,cifra)); }
    @JsonIgnore
    public String getLista(){
        String s="";
        for (MotivoSpesa r:this.tieniConto) s=s+r.getMotivazione()+"-"+r.getCifra()+"\n";
        return s;
    }
}
