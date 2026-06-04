package com.mars.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class EstadisticaDetallada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double velocidadPunta;

    private Integer pasesUltimoTercio;

    private Integer duelosDefensivos;

    private Double expectedGoals;

    private Integer atajadas;

    @OneToOne
    @JoinColumn(name = "jugador_id", unique = true)
    private Jugador jugador;

    public EstadisticaDetallada() {
    }

    public EstadisticaDetallada(Double velocidadPunta, Integer pasesUltimoTercio, Integer duelosDefensivos, Double expectedGoals, Jugador jugador) {
        this.velocidadPunta = velocidadPunta;
        this.pasesUltimoTercio = pasesUltimoTercio;
        this.duelosDefensivos = duelosDefensivos;
        this.expectedGoals = expectedGoals;
        this.jugador = jugador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getVelocidadPunta() {
        return velocidadPunta;
    }

    public void setVelocidadPunta(Double velocidadPunta) {
        this.velocidadPunta = velocidadPunta;
    }

    public Integer getPasesUltimoTercio() {
        return pasesUltimoTercio;
    }

    public void setPasesUltimoTercio(Integer pasesUltimoTercio) {
        this.pasesUltimoTercio = pasesUltimoTercio;
    }

    public Integer getDuelosDefensivos() {
        return duelosDefensivos;
    }

    public void setDuelosDefensivos(Integer duelosDefensivos) {
        this.duelosDefensivos = duelosDefensivos;
    }

    public Double getExpectedGoals() {
        return expectedGoals;
    }

    public void setExpectedGoals(Double expectedGoals) {
        this.expectedGoals = expectedGoals;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public Integer getAtajadas() {
        return atajadas;
    }

    public void setAtajadas(Integer atajadas) {
        this.atajadas = atajadas;
    }
}
