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
import org.springframework.web.bind.annotation.PathVariable;

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
    public String listarJugadores(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String dir,
            Model model) {
        
        org.springframework.data.domain.Sort sort = dir.equalsIgnoreCase("desc") 
                ? org.springframework.data.domain.Sort.by(sortBy).descending() 
                : org.springframework.data.domain.Sort.by(sortBy).ascending();
                
        List<Jugador> jugadores = jugadorRepository.findAllEager(sort);
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

        // Recuperación y validación de la relación Many-to-One con la entidad Club
        Club club = clubRepository.findById(clubId).orElse(null);
        if (club == null) {
            model.addAttribute("error", "El club seleccionado no existe.");
            model.addAttribute("positions", Position.values());
            model.addAttribute("clubes", clubRepository.findAll());
            return "scouting/admin/nuevo-jugador";
        }

        // Persistencia primaria de la entidad pivote (Jugador)
        Jugador jugador = new Jugador();
        jugador.setNombre(nombre);
        jugador.setEdad(edad);
        jugador.setNacionalidad(nacionalidad);
        jugador.setPosicion(posicion);
        jugador.setClub(club);
        jugador.setValorMercado(valorMercado);
        jugadorRepository.save(jugador);

        // Persistencia secundaria: Construcción de métricas base vinculadas mediante One-to-One
        Estadistica stats = new Estadistica();
        stats.setGoles(goles);
        stats.setPasesExitosos(pasesExitosos);
        stats.setMinutos(minutos);
        stats.setRating(rating);
        stats.setJugador(jugador);
        estadisticaRepository.save(stats);

        // Persistencia avanzada: Construcción de métricas tácticas vinculadas mediante One-to-One
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

    @GetMapping("/jugadores/editar/{id}")
    public String editarJugador(@PathVariable Long id, Model model) {
        Jugador jugador = jugadorRepository.findById(id).orElse(null);
        if (jugador == null) {
            return "redirect:/scouting/admin/jugadores";
        }
        
        List<Estadistica> statsList = estadisticaRepository.findByJugadorId(id);
        Estadistica stats = (statsList != null && !statsList.isEmpty()) ? statsList.get(0) : new Estadistica();
        jugador.setEstadistica(stats);

        EstadisticaDetallada detStats = detailedStatsRepository.findByJugadorId(id).orElse(new EstadisticaDetallada());

        model.addAttribute("jugador", jugador);
        model.addAttribute("estadistica", stats);
        model.addAttribute("detStats", detStats);
        model.addAttribute("positions", Position.values());
        model.addAttribute("clubes", clubRepository.findAll());
        return "scouting/admin/editar-jugador";
    }

    @PostMapping("/jugadores/editar/{id}")
    public String guardarEdicion(
            @PathVariable Long id,
            @RequestParam String nombre,
            @RequestParam Integer edad,
            @RequestParam String nacionalidad,
            @RequestParam Position posicion,
            @RequestParam(required = false) Long clubId,
            @RequestParam Double valorMercado,
            @RequestParam Integer goles,
            @RequestParam Integer pasesExitosos,
            @RequestParam Integer minutos,
            @RequestParam Double rating,
            @RequestParam Double velocidadPunta,
            @RequestParam Double expectedGoals,
            @RequestParam Integer duelosDefensivos,
            @RequestParam Integer pasesUltimoTercio,
            @RequestParam Integer atajadas) {

        Jugador jugador = jugadorRepository.findById(id).orElse(null);
        if (jugador == null) {
            return "redirect:/scouting/admin/jugadores";
        }

        Club club = (clubId != null && clubId > 0) ? clubRepository.findById(clubId).orElse(null) : null;

        jugador.setNombre(nombre);
        jugador.setEdad(edad);
        jugador.setNacionalidad(nacionalidad);
        jugador.setPosicion(posicion);
        jugador.setClub(club);
        jugador.setValorMercado(valorMercado);
        jugadorRepository.save(jugador);

        List<Estadistica> statsList = estadisticaRepository.findByJugadorId(id);
        Estadistica stats = (statsList != null && !statsList.isEmpty()) ? statsList.get(0) : new Estadistica();
        stats.setGoles(goles);
        stats.setPasesExitosos(pasesExitosos);
        stats.setMinutos(minutos);
        stats.setRating(rating);
        stats.setJugador(jugador);
        estadisticaRepository.save(stats);

        EstadisticaDetallada detStats = detailedStatsRepository.findByJugadorId(id).orElse(new EstadisticaDetallada());
        detStats.setVelocidadPunta(velocidadPunta);
        detStats.setExpectedGoals(expectedGoals);
        detStats.setDuelosDefensivos(duelosDefensivos);
        detStats.setPasesUltimoTercio(pasesUltimoTercio);
        detStats.setAtajadas(atajadas);
        detStats.setJugador(jugador);
        detailedStatsRepository.save(detStats);

        return "redirect:/scouting/admin/jugadores";
    }

    @PostMapping("/jugadores/eliminar/{id}")
    public String eliminarJugador(@PathVariable Long id) {
        jugadorRepository.deleteById(id);
        return "redirect:/scouting/admin/jugadores";
    }
}
