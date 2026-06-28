package com.mars.core.services.strategy;

import com.mars.core.model.Position;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Fábrica de estrategias de scouting posicional (Factory Method + DIP).
 * Recibe mediante inyección de constructor la colección completa de estrategias
 * registradas como beans de Spring, y construye un mapa de resolución O(1)
 * indexado por el enum {@link Position}.
 */
@Component
public class ScoutingStrategyFactory {

    private final Map<Position, IPositionalScoutingStrategy> strategyMap;

    /**
     * Construye el mapa de resolución a partir de todas las implementaciones
     * de {@link IPositionalScoutingStrategy} inyectadas por el contenedor de Spring.
     *
     * @param strategies colección de estrategias descubiertas automáticamente por component-scan
     */
    public ScoutingStrategyFactory(List<IPositionalScoutingStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .flatMap(strategy -> strategy.getSupportedPositions().stream()
                        .map(position -> Map.entry(position, strategy)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Resuelve la estrategia de scouting correspondiente a la posición táctica indicada.
     *
     * @param position posición táctica del jugador a evaluar
     * @return implementación de estrategia correspondiente
     * @throws IllegalArgumentException si no existe estrategia registrada para la posición
     */
    public IPositionalScoutingStrategy getStrategy(Position position) {
        IPositionalScoutingStrategy strategy = strategyMap.get(position);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No se encontró estrategia de scouting registrada para la posición: " + position);
        }
        return strategy;
    }
}
