package praticaest1.praticaest1.obj;

import praticaest1.praticaest1.utility.*;
import java.time.LocalDate;

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
            String coordinate= LocalizzatoreGeo.riceviLatLong(nome);
            this.latitudine = Double.parseDouble(coordinate.split("-")[0]);
            this.longitudine = Double.parseDouble(coordinate.split("-")[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Getters & setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
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
}

