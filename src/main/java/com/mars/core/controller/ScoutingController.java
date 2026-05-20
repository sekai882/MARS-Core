package com.mars.core.controller;

import com.mars.core.dto.ScoutingFormDTO;
import com.mars.core.model.Jugador;
import com.mars.core.model.Position;
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
        model.addAttribute("scoutingForm", new ScoutingFormDTO());
        model.addAttribute("positions", Position.values());
        return "scouting/dashboard";
    }

    @PostMapping("/search")
    public String search(@ModelAttribute("scoutingForm") @Valid ScoutingFormDTO form, BindingResult result, Model model) {
        model.addAttribute("positions", Position.values());
        if (result.hasErrors()) {
            return "scouting/dashboard";
        }
        List<Jugador> jugadores = marsService.executeScouting(form.getBudget(), form.getPosition());
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
        Double valor = jugador.getValorMercado() != null ? jugador.getValorMercado() : 0.0;
        
        Double proyeccion2 = valor * (1 + (iem * 0.05) * 2);
        Double proyeccion5 = valor * (1 + (iem * 0.05) * 5);
        Double proyeccion10 = valor * (1 + (iem * 0.05) * 10);
        
        model.addAttribute("jugador", jugador);
        model.addAttribute("iem", iem);
        model.addAttribute("proyeccion2", proyeccion2);
        model.addAttribute("proyeccion5", proyeccion5);
        model.addAttribute("proyeccion10", proyeccion10);
        
        return "scouting/analisis";
    }
}
