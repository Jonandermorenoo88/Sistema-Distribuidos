package es.ubu.gii.sd.epo2;

public class Pais {
    private String nombre;
    private String capital;
    private String continente;
    private int poblacion;
    private Long id;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCapital() { return capital; }
    public void setCapital(String capital) { this.capital = capital; }

    public String getContinente() { return continente; }
    public void setContinente(String continente) { this.continente = continente; }

    public int getPoblacion() { return poblacion; }
    public void setPoblacion(int poblacion) { this.poblacion = poblacion; }
}
