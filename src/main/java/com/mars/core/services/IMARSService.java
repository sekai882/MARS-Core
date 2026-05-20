package com.mars.core.services;

import java.util.List;
import com.mars.core.model.Jugador;
import com.mars.core.model.Position;

public interface IMARSService {
    List<Jugador> executeScouting(Double budget, Position pos);
    Double calculateIEM(Long jugadorId);
    Jugador getPlayerDetail(Long id);
}
