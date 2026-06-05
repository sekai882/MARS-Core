package com.mars.core.controller;

import com.mars.core.dto.FiltroComplejoDTO;
import com.mars.core.model.Club;
import com.mars.core.model.Jugador;
import com.mars.core.model.Position;
import com.mars.core.model.Estadistica;
import com.mars.core.model.Joya;
import com.mars.core.repository.ClubRepository;
import com.mars.core.repository.EstadisticaRepository;
import com.mars.core.repository.JoyaRepository;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/scouting")
public class ScoutingController {

    private final IMARSService marsService;
    private final ClubRepository clubRepository;
    private final EstadisticaRepository estadisticaRepository;
    private final JoyaRepository joyaRepository;

    public ScoutingController(IMARSService marsService, ClubRepository clubRepository, EstadisticaRepository estadisticaRepository, JoyaRepository joyaRepository) {
        this.marsService = marsService;
        this.clubRepository = clubRepository;
        this.estadisticaRepository = estadisticaRepository;
        this.joyaRepository = joyaRepository;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("scoutingForm", new FiltroComplejoDTO());
        model.addAttribute("positions", Position.values());
        model.addAttribute("topJoyas", joyaRepository.findTop5ByOrderByBusquedasDescMaxIemDesc());
        return "scouting/dashboard";
    }

    @PostMapping("/search")
    public String search(@ModelAttribute("scoutingForm") @Valid FiltroComplejoDTO form, BindingResult result, Model model) {
        model.addAttribute("positions", Position.values());
        if (result.hasErrors()) {
            return "scouting/dashboard";
        }
        // Invocación del motor analítico avanzado mediante transferencia de DTO complejo
        List<Jugador> jugadores = marsService.executeScouting(form);
        java.util.Map<Long, Estadistica> statsMap = estadisticaRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(s -> s.getJugador().getId(), s -> s, (s1, s2) -> s1));
        for (Jugador j : jugadores) {
            j.setEstadistica(statsMap.get(j.getId()));
            Double iem = marsService.calculateIEM(j, j.getEstadistica());
            if (iem != null && iem > 6.0) {
                java.util.Optional<Joya> existingJoya = joyaRepository.findByJugadorId(j.getId());
                if (existingJoya.isPresent()) {
                    Joya joya = existingJoya.get();
                    joya.setBusquedas(joya.getBusquedas() + 1);
                    if (iem > joya.getMaxIem()) {
                        joya.setMaxIem(iem);
                    }
                    joyaRepository.save(joya);
                } else {
                    Joya nuevaJoya = new Joya(j, 1, iem);
                    joyaRepository.save(nuevaJoya);
                }
            }
        }
        model.addAttribute("jugadoresRecomendados", jugadores);
        model.addAttribute("topJoyas", joyaRepository.findTop5ByOrderByBusquedasDescMaxIemDesc());
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
        Double positionalScore = marsService.calculatePositionalScore(id);
        
        model.addAttribute("jugador", jugador);
        model.addAttribute("iem", iem);
        model.addAttribute("proyeccion2", proyeccion2);
        model.addAttribute("proyeccion5", proyeccion5);
        model.addAttribute("proyeccion10", proyeccion10);
        model.addAttribute("factorEdad", factorEdad);
        model.addAttribute("stats", stats);
        model.addAttribute("positionalScore", positionalScore);
        
        return "scouting/analisis";
    }

    @GetMapping("/bestxi")
    public String bestXI(@RequestParam(value = "clubId", required = false) Long clubId, Model model) {
        List<Club> clubes = clubRepository.findAll();
        model.addAttribute("clubes", clubes);

        Long effectiveClubId = (clubId != null) ? clubId : 0L;
        List<Jugador> mejorXI = marsService.suggestBestXI(effectiveClubId);
        model.addAttribute("mejorXI", mejorXI);
        model.addAttribute("selectedClubId", effectiveClubId);
        
        java.util.Map<Long, Estadistica> statsMap = estadisticaRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(s -> s.getJugador().getId(), s -> s, (s1, s2) -> s1));

        // Calcular IEM para cada jugador del XI
        java.util.Map<Long, Double> iemMap = new java.util.HashMap<>();
        for (Jugador j : mejorXI) {
            iemMap.put(j.getId(), marsService.calculateIEM(j, statsMap.get(j.getId())));
        }
        model.addAttribute("iemMap", iemMap);

        return "scouting/bestxi";
    }
}
