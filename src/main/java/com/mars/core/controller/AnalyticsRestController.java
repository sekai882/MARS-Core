package com.mars.core.controller;

import com.mars.core.dto.JoyaAnaliticaDTO;
import com.mars.core.model.Joya;
import com.mars.core.repository.JoyaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para endpoints analíticos del sistema de scouting (API RESTful JSON).
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsRestController {

    private final JoyaRepository joyaRepository;

    public AnalyticsRestController(JoyaRepository joyaRepository) {
        this.joyaRepository = joyaRepository;
    }

    /**
     * Retorna el Top 5 de joyas ocultas detectadas por el sistema.
     * Acceso público.
     *
     * @return Lista serializada de Joyas Analíticas
     */
    @GetMapping("/top-gems")
    public List<JoyaAnaliticaDTO> getTopGems() {
        List<Joya> joyas = joyaRepository.findTop5ByOrderByBusquedasDescMaxIemDesc();
        return joyas.stream()
                .map(joya -> new JoyaAnaliticaDTO(
                        joya.getId(),
                        joya.getJugador() != null ? joya.getJugador().getNombre() : "Desconocido",
                        joya.getJugador() != null && joya.getJugador().getPosicion() != null ? joya.getJugador().getPosicion().name() : "N/A",
                        joya.getJugador() != null && joya.getJugador().getClub() != null ? joya.getJugador().getClub().getNombre() : "Libre",
                        joya.getJugador() != null ? joya.getJugador().getValorMercado() : 0.0,
                        joya.getMaxIem(),
                        joya.getBusquedas()
                ))
                .collect(Collectors.toList());
    }
}
