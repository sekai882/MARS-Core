package com.mars.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Joya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "jugador_id", unique = true)
    private Jugador jugador;

    private int busquedas;

    private Double maxIem;

    public Joya() {
    }

    public Joya(Jugador jugador, int busquedas, Double maxIem) {
        this.jugador = jugador;
        this.busquedas = busquedas;
        this.maxIem = maxIem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    public int getBusquedas() {
        return busquedas;
    }

    public void setBusquedas(int busquedas) {
        this.busquedas = busquedas;
    }

    public Double getMaxIem() {
        return maxIem;
    }

    public void setMaxIem(Double maxIem) {
        this.maxIem = maxIem;
    }
}
