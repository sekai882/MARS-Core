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
}
