package praticaest1.praticaest1.utility;

public class MotivoSpesa {
    private String motivazione;
    private Double cifra;

    public MotivoSpesa() {}
    public MotivoSpesa(String motivazione, Double cifra) {
        this.motivazione = motivazione;
        this.cifra = cifra;
    }

    public String getMotivazione() {
        return motivazione;
    }

    public void setMotivazione(String motivazione) {
        this.motivazione = motivazione;
    }

    public Double getCifra() {
        return cifra;
    }

    public void setCifra(Double cifra) {
        this.cifra = cifra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MotivoSpesa that = (MotivoSpesa) o;

        if (!motivazione.equals(that.motivazione)) return false;
        return cifra.equals(that.cifra);
    }

    @Override
    public int hashCode() {
        int result = motivazione.hashCode();
        result = 31 * result + cifra.hashCode();
        return result;
    }

}
