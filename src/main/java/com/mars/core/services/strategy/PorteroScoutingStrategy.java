package com.mars.core.services.strategy;

import com.mars.core.model.EstadisticaDetallada;
import com.mars.core.model.Position;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Estrategia de scouting especializada para el perfil táctico de PORTERO.
 * Evalúa métricas exclusivas de portería: atajadas y distribución
 * de pases largos al último tercio del campo.
 */
@Component
public class PorteroScoutingStrategy implements IPositionalScoutingStrategy {

    @Override
    public List<Position> getSupportedPositions() {
        return List.of(Position.PORTERO);
    }

    @Override
    public int calculateDominancePoints(EstadisticaDetallada candidate, EstadisticaDetallada rival) {
        int victoryCount = 0;

        int atajadasCand = (candidate != null && candidate.getAtajadas() != null) ? candidate.getAtajadas() : 0;
        int atajadasRival = (rival != null && rival.getAtajadas() != null) ? rival.getAtajadas() : 0;
        int pasesCand = (candidate != null && candidate.getPasesUltimoTercio() != null) ? candidate.getPasesUltimoTercio() : 0;
        int pasesRival = (rival != null && rival.getPasesUltimoTercio() != null) ? rival.getPasesUltimoTercio() : 0;

        if (atajadasCand > atajadasRival) victoryCount++;
        if (pasesCand > pasesRival) victoryCount++;

        return victoryCount;
    }

    @Override
    public int getKpiCount() {
        return 2;
    }

    @Override
    public double calculatePositionalScore(EstadisticaDetallada stats, Position position, Map<String, Double> weights) {
        if (stats == null || weights == null) {
            return 0.0;
        }
        double atajadas = stats.getAtajadas() != null ? stats.getAtajadas() : 0.0;
        double pases = stats.getPasesUltimoTercio() != null ? stats.getPasesUltimoTercio() : 0.0;

        return (atajadas * weights.getOrDefault("atajadas", 0.0))
             + (pases * weights.getOrDefault("pases", 0.0));
    }
}
