package com.mars.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;

@Entity
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Double valorMercado;

    private String nacionalidad;

    private Integer edad;

    @Enumerated(EnumType.STRING)
    private Position posicion;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    public Jugador() {
    }

    public Jugador(String nombre, Double valorMercado, String nacionalidad, Integer edad, Position posicion, Club club) {
        this.nombre = nombre;
        this.valorMercado = valorMercado;
        this.nacionalidad = nacionalidad;
        this.edad = edad;
        this.posicion = posicion;
        this.club = club;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getValorMercado() {
        return valorMercado;
    }

    public void setValorMercado(Double valorMercado) {
        this.valorMercado = valorMercado;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Position getPosicion() {
        return posicion;
    }

    public void setPosicion(Position posicion) {
        this.posicion = posicion;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    @Transient
    private Double dominanceScore;

    public Double getDominanceScore() {
        return dominanceScore;
    }

    public void setDominanceScore(Double dominanceScore) {
        this.dominanceScore = dominanceScore;
    }

    @OneToOne(mappedBy = "jugador", fetch = FetchType.LAZY)
    private EstadisticaDetallada estadisticaDetallada;

    public EstadisticaDetallada getEstadisticaDetallada() {
        return estadisticaDetallada;
    }

    public void setEstadisticaDetallada(EstadisticaDetallada estadisticaDetallada) {
        this.estadisticaDetallada = estadisticaDetallada;
    }

    @Transient
    private transient com.mars.core.model.Estadistica estadistica;

    public com.mars.core.model.Estadistica getEstadistica() {
        return estadistica;
    }

    public void setEstadistica(com.mars.core.model.Estadistica estadistica) {
        this.estadistica = estadistica;
    }
}
