package com.mars.core.services;

import com.mars.core.model.Jugador;
import com.mars.core.model.Position;
import com.mars.core.model.Estadistica;
import com.mars.core.repository.JugadorRepository;
import com.mars.core.repository.EstadisticaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MARSServiceImpl implements IMARSService {

    private final JugadorRepository jugadorRepository;
    private final EstadisticaRepository estadisticaRepository;

    public MARSServiceImpl(JugadorRepository jugadorRepository, EstadisticaRepository estadisticaRepository) {
        this.jugadorRepository = jugadorRepository;
        this.estadisticaRepository = estadisticaRepository;
    }

    @Override
    public Double calculateIEM(Long jugadorId) {
        Jugador jugador = jugadorRepository.findById(jugadorId).orElse(null);
        if (jugador == null || jugador.getValorMercado() == null || jugador.getValorMercado() == 0) {
            return 0.0;
        }

        List<Estadistica> statsList = estadisticaRepository.findByJugadorId(jugadorId);
        if (statsList == null || statsList.isEmpty()) {
            return 0.0;
        }

        // Usamos el primer registro de estadísticas disponible (el de la temporada actual)
        Estadistica stats = statsList.get(0);
        
        int goles = stats.getGoles();
        int pases = stats.getPasesExitosos();
        int minutos = stats.getMinutos();
        double rating = stats.getRating() != null ? stats.getRating() : 0.0;

        return ((goles * 50.0) + (pases * 2.0) + (minutos * 0.5) + (rating * 100.0)) / (jugador.getValorMercado() / 100000.0);
    }

    @Override
    public List<Jugador> executeScouting(Double budget, Position pos) {
        List<Jugador> jugadores = jugadorRepository.findAll();

        return jugadores.stream()
                .filter(j -> j.getPosicion() == pos)
                .filter(j -> j.getValorMercado() != null && j.getValorMercado() <= budget)
                .sorted((j1, j2) -> Double.compare(calculateIEM(j2.getId()), calculateIEM(j1.getId())))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public Jugador getPlayerDetail(Long id) {
        return jugadorRepository.findById(id).orElse(null);
    }

    @Override
    public Estadistica getPlayerStats(Long jugadorId) {
        List<Estadistica> statsList = estadisticaRepository.findByJugadorId(jugadorId);
        return (statsList != null && !statsList.isEmpty()) ? statsList.get(0) : null;
    }
}
