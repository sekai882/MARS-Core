package com.mars.core.controller;

import com.mars.core.dto.ReporteComparativo;
import com.mars.core.model.Jugador;
import com.mars.core.model.Estadistica;
import com.mars.core.model.EstadisticaDetallada;
import com.mars.core.services.IMARSService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ComparativaController {

    private final IMARSService marsService;

    public ComparativaController(IMARSService marsService) {
        this.marsService = marsService;
    }

    @GetMapping("/scouting/comparativa/{id1}/{id2}")
    public String compararJugadores(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2, Model model) {
        Jugador jugador1 = marsService.getPlayerDetail(id1);
        Jugador jugador2 = marsService.getPlayerDetail(id2);

        if (jugador1 == null || jugador2 == null) {
            return "redirect:/scouting";
        }

        Estadistica stats1 = marsService.getPlayerStats(id1);
        Estadistica stats2 = marsService.getPlayerStats(id2);

        EstadisticaDetallada detailedStats1 = marsService.getDetailedStats(id1);
        EstadisticaDetallada detailedStats2 = marsService.getDetailedStats(id2);

        // Crear reporte comparativo detallado
        ReporteComparativo reporte = new ReporteComparativo(
                jugador1, jugador2,
                stats1, stats2,
                detailedStats1, detailedStats2
        );

        model.addAttribute("reporte", reporte);
        return "scouting/comparativa";
    }
}
