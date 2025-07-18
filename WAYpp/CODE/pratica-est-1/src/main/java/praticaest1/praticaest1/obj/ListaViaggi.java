package praticaest1.praticaest1.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListaViaggi {
    private List<Viaggio> list;

    public ListaViaggi() {
        this.list = new ArrayList<>();
    }
    public ListaViaggi(List<Viaggio> list) { this.list = list; }

    public List<Viaggio> getList() { return list; }
    public void setList(List<Viaggio> list) { this.list = list; }

    //Interfacciamento principale
    public void addElemento(Viaggio v){
        this.list.add(v);
    }
    public void removeElemento(Viaggio v){
        this.list.remove(v);
    }
    public void setElemento(Viaggio vRicercato,Viaggio vDaSostituire) throws Exception{
        int ind=this.list.indexOf(vRicercato);
        if (ind!=-1)
            this.list.set(ind,vDaSostituire);
        else
            throw new Exception("Elemento non trovato"); //Dato l'interfacciamento grafico sarà quasi inutile
    }
}
