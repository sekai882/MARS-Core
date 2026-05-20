package com.mars.core.dto;

import com.mars.core.model.Position;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FiltroComplejoDTO {

    @NotNull(message = "El presupuesto es obligatorio")
    @Min(value = 100000, message = "El presupuesto mínimo debe ser 100,000")
    private Double budget;

    @NotNull(message = "Debe seleccionar una posición táctica")
    private Position position;

    private String edad;
    private String nacionalidad;
    private String liga;
    private String club;

    public FiltroComplejoDTO() {
    }

    public FiltroComplejoDTO(Double budget, Position position) {
        this.budget = budget;
        this.position = position;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getLiga() {
        return liga;
    }

    public void setLiga(String liga) {
        this.liga = liga;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }
}
