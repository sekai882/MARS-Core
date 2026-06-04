package com.mars.core.controller;

import com.mars.core.model.Club;
import com.mars.core.model.Estadistica;
import com.mars.core.model.EstadisticaDetallada;
import com.mars.core.model.Jugador;
import com.mars.core.model.Position;
import com.mars.core.repository.ClubRepository;
import com.mars.core.repository.EstadisticaDetalladaRepository;
import com.mars.core.repository.EstadisticaRepository;
import com.mars.core.repository.JugadorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/scouting/admin")
public class AdminController {

    private final JugadorRepository jugadorRepository;
    private final ClubRepository clubRepository;
    private final EstadisticaRepository estadisticaRepository;
    private final EstadisticaDetalladaRepository detailedStatsRepository;

    public AdminController(JugadorRepository jugadorRepository,
                           ClubRepository clubRepository,
                           EstadisticaRepository estadisticaRepository,
                           EstadisticaDetalladaRepository detailedStatsRepository) {
        this.jugadorRepository = jugadorRepository;
        this.clubRepository = clubRepository;
        this.estadisticaRepository = estadisticaRepository;
        this.detailedStatsRepository = detailedStatsRepository;
    }

    @GetMapping("/jugadores")
    public String listarJugadores(Model model) {
        List<Jugador> jugadores = jugadorRepository.findAllEager();
        java.util.Map<Long, Estadistica> statsMap = estadisticaRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(s -> s.getJugador().getId(), s -> s, (s1, s2) -> s1));
        for (Jugador j : jugadores) {
            j.setEstadistica(statsMap.get(j.getId()));
        }
        model.addAttribute("jugadores", jugadores);
        return "scouting/admin/jugadores";
    }

    @GetMapping("/jugadores/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("positions", Position.values());
        model.addAttribute("clubes", clubRepository.findAll());
        return "scouting/admin/nuevo-jugador";
    }

    @PostMapping("/jugadores/nuevo")
    public String crearJugador(
            @RequestParam String nombre,
            @RequestParam Integer edad,
            @RequestParam String nacionalidad,
            @RequestParam Position posicion,
            @RequestParam Long clubId,
            @RequestParam Double valorMercado,
            // Estadísticas base
            @RequestParam int goles,
            @RequestParam int pasesExitosos,
            @RequestParam int minutos,
            @RequestParam Double rating,
            // Estadísticas detalladas
            @RequestParam Double velocidadPunta,
            @RequestParam Double expectedGoals,
            @RequestParam Integer duelosDefensivos,
            @RequestParam Integer pasesUltimoTercio,
            @RequestParam Integer atajadas,
            Model model) {

        // 1. Buscar el club seleccionado
        Club club = clubRepository.findById(clubId).orElse(null);
        if (club == null) {
            model.addAttribute("error", "El club seleccionado no existe.");
            model.addAttribute("positions", Position.values());
            model.addAttribute("clubes", clubRepository.findAll());
            return "scouting/admin/nuevo-jugador";
        }

        // 2. Crear y guardar el jugador
        Jugador jugador = new Jugador();
        jugador.setNombre(nombre);
        jugador.setEdad(edad);
        jugador.setNacionalidad(nacionalidad);
        jugador.setPosicion(posicion);
        jugador.setClub(club);
        jugador.setValorMercado(valorMercado);
        jugadorRepository.save(jugador);

        // 3. Crear y guardar estadísticas base vinculadas al jugador
        Estadistica stats = new Estadistica();
        stats.setGoles(goles);
        stats.setPasesExitosos(pasesExitosos);
        stats.setMinutos(minutos);
        stats.setRating(rating);
        stats.setJugador(jugador);
        estadisticaRepository.save(stats);

        // 4. Crear y guardar estadísticas detalladas vinculadas al jugador
        EstadisticaDetallada detStats = new EstadisticaDetallada();
        detStats.setVelocidadPunta(velocidadPunta);
        detStats.setExpectedGoals(expectedGoals);
        detStats.setDuelosDefensivos(duelosDefensivos);
        detStats.setPasesUltimoTercio(pasesUltimoTercio);
        detStats.setAtajadas(atajadas);
        detStats.setJugador(jugador);
        detailedStatsRepository.save(detStats);

        return "redirect:/scouting/admin/jugadores";
    }
}
