package com.mars.core.services;

import com.mars.core.model.Jugador;
import com.mars.core.model.Position;
import com.mars.core.model.Estadistica;
import com.mars.core.model.EstadisticaDetallada;
import com.mars.core.dto.FiltroComplejoDTO;
import com.mars.core.repository.JugadorRepository;
import com.mars.core.repository.EstadisticaRepository;
import com.mars.core.repository.EstadisticaDetalladaRepository;
import com.mars.core.services.strategy.IPositionalScoutingStrategy;
import com.mars.core.services.strategy.ScoutingStrategyFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Fachada analítica de alto nivel del motor MARS-Core (Facade Pattern).
 * Coordina los componentes inyectados (repositorios, factoría de estrategias)
 * para orquestar los flujos de scouting, cálculo del IEM y sugerencia del Mejor XI.
 * Todas las dependencias se resuelven mediante interfaces abstractas (DIP).
 */
@Service
public class MARSServiceImpl implements IMARSService {

    private final JugadorRepository jugadorRepository;
    private final EstadisticaRepository estadisticaRepository;
    private final EstadisticaDetalladaRepository detailedStatsRepository;
    private final ScoutingStrategyFactory strategyFactory;

    /**
     * Configuración de pesos relativos según la posición táctica.
     * Cada mapa interior asocia el nombre de una métrica con su peso ponderado [0.0-1.0].
     */
    private static final Map<Position, Map<String, Double>> PESOS_POSICIONALES = new HashMap<>();

    static {
        Map<String, Double> pesosExtremo = new HashMap<>();
        pesosExtremo.put("velocidad", 0.3);
        pesosExtremo.put("pases", 0.3);
        pesosExtremo.put("duelos", 0.0);
        pesosExtremo.put("xG", 0.4);
        PESOS_POSICIONALES.put(Position.EXTREMO, pesosExtremo);

        Map<String, Double> pesosDelantero = new HashMap<>();
        pesosDelantero.put("velocidad", 0.2);
        pesosDelantero.put("pases", 0.1);
        pesosDelantero.put("duelos", 0.0);
        pesosDelantero.put("xG", 0.7);
        PESOS_POSICIONALES.put(Position.DELANTERO, pesosDelantero);

        Map<String, Double> pesosPivote = new HashMap<>();
        pesosPivote.put("velocidad", 0.1);
        pesosPivote.put("pases", 0.4);
        pesosPivote.put("duelos", 0.5);
        pesosPivote.put("xG", 0.0);
        PESOS_POSICIONALES.put(Position.PIVOTE, pesosPivote);

        Map<String, Double> pesosDefensa = new HashMap<>();
        pesosDefensa.put("velocidad", 0.2);
        pesosDefensa.put("pases", 0.0);
        pesosDefensa.put("duelos", 0.8);
        pesosDefensa.put("xG", 0.0);
        PESOS_POSICIONALES.put(Position.DEFENSA, pesosDefensa);

        Map<String, Double> pesosPortero = new HashMap<>();
        pesosPortero.put("velocidad", 0.0);
        pesosPortero.put("pases", 0.2);
        pesosPortero.put("duelos", 0.0);
        pesosPortero.put("xG", 0.0);
        pesosPortero.put("atajadas", 0.8);
        PESOS_POSICIONALES.put(Position.PORTERO, pesosPortero);
    }

    public MARSServiceImpl(JugadorRepository jugadorRepository,
                            EstadisticaRepository estadisticaRepository,
                            EstadisticaDetalladaRepository detailedStatsRepository,
                            ScoutingStrategyFactory strategyFactory) {
        this.jugadorRepository = jugadorRepository;
        this.estadisticaRepository = estadisticaRepository;
        this.detailedStatsRepository = detailedStatsRepository;
        this.strategyFactory = strategyFactory;
    }

    @Override
    public Double calculateIEM(Jugador jugador, Estadistica stats) {
        if (jugador == null || jugador.getValorMercado() == null || jugador.getValorMercado() == 0 || stats == null) {
            return 0.0;
        }
        int goles = stats.getGoles();
        int pases = stats.getPasesExitosos();
        int minutos = stats.getMinutos();
        double rating = stats.getRating() != null ? stats.getRating() : 0.0;

        double rendimiento = (goles * 50.0) + (pases * 2.0) + (minutos * 0.5) + (rating * 100.0);

        double logPrecio = Math.log10(jugador.getValorMercado());
        if (logPrecio <= 0) logPrecio = 1.0;

        double iemBase = rendimiento / logPrecio;
        double iemScaled = iemBase / 100.0;

        double iemFinal = Math.min(10.0, Math.max(0.0, iemScaled));

        return Math.round(iemFinal * 100.0) / 100.0;
    }

