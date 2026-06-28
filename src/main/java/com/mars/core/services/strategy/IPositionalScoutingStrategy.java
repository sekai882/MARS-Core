package com.mars.core.services.strategy;

import com.mars.core.model.EstadisticaDetallada;
import com.mars.core.model.Position;

import java.util.List;
import java.util.Map;

/**
 * Contrato del patrón Strategy para la evaluación posicional en la Matriz de Dominancia Competitiva.
 * Cada implementación encapsula las métricas específicas de un perfil táctico,
 * permitiendo extender el sistema con nuevas posiciones sin modificar el código existente (OCP).
 */
public interface IPositionalScoutingStrategy {

    /**
     * Retorna las posiciones tácticas que esta estrategia soporta.
     *
     * @return lista de posiciones compatibles con esta implementación
     */
    List<Position> getSupportedPositions();

    /**
     * Calcula los puntos de victoria del candidato contra un par competitivo
     * en el cruce bidimensional de KPIs de la Matriz de Dominancia.
     *
     * @param candidate estadísticas detalladas del sujeto de estudio
     * @param rival     estadísticas detalladas del par competitivo
     * @return número de KPIs en los que el candidato supera al rival
     */
    int calculateDominancePoints(EstadisticaDetallada candidate, EstadisticaDetallada rival);

    /**
     * Retorna el número total de KPIs que evalúa esta estrategia,
     * utilizado como denominador en el cálculo del porcentaje de dominancia.
     *
     * @return cantidad de métricas evaluadas (ej. 2 para portero, 4 para campo)
     */
    int getKpiCount();

    /**
     * Calcula el score posicional ponderado de un jugador aplicando
     * los pesos relativos definidos para su posición táctica.
     *
     * @param stats    estadísticas detalladas del jugador
     * @param position posición táctica del jugador
     * @param weights  mapa de pesos relativos por métrica
     * @return score posicional ponderado
     */
    double calculatePositionalScore(EstadisticaDetallada stats, Position position, Map<String, Double> weights);
}
