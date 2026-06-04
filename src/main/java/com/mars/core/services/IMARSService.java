package com.mars.core.services;

import java.util.List;
import com.mars.core.model.Jugador;
import com.mars.core.model.Position;
import com.mars.core.model.Estadistica;
import com.mars.core.model.EstadisticaDetallada;
import com.mars.core.dto.FiltroComplejoDTO;

public interface IMARSService {
    List<Jugador> executeScouting(Double budget, Position pos);
    List<Jugador> executeScouting(FiltroComplejoDTO filtro);
    Double calculateIEM(Long jugadorId);
    Double calculateIEM(Jugador jugador, Estadistica stats);
    Jugador getPlayerDetail(Long id);
    Estadistica getPlayerStats(Long jugadorId);
    EstadisticaDetallada getDetailedStats(Long jugadorId);
    Double calculateProjection(Long jugadorId, int años);
    Double getAgeFactor(Long jugadorId);
    Double getAgeFactor(int edadFutura);
    List<Jugador> suggestBestXI(Long clubId);
    Double calculatePositionalScore(Long jugadorId);
}
