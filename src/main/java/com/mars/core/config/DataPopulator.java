package com.mars.core.config;

import com.mars.core.model.Jugador;
import com.mars.core.model.EstadisticaDetallada;
import com.mars.core.repository.JugadorRepository;
import com.mars.core.repository.EstadisticaDetalladaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataPopulator implements CommandLineRunner {

    private final JugadorRepository jugadorRepository;
    private final EstadisticaDetalladaRepository detailedStatsRepository;

    public DataPopulator(JugadorRepository jugadorRepository, 
                         EstadisticaDetalladaRepository detailedStatsRepository) {
        this.jugadorRepository = jugadorRepository;
        this.detailedStatsRepository = detailedStatsRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Jugador> jugadores = jugadorRepository.findAll();
        
        for (Jugador jugador : jugadores) {
            if (detailedStatsRepository.findByJugadorId(jugador.getId()).isEmpty()) {
                double vel = 28.5 + (jugador.getId() % 8) * 1.1;
                int pases = 35 + (int)(jugador.getId() % 6) * 12;
                int duelos = 12 + (int)(jugador.getId() % 7) * 8;
                double xG = 0.5 + (jugador.getId() % 5) * 1.25;

                // Ajustar métricas según la posición para realismo analítico
                switch (jugador.getPosicion()) {
                    case DELANTERO:
                        vel += 3.5;
                        xG += 4.5;
                        duelos = Math.max(5, duelos - 5);
                        break;
                    case EXTREMO:
                        vel += 5.0;
                        pases += 15;
                        xG += 2.0;
                        break;
                    case PIVOTE:
                        pases += 30;
                        duelos += 20;
                        xG = 0.8;
                        break;
                    case DEFENSA:
                        vel = Math.max(22.0, vel - 2.0);
                        duelos += 35;
                        xG = 0.2;
                        break;
                }

                EstadisticaDetallada detailed = new EstadisticaDetallada(
                        Math.round(vel * 10.0) / 10.0,
                        pases,
                        duelos,
                        Math.round(xG * 100.0) / 100.0,
                        jugador
                );
                
                detailedStatsRepository.save(detailed);
                System.out.println("SEED: Creada Estadística Detallada para el jugador: " + jugador.getNombre());
            }
        }
    }
}
