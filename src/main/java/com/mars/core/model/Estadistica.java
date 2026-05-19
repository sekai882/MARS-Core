package com.mars.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Estadistica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int goles;

    private int pasesExitosos;

    private int minutos;

    private Double rating;

    @ManyToOne
    @JoinColumn(name = "jugador_id")
    private Jugador jugador;

    public Estadistica() {
    }

    public Estadistica(int goles, int pasesExitosos, int minutos, Double rating, Jugador jugador) {
        this.goles = goles;
        this.pasesExitosos = pasesExitosos;
        this.minutos = minutos;
        this.rating = rating;
        this.jugador = jugador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGoles() {
        return goles;
    }

    public void setGoles(int goles) {
        this.goles = goles;
    }

    public int getPasesExitosos() {
        return pasesExitosos;
    }

    public void setPasesExitosos(int pasesExitosos) {
        this.pasesExitosos = pasesExitosos;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public void setJugador(Jugador jugador) {
        this.jugador = jugador;
    }
}
