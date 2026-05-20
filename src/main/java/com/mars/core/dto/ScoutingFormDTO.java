package com.mars.core.dto;

import com.mars.core.model.Position;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ScoutingFormDTO {

    @NotNull(message = "El presupuesto es obligatorio")
    @Min(value = 100000, message = "El presupuesto mínimo debe ser 100,000")
    private Double budget;

    @NotNull(message = "Debe seleccionar una posición táctica")
    private Position position;

    public ScoutingFormDTO() {
    }

    public ScoutingFormDTO(Double budget, Position position) {
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
}