    @Override
    public Double calculateIEM(Long jugadorId) {
        Jugador jugador = jugadorRepository.findById(jugadorId).orElse(null);
        if (jugador == null) return 0.0;
        List<Estadistica> statsList = estadisticaRepository.findByJugadorId(jugadorId);
        Estadistica stats = (statsList != null && !statsList.isEmpty()) ? statsList.get(0) : null;
        return calculateIEM(jugador, stats);
    }

    @Override
    public List<Jugador> executeScouting(Double budget, Position pos) {
        List<Jugador> jugadores = jugadorRepository.findAllEager();
        Map<Long, Estadistica> statsMap = estadisticaRepository.findAll().stream()
                .collect(Collectors.toMap(s -> s.getJugador().getId(), s -> s, (s1, s2) -> s1));

        return jugadores.stream()
                .filter(j -> j.getPosicion() == pos)
                .filter(j -> j.getValorMercado() != null && j.getValorMercado() <= budget)
                .sorted((j1, j2) -> Double.compare(calculateIEM(j2, statsMap.get(j2.getId())), calculateIEM(j1, statsMap.get(j1.getId()))))
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Ejecuta el motor de scouting avanzado con filtros complejos y Matriz de Dominancia Competitiva.
     * Delega la resolución posicional de KPIs a la factoría de estrategias (Strategy + Factory Method),
     * eliminando la bifurcación condicional por posición.
     */
    @Override
    public List<Jugador> executeScouting(FiltroComplejoDTO filtro) {
        List<Jugador> todos = jugadorRepository.findAllEager();
        List<Jugador> candidatos = new ArrayList<>();

        for (Jugador jugador : todos) {
            if (jugador.getPosicion() != filtro.getPosition()) {
                continue;
            }
            if (jugador.getValorMercado() != null && jugador.getValorMercado() > filtro.getBudget()) {
                continue;
            }

            if (filtro.getNacionalidad() != null && !filtro.getNacionalidad().equals("todas") && !filtro.getNacionalidad().isEmpty()) {
                String nac = jugador.getNacionalidad().toLowerCase();
                if (filtro.getNacionalidad().equals("es") && !nac.contains("esp")) continue;
                if (filtro.getNacionalidad().equals("br") && !nac.contains("bra")) continue;
                if (filtro.getNacionalidad().equals("fr") && !nac.contains("fra")) continue;
                if (filtro.getNacionalidad().equals("ar") && !nac.contains("arg")) continue;
            }

            if (filtro.getEdad() != null && !filtro.getEdad().isEmpty() && !filtro.getEdad().equals("todas")) {
                String ageFilter = filtro.getEdad();
                int edad = jugador.getEdad() != null ? jugador.getEdad() : 0;

                if (ageFilter.equals("sub21")) {
                    if (edad < 16 || edad > 21) continue;
                } else if (ageFilter.equals("madurez")) {
                    if (edad < 22 || edad > 26) continue;
                } else if (ageFilter.equals("veteranos")) {
                    if (edad < 27) continue;
                }
            }

            candidatos.add(jugador);
        }

        List<Jugador> pares = new ArrayList<>();
        for (Jugador jugador : todos) {
            if (jugador.getPosicion() == filtro.getPosition()) {
                pares.add(jugador);
            }
        }

        IPositionalScoutingStrategy strategy = strategyFactory.getStrategy(filtro.getPosition());
        int totalPares = pares.size();
        int kpisEvaluados = strategy.getKpiCount();

        Map<Long, Double> dominanceScores = new HashMap<>();
        Map<Long, EstadisticaDetallada> detailedMap = detailedStatsRepository.findAll().stream()
                .collect(Collectors.toMap(d -> d.getJugador().getId(), d -> d, (d1, d2) -> d1));

        for (Jugador cand : candidatos) {
            EstadisticaDetallada statsCand = detailedMap.get(cand.getId());
            int victoryCount = 0;

            for (Jugador par : pares) {
                EstadisticaDetallada statsPar = detailedMap.get(par.getId());
                victoryCount += strategy.calculateDominancePoints(statsCand, statsPar);
            }

            double score = 0.0;
            if (totalPares > 0) {
                score = (double) victoryCount / (totalPares * kpisEvaluados);
            }
            dominanceScores.put(cand.getId(), score);
            cand.setDominanceScore(score);
        }

        candidatos.sort((j1, j2) -> Double.compare(dominanceScores.getOrDefault(j2.getId(), 0.0), dominanceScores.getOrDefault(j1.getId(), 0.0)));

        return candidatos.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * Calcula el score posicional ponderado delegando a la estrategia correspondiente
     * mediante la factoría de resolución (Factory Method + Strategy).
     */
    @Override
    public Double calculatePositionalScore(Long jugadorId) {
        Jugador jugador = jugadorRepository.findById(jugadorId).orElse(null);
        if (jugador == null) {
            return 0.0;
        }

        EstadisticaDetallada detStats = detailedStatsRepository.findByJugadorId(jugador.getId()).orElse(null);
        if (detStats == null) {
            return 0.0;
        }

        Position pos = jugador.getPosicion();
        Map<String, Double> pesos = PESOS_POSICIONALES.get(pos);
        if (pesos == null) {
            return 0.0;
        }

        IPositionalScoutingStrategy strategy = strategyFactory.getStrategy(pos);
        return strategy.calculatePositionalScore(detStats, pos, pesos);
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

    @Override
    public Double calculateProjection(Long jugadorId, int años) {
        Jugador jugador = jugadorRepository.findById(jugadorId).orElse(null);
        if (jugador == null || jugador.getEdad() == null) {
            return 0.0;
        }
        Double valorMercado = jugador.getValorMercado() != null ? jugador.getValorMercado() : 0.0;
        int edadFutura = jugador.getEdad() + años;
        double factorEdad = getAgeFactor(edadFutura);
        Double iem = calculateIEM(jugadorId);

        double performanceBonus = (iem * 0.03) * años;
        if (edadFutura >= 35) {
            performanceBonus = 0.0;
        } else if (edadFutura >= 31) {
            performanceBonus *= 0.15;
        }

        return (valorMercado * factorEdad) * (1.0 + performanceBonus);
    }

    @Override
    public Double getAgeFactor(Long jugadorId) {
        Jugador jugador = jugadorRepository.findById(jugadorId).orElse(null);
        if (jugador == null || jugador.getEdad() == null) {
            return 1.0;
        }
        return getAgeFactor(jugador.getEdad());
    }

    @Override
    public Double getAgeFactor(int edadFutura) {
        if (edadFutura < 23) {
            return 1.2;
        } else if (edadFutura >= 23 && edadFutura <= 30) {
            return 1.0;
        } else if (edadFutura >= 31 && edadFutura <= 34) {
            return 0.70;
        } else {
            double factor = 0.25 - ((edadFutura - 35) * 0.04);
            return Math.max(0.05, factor);
        }
    }

    @Override
    public List<Jugador> suggestBestXI(Long clubId) {
        List<Jugador> jugadores = jugadorRepository.findAllEager().stream()
                .filter(j -> clubId == 0L || (j.getClub() != null && j.getClub().getId().equals(clubId)))
                .collect(Collectors.toList());

        Map<Long, Estadistica> statsMap = estadisticaRepository.findAll().stream()
                .collect(Collectors.toMap(s -> s.getJugador().getId(), s -> s, (s1, s2) -> s1));

        Map<Long, Double> scores = new HashMap<>();
        for (Jugador j : jugadores) {
            scores.put(j.getId(), calculateIEM(j, statsMap.get(j.getId())));
        }

        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j1 = jugadores.get(i);
            for (int k = i + 1; k < jugadores.size(); k++) {
                Jugador j2 = jugadores.get(k);

                boolean mismaNac = j1.getNacionalidad() != null &&
                                   j1.getNacionalidad().equalsIgnoreCase(j2.getNacionalidad());

                boolean esDefensaYPivote = (j1.getPosicion() == Position.DEFENSA && j2.getPosicion() == Position.PIVOTE)
                        || (j1.getPosicion() == Position.PIVOTE && j2.getPosicion() == Position.DEFENSA);

                if (mismaNac && esDefensaYPivote) {
                    scores.put(j1.getId(), scores.get(j1.getId()) * 1.05);
                    scores.put(j2.getId(), scores.get(j2.getId()) * 1.05);
                }
            }
        }

        jugadores.sort((j1, j2) -> Double.compare(scores.get(j2.getId()), scores.get(j1.getId())));

        return jugadores.stream().limit(11).collect(Collectors.toList());
    }
}
