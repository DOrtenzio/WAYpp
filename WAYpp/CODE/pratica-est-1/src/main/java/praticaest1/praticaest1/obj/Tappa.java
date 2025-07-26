package praticaest1.praticaest1.obj;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import praticaest1.praticaest1.utility.*;
import java.time.LocalDate;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Tappa implements Comparable<Tappa>{
    private String nome;
    private LocalDate data;
    private double latitudine;
    private double longitudine;

    public Tappa(){}
    public Tappa(String nome, LocalDate data) {
        this.nome = nome;
        this.data = data;
        try {
            String coordinate= LocalizzatoreGeo.riceviLatLong(this.nome.replaceAll(" ", "%20"));
            this.latitudine = Double.parseDouble(coordinate.split("&")[0]);
            this.longitudine = Double.parseDouble(coordinate.split("&")[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Tappa(String nome, LocalDate data, double latitudine, double longitudine) {
        this.nome = nome;
        this.data = data;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    // Getters & setters
    public String getNome() { return nome; }
    public void setNome(String nome) {
        this.nome = nome;
        try { //Aggiorno di conseguenza
            String coordinate= LocalizzatoreGeo.riceviLatLong(this.nome.replaceAll(" ", "%20"));
            this.latitudine = Double.parseDouble(coordinate.split("&")[0]);
            this.longitudine = Double.parseDouble(coordinate.split("&")[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @JsonIgnore
    public void setOnlyNome(String nome){
        this.nome=nome;
    }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public double getLatitudine() { return latitudine; }
    public double getLongitudine() { return longitudine; }

    //Comaprable
    public int compareTo(Tappa tappa2){
        if (tappa2.getData().isBefore(this.data))
            return -1;
        else if (tappa2.getData().isAfter(this.data)) {
            return 1;
        } else return 0;
    }

    @Override
    public String toString() {
        return "Tappa{" +
                "nome='" + nome + '\'' +
                ", data=" + data +
                ", latitudine=" + latitudine +
                ", longitudine=" + longitudine +
                '}';
    }
}

