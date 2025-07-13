package praticaest1.praticaest1.obj;

import java.util.*;

public class ListaViaggi {
    private List<Viaggio> list;

    public ListaViaggi() {
        this.list = new ArrayList<>();
    }

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
