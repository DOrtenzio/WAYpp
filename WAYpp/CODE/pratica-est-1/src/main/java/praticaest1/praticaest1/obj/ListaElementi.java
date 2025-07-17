package praticaest1.praticaest1.obj;

import java.util.ArrayList;
import java.util.List;

public class ListaElementi {
    private List<Elemento> list;
    private int elementiTot, elementiAcquistati;

    public ListaElementi() {
        this.list = new ArrayList<>();
        this.elementiTot = 0;
        this.elementiAcquistati = 0;
    }

    public int getElementiTot() { return elementiTot; }
    public int getElementiAcquistati() { return elementiAcquistati; }
    public List<Elemento> getList() { return list; }
    public void setList(List<Elemento> list) { this.list = list; }
    public void setElementiTot(int elementiTot) { this.elementiTot = elementiTot; }
    public void setElementiAcquistati(int elementiAcquistati) { this.elementiAcquistati = elementiAcquistati; }

    //Interfacciamento principale
    public void addElemento(Elemento e){
        this.list.add(e);
        elementiTot++;
    }
    public void removeElemento(Elemento e){
        this.list.remove(e);
        elementiTot--;
    }
    public void setElemento(Elemento eRicercato,Elemento eDaSostituire) throws Exception{
        int ind=this.list.indexOf(eRicercato);
        if (ind!=-1)
            this.list.set(ind,eDaSostituire);
        else
            throw new Exception("Elemento non trovato"); //Dato l'interfacciamento grafico sarà quasi inutile
    }
    public void setElementoAcquistato(Elemento e, boolean isAcq) throws Exception{ //A differenza del precedente in cui voglio cambiare vari elemnti fondamentali qui modifico solo il check
        int ind=this.list.indexOf(e);
        if (ind!=-1) {
            e.setAcquistato(isAcq);
            this.list.set(ind, e);
        } else
            throw new Exception("Elemento non trovato"); //Dato l'interfacciamento grafico sarà quasi inutile
    }
}
