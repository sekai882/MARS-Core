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
        System.out.println("DEBUG: Iniciando matriz de dominancia competitiva para el club: " + clubName);

        List<Jugador> todos = jugadorRepository.findAll();
        List<Jugador> candidatos = new java.util.ArrayList<>();

        // 1. Filtra primero los jugadores por posición y presupuesto máximo en un bucle inicial
        for (Jugador jugador : todos) {
            if (jugador.getPosicion() != filtro.getPosition()) {
                continue;
            }
            if (jugador.getValorMercado() != null && jugador.getValorMercado() > filtro.getBudget()) {
                continue;
            }
            
            // Opcional: Filtro dinámico opcional por nacionalidad
            if (filtro.getNacionalidad() != null && !filtro.getNacionalidad().equals("todas") && !filtro.getNacionalidad().isEmpty()) {
                String nac = jugador.getNacionalidad().toLowerCase();
                if (filtro.getNacionalidad().equals("es") && !nac.contains("esp")) continue;
                if (filtro.getNacionalidad().equals("br") && !nac.contains("bra")) continue;
                if (filtro.getNacionalidad().equals("fr") && !nac.contains("fra")) continue;
                if (filtro.getNacionalidad().equals("ar") && !nac.contains("arg")) continue;
            }
            
            candidatos.add(jugador);
        }

        // Obtener todos los pares competitivos
        List<Jugador> pares = new java.util.ArrayList<>();
        for (Jugador jugador : todos) {
            if (jugador.getPosicion() == filtro.getPosition()) {
                pares.add(jugador);
            }
        }

        int totalPares = pares.size();
        int kpisEvaluados = 4; // velocidad, expectedGoals, duelosDefensivos, pasesUltimoTercio

        // 2. Estructura interna para almacenar DominanceScore por candidato
        Map<Long, Double> dominanceScores = new HashMap<>();

        // 3. Bucle Externo (for) que recorra la lista de candidatos prefiltrados
        for (Jugador cand : candidatos) {
            EstadisticaDetallada statsCand = detailedStatsRepository.findByJugadorId(cand.getId()).orElse(null);
            double velCand = (statsCand != null && statsCand.getVelocidadPunta() != null) ? statsCand.getVelocidadPunta() : 0.0;
            double xGCand = (statsCand != null && statsCand.getExpectedGoals() != null) ? statsCand.getExpectedGoals() : 0.0;
            int duelosCand = (statsCand != null && statsCand.getDuelosDefensivos() != null) ? statsCand.getDuelosDefensivos() : 0;
            int pasesCand = (statsCand != null && statsCand.getPasesUltimoTercio() != null) ? statsCand.getPasesUltimoTercio() : 0;

            int victoryCount = 0;

            // 4. Bucle Interno (for) que recorra la lista completa de todos los jugadores del sistema que jueguen en esa misma posición
            for (Jugador par : pares) {
                EstadisticaDetallada statsPar = detailedStatsRepository.findByJugadorId(par.getId()).orElse(null);
                double velPar = (statsPar != null && statsPar.getVelocidadPunta() != null) ? statsPar.getVelocidadPunta() : 0.0;
                double xGPar = (statsPar != null && statsPar.getExpectedGoals() != null) ? statsPar.getExpectedGoals() : 0.0;
                int duelosPar = (statsPar != null && statsPar.getDuelosDefensivos() != null) ? statsPar.getDuelosDefensivos() : 0;
                int pasesPar = (statsPar != null && statsPar.getPasesUltimoTercio() != null) ? statsPar.getPasesUltimoTercio() : 0;

                // 5. Comparaciones condicionales métrica por métrica
                if (velCand > velPar) victoryCount++;
                if (xGCand > xGPar) victoryCount++;
                if (duelosCand > duelosPar) victoryCount++;
                if (pasesCand > pasesPar) victoryCount++;
            }

            // 6. Calcula el porcentaje final de dominancia de mercado
            double score = 0.0;
            if (totalPares > 0) {
                score = (double) victoryCount / (totalPares * kpisEvaluados);
            }
            dominanceScores.put(cand.getId(), score);
            cand.setDominanceScore(score);
        }

        // 7. Ordena la lista final de forma descendente y retorna los 5 mejores
        candidatos.sort((j1, j2) -> Double.compare(dominanceScores.getOrDefault(j2.getId(), 0.0), dominanceScores.getOrDefault(j1.getId(), 0.0)));

        return candidatos.stream().limit(5).collect(Collectors.toList());
    }

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
        return (valorMercado * factorEdad) * (1.0 + (iem * 0.03) * años);
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
            return 0.80;
        } else {
            return 0.45;
        }
    }

    @Override
    public List<Jugador> suggestBestXI(Long clubId) {
        List<Jugador> jugadores = jugadorRepository.findAll().stream()
                .filter(j -> clubId == 0L || (j.getClub() != null && j.getClub().getId().equals(clubId)))
                .collect(Collectors.toList());

        // Mapa para almacenar los scores ajustados con el bonus de química
        Map<Long, Double> scores = new HashMap<>();
        for (Jugador j : jugadores) {
            scores.put(j.getId(), calculateIEM(j.getId()));
        }

        // Bucle anidado para comparar nacionalidades y aplicar el bonus de química del 5%
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j1 = jugadores.get(i);
            for (int k = i + 1; k < jugadores.size(); k++) {
                Jugador j2 = jugadores.get(k);

                boolean mismaNac = j1.getNacionalidad() != null && 
                                   j1.getNacionalidad().equalsIgnoreCase(j2.getNacionalidad());
                
                boolean esDefensaYPivote = (j1.getPosicion() == Position.DEFENSA && j2.getPosicion() == Position.PIVOTE)
                        || (j1.getPosicion() == Position.PIVOTE && j2.getPosicion() == Position.DEFENSA);

                if (mismaNac && esDefensaYPivote) {
                    // Sinergia: Incrementar su score conjunto en 5%
                    scores.put(j1.getId(), scores.get(j1.getId()) * 1.05);
                    scores.put(j2.getId(), scores.get(j2.getId()) * 1.05);
                }
            }
        }

        // Ordenar por score ajustado descendentemente
        jugadores.sort((j1, j2) -> Double.compare(scores.get(j2.getId()), scores.get(j1.getId())));

        // Retornar los mejores jugadores
        return jugadores.stream().limit(11).collect(Collectors.toList());
    }
}
