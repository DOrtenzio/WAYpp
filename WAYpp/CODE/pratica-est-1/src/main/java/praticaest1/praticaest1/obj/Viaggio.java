package praticaest1.praticaest1.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Viaggio {
    private String nomeUnivoco,mezzoUsato,obiettivo;
    private Budget budget;
    private Itinerario itinerario;
    private ListaElementi listaElementi;

    //Costruttore
    public Viaggio(){}
    public Viaggio(String nomeUnivoco,String mezzoUsato, String obiettivo, Budget budget, Itinerario itinerario, ListaElementi listaElementi) {
        this.nomeUnivoco=nomeUnivoco;
        this.mezzoUsato=mezzoUsato;
        this.obiettivo=obiettivo;
        this.budget = budget;
        this.itinerario = itinerario;
        this.listaElementi = listaElementi;
    }

    //getter e setter
    public String getNomeUnivoco() { return nomeUnivoco; }
    public void setNomeUnivoco(String nomeUnivoco) { this.nomeUnivoco = nomeUnivoco; }
    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }
    public Itinerario getItinerario() { return itinerario; }
    public void setItinerario(Itinerario itinerario) { this.itinerario = itinerario; }
    public ListaElementi getListaElementi() { return listaElementi; }
    public void setListaElementi(ListaElementi listaElementi) { this.listaElementi = listaElementi; }
    public String getMezzoUsato() { return mezzoUsato; }
    public void setMezzoUsato(String mezzoUsato) { this.mezzoUsato = mezzoUsato; }
    public String getObiettivo() { return obiettivo; }
    public void setObiettivo(String obiettivo) { this.obiettivo = obiettivo; }

    //equals e to string
    @Override
    public boolean equals(Object o) {
        if(o instanceof Viaggio){
            Viaggio v=(Viaggio) o;
            return v.getNomeUnivoco().equalsIgnoreCase(this.nomeUnivoco);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Viaggio{" +
                "budget=" + budget +
                ", itinerario=" + itinerario +
                ", listaElementi=" + listaElementi +
                '}';
    }
}
