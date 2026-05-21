package com.mars.core.controller;

import com.mars.core.dto.FiltroComplejoDTO;
import com.mars.core.model.Jugador;
import com.mars.core.model.Position;
import com.mars.core.model.Estadistica;
import com.mars.core.services.IMARSService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/scouting")
public class ScoutingController {

    private final IMARSService marsService;

    public ScoutingController(IMARSService marsService) {
        this.marsService = marsService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("scoutingForm", new FiltroComplejoDTO());
        model.addAttribute("positions", Position.values());
        return "scouting/dashboard";
    }

    @PostMapping("/search")
    public String search(@ModelAttribute("scoutingForm") @Valid FiltroComplejoDTO form, BindingResult result, Model model) {
        model.addAttribute("positions", Position.values());
        if (result.hasErrors()) {
            return "scouting/dashboard";
        }
        // Llamada al nuevo motor de analítica avanzado que recibe FiltroComplejoDTO
        List<Jugador> jugadores = marsService.executeScouting(form);
        model.addAttribute("jugadoresRecomendados", jugadores);
        return "scouting/dashboard";
    }

    @GetMapping("/analisis/{id}")
    public String analisis(@PathVariable("id") Long id, Model model) {
        Jugador jugador = marsService.getPlayerDetail(id);
        if (jugador == null) {
            return "redirect:/scouting";
        }
        
        Double iem = marsService.calculateIEM(id);
        Double proyeccion2 = marsService.calculateProjection(id, 2);
        Double proyeccion5 = marsService.calculateProjection(id, 5);
        Double proyeccion10 = marsService.calculateProjection(id, 10);
        Double factorEdad = marsService.getAgeFactor(id);
        
        Estadistica stats = marsService.getPlayerStats(id);
        
        model.addAttribute("jugador", jugador);
        model.addAttribute("iem", iem);
        model.addAttribute("proyeccion2", proyeccion2);
        model.addAttribute("proyeccion5", proyeccion5);
        model.addAttribute("proyeccion10", proyeccion10);
        model.addAttribute("factorEdad", factorEdad);
        model.addAttribute("stats", stats);
        
        return "scouting/analisis";
    }
}
