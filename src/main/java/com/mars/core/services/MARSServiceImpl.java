package com.mars.core.services;

import com.mars.core.model.Jugador;
import com.mars.core.model.Position;
import com.mars.core.model.Estadistica;
import com.mars.core.model.EstadisticaDetallada;
import com.mars.core.dto.FiltroComplejoDTO;
import com.mars.core.repository.JugadorRepository;
import com.mars.core.repository.EstadisticaRepository;
import com.mars.core.repository.EstadisticaDetalladaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class MARSServiceImpl implements IMARSService {

    private final JugadorRepository jugadorRepository;
    private final EstadisticaRepository estadisticaRepository;
    private final EstadisticaDetalladaRepository detailedStatsRepository;

    // Configuración de pesos relativos según la posición
    private static final Map<Position, Map<String, Double>> PESOS_POSICIONALES = new HashMap<>();

    static {
        // EXTREMO: velocidad (0.3), pasesUltimoTercio (0.3), xG (0.4)
        Map<String, Double> pesosExtremo = new HashMap<>();
        pesosExtremo.put("velocidad", 0.3);
        pesosExtremo.put("pases", 0.3);
        pesosExtremo.put("duelos", 0.0);
        pesosExtremo.put("xG", 0.4);
        PESOS_POSICIONALES.put(Position.EXTREMO, pesosExtremo);

        // DELANTERO: velocidad (0.2), pasesUltimoTercio (0.1), xG (0.7)
        Map<String, Double> pesosDelantero = new HashMap<>();
        pesosDelantero.put("velocidad", 0.2);
        pesosDelantero.put("pases", 0.1);
        pesosDelantero.put("duelos", 0.0);
        pesosDelantero.put("xG", 0.7);
        PESOS_POSICIONALES.put(Position.DELANTERO, pesosDelantero);

        // PIVOTE: velocidad (0.1), pasesUltimoTercio (0.4), duelosDefensivos (0.5)
        Map<String, Double> pesosPivote = new HashMap<>();
        pesosPivote.put("velocidad", 0.1);
        pesosPivote.put("pases", 0.4);
        pesosPivote.put("duelos", 0.5);
        pesosPivote.put("xG", 0.0);
        PESOS_POSICIONALES.put(Position.PIVOTE, pesosPivote);

        // DEFENSA: velocidad (0.2), duelosDefensivos (0.8)
        Map<String, Double> pesosDefensa = new HashMap<>();
        pesosDefensa.put("velocidad", 0.2);
        pesosDefensa.put("pases", 0.0);
        pesosDefensa.put("duelos", 0.8);
        pesosDefensa.put("xG", 0.0);
        PESOS_POSICIONALES.put(Position.DEFENSA, pesosDefensa);
    }

    public MARSServiceImpl(JugadorRepository jugadorRepository, 
                            EstadisticaRepository estadisticaRepository,
                            EstadisticaDetalladaRepository detailedStatsRepository) {
        this.jugadorRepository = jugadorRepository;
        this.estadisticaRepository = estadisticaRepository;
        this.detailedStatsRepository = detailedStatsRepository;
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
    public List<Jugador> executeScouting(FiltroComplejoDTO filtro) {
        String clubName = (filtro.getClub() != null && !filtro.getClub().isEmpty()) ? filtro.getClub() : "Todos los Clubes";
        System.out.println("DEBUG: Iniciando ciclo iterativo de analítica avanzada para el club: " + clubName);

        List<Jugador> todos = jugadorRepository.findAll();
        List<Jugador> filtrados = new java.util.ArrayList<>();

        // Bucle explícito para recorrer la lista de jugadores y aplicar filtros base
        for (Jugador jugador : todos) {
            if (jugador.getPosicion() != filtro.getPosition()) {
                continue;
            }
            if (jugador.getValorMercado() != null && jugador.getValorMercado() > filtro.getBudget()) {
                continue;
            }

            // Filtro dinámico opcional por nacionalidad
            if (filtro.getNacionalidad() != null && !filtro.getNacionalidad().equals("todas") && !filtro.getNacionalidad().isEmpty()) {
                String nac = jugador.getNacionalidad().toLowerCase();
                if (filtro.getNacionalidad().equals("es") && !nac.contains("esp")) continue;
                if (filtro.getNacionalidad().equals("br") && !nac.contains("bra")) continue;
                if (filtro.getNacionalidad().equals("fr") && !nac.contains("fra")) continue;
                if (filtro.getNacionalidad().equals("ar") && !nac.contains("arg")) continue;
            }

            filtrados.add(jugador);
        }

        // Ordenamos los jugadores según su score posicional avanzado
        filtrados.sort((j1, j2) -> Double.compare(calculatePositionalScore(j2), calculatePositionalScore(j1)));

        return filtrados.stream().limit(5).collect(Collectors.toList());
    }

    private Double calculatePositionalScore(Jugador jugador) {
        EstadisticaDetallada detStats = detailedStatsRepository.findByJugadorId(jugador.getId()).orElse(null);
        if (detStats == null) {
            return 0.0;
        }

        Position pos = jugador.getPosicion();
        Map<String, Double> pesos = PESOS_POSICIONALES.get(pos);
        if (pesos == null) {
            return 0.0;
        }

        double score = 0.0;
        double vel = detStats.getVelocidadPunta() != null ? detStats.getVelocidadPunta() : 0.0;
        double pases = detStats.getPasesUltimoTercio() != null ? detStats.getPasesUltimoTercio() : 0.0;
        double duelos = detStats.getDuelosDefensivos() != null ? detStats.getDuelosDefensivos() : 0.0;
        double xG = detStats.getExpectedGoals() != null ? detStats.getExpectedGoals() : 0.0;

        // Estructura switch-case requerida para aplicar pesos e IEM
        switch (pos) {
            case EXTREMO:
                score = (vel * pesos.get("velocidad")) + (pases * pesos.get("pases")) + (xG * pesos.get("xG"));
                break;
            case DELANTERO:
                score = (vel * pesos.get("velocidad")) + (pases * pesos.get("pases")) + (xG * pesos.get("xG"));
                break;
            case PIVOTE:
                score = (vel * pesos.get("velocidad")) + (pases * pesos.get("pases")) + (duelos * pesos.get("duelos"));
                break;
            case DEFENSA:
                score = (vel * pesos.get("velocidad")) + (duelos * pesos.get("duelos"));
                break;
            default:
                score = 0.0;
        }

        return score;
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

    @Override
    public EstadisticaDetallada getDetailedStats(Long jugadorId) {
        return detailedStatsRepository.findByJugadorId(jugadorId).orElse(null);
    }
}
