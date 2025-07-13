package praticaest1.praticaest1.obj;

import praticaest1.praticaest1.utility.*;

import java.util.*;

public class Budget {
    private double budgetIniziale,budgetSpeso;
    private List<Pair<String,Double>> tieniConto; //Motivazione,Spesa
    //Costruttore
    public Budget(double budgetIniziale) {
        this.budgetIniziale = budgetIniziale;
        this.budgetSpeso = 0.0;
        this.tieniConto = new ArrayList<Pair<String,Double>>();
    }
    //Getter
    public double getBudgetIniziale() { return budgetIniziale; }
    public double getBudgetSpeso() { return budgetSpeso; }
    public List<Pair<String, Double>> getTieniConto() { return tieniConto; }

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
    public void aggiungiRendicontazione(String motivazione,Double cifra){ this.tieniConto.add(new Pair<>(motivazione,cifra)); }
    public void rimuoviRendicontazione(String motivazione,Double cifra){ this.tieniConto.remove(new Pair<>(motivazione,cifra)); }
    public String getLista(){
        String s="";
        for (Pair r:this.tieniConto) s=s+r.getKey()+"-"+r.getValue()+"\n";
        return s;
    }
}
