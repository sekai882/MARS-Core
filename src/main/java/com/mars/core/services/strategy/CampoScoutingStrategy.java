package com.mars.core.services.strategy;

import com.mars.core.model.EstadisticaDetallada;
import com.mars.core.model.Position;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Estrategia de scouting para jugadores de campo: EXTREMO, DELANTERO, PIVOTE y DEFENSA.
 * Evalúa los cuatro KPIs escalares de rendimiento atlético y táctico:
 * velocidad punta, expected goals (xG), duelos defensivos y pases al último tercio.
 */
@Component
public class CampoScoutingStrategy implements IPositionalScoutingStrategy {

    @Override
    public List<Position> getSupportedPositions() {
        return List.of(Position.EXTREMO, Position.DELANTERO, Position.PIVOTE, Position.DEFENSA);
    }

    @Override
    public int calculateDominancePoints(EstadisticaDetallada candidate, EstadisticaDetallada rival) {
        int victoryCount = 0;

        double velCand = (candidate != null && candidate.getVelocidadPunta() != null) ? candidate.getVelocidadPunta() : 0.0;
        double velRival = (rival != null && rival.getVelocidadPunta() != null) ? rival.getVelocidadPunta() : 0.0;
        double xGCand = (candidate != null && candidate.getExpectedGoals() != null) ? candidate.getExpectedGoals() : 0.0;
        double xGRival = (rival != null && rival.getExpectedGoals() != null) ? rival.getExpectedGoals() : 0.0;
        int duelosCand = (candidate != null && candidate.getDuelosDefensivos() != null) ? candidate.getDuelosDefensivos() : 0;
        int duelosRival = (rival != null && rival.getDuelosDefensivos() != null) ? rival.getDuelosDefensivos() : 0;
        int pasesCand = (candidate != null && candidate.getPasesUltimoTercio() != null) ? candidate.getPasesUltimoTercio() : 0;
        int pasesRival = (rival != null && rival.getPasesUltimoTercio() != null) ? rival.getPasesUltimoTercio() : 0;

        if (velCand > velRival) victoryCount++;
        if (xGCand > xGRival) victoryCount++;
        if (duelosCand > duelosRival) victoryCount++;
        if (pasesCand > pasesRival) victoryCount++;

        return victoryCount;
    }

    @Override
    public int getKpiCount() {
        return 4;
    }

    @Override
    public double calculatePositionalScore(EstadisticaDetallada stats, Position position, Map<String, Double> weights) {
        if (stats == null || weights == null) {
            return 0.0;
        }

        double vel = stats.getVelocidadPunta() != null ? stats.getVelocidadPunta() : 0.0;
        double pases = stats.getPasesUltimoTercio() != null ? stats.getPasesUltimoTercio() : 0.0;
        double duelos = stats.getDuelosDefensivos() != null ? stats.getDuelosDefensivos() : 0.0;
        double xG = stats.getExpectedGoals() != null ? stats.getExpectedGoals() : 0.0;

        return (vel * weights.getOrDefault("velocidad", 0.0))
             + (pases * weights.getOrDefault("pases", 0.0))
             + (duelos * weights.getOrDefault("duelos", 0.0))
             + (xG * weights.getOrDefault("xG", 0.0));
    }
}
