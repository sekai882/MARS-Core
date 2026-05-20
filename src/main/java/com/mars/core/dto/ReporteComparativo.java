package com.mars.core.dto;

import com.mars.core.model.Jugador;
import com.mars.core.model.Estadistica;
import com.mars.core.model.EstadisticaDetallada;

public class ReporteComparativo {
    private Jugador jugador1;
    private Jugador jugador2;
    private Estadistica stats1;
    private Estadistica stats2;
    private EstadisticaDetallada detailedStats1;
    private EstadisticaDetallada detailedStats2;

    // Comparaciones individuales
    private String masGoles;
    private String masVelocidad;
    private String masPases;
    private String mejorRating;
    private String masDuelos;
    private String mejorxG;

    public ReporteComparativo(Jugador jugador1, Jugador jugador2, 
                              Estadistica stats1, Estadistica stats2,
                              EstadisticaDetallada detailedStats1, EstadisticaDetallada detailedStats2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.stats1 = stats1;
        this.stats2 = stats2;
        this.detailedStats1 = detailedStats1;
        this.detailedStats2 = detailedStats2;
        calcularDiferencias();
    }

    private void calcularDiferencias() {
        int goles1 = stats1 != null ? stats1.getGoles() : 0;
        int goles2 = stats2 != null ? stats2.getGoles() : 0;
        if (goles1 > goles2) {
            this.masGoles = jugador1.getNombre() + " (" + goles1 + " vs " + goles2 + ")";
        } else if (goles2 > goles1) {
            this.masGoles = jugador2.getNombre() + " (" + goles2 + " vs " + goles1 + ")";
        } else {
            this.masGoles = "Empate (" + goles1 + ")";
        }

        double vel1 = detailedStats1 != null && detailedStats1.getVelocidadPunta() != null ? detailedStats1.getVelocidadPunta() : 0.0;
        double vel2 = detailedStats2 != null && detailedStats2.getVelocidadPunta() != null ? detailedStats2.getVelocidadPunta() : 0.0;
        if (vel1 > vel2) {
            this.masVelocidad = jugador1.getNombre() + " (" + vel1 + " km/h vs " + vel2 + " km/h)";
        } else if (vel2 > vel1) {
            this.masVelocidad = jugador2.getNombre() + " (" + vel2 + " km/h vs " + vel1 + " km/h)";
        } else {
            this.masVelocidad = "Empate (" + vel1 + " km/h)";
        }

        int pases1 = detailedStats1 != null && detailedStats1.getPasesUltimoTercio() != null ? detailedStats1.getPasesUltimoTercio() : 0;
        int pases2 = detailedStats2 != null && detailedStats2.getPasesUltimoTercio() != null ? detailedStats2.getPasesUltimoTercio() : 0;
        if (pases1 > pases2) {
            this.masPases = jugador1.getNombre() + " (" + pases1 + " vs " + pases2 + ")";
        } else if (pases2 > pases1) {
            this.masPases = jugador2.getNombre() + " (" + pases2 + " vs " + pases1 + ")";
        } else {
            this.masPases = "Empate (" + pases1 + ")";
        }

        double rat1 = stats1 != null && stats1.getRating() != null ? stats1.getRating() : 0.0;
        double rat2 = stats2 != null && stats2.getRating() != null ? stats2.getRating() : 0.0;
        if (rat1 > rat2) {
            this.mejorRating = jugador1.getNombre() + " (" + rat1 + " vs " + rat2 + ")";
        } else if (rat2 > rat1) {
            this.mejorRating = jugador2.getNombre() + " (" + rat2 + " vs " + rat1 + ")";
        } else {
            this.mejorRating = "Empate (" + rat1 + ")";
        }

        int duelos1 = detailedStats1 != null && detailedStats1.getDuelosDefensivos() != null ? detailedStats1.getDuelosDefensivos() : 0;
        int duelos2 = detailedStats2 != null && detailedStats2.getDuelosDefensivos() != null ? detailedStats2.getDuelosDefensivos() : 0;
        if (duelos1 > duelos2) {
            this.masDuelos = jugador1.getNombre() + " (" + duelos1 + " vs " + duelos2 + ")";
        } else if (duelos2 > duelos1) {
            this.masDuelos = jugador2.getNombre() + " (" + duelos2 + " vs " + duelos1 + ")";
        } else {
            this.masDuelos = "Empate (" + duelos1 + ")";
        }

        double xG1 = detailedStats1 != null && detailedStats1.getExpectedGoals() != null ? detailedStats1.getExpectedGoals() : 0.0;
        double xG2 = detailedStats2 != null && detailedStats2.getExpectedGoals() != null ? detailedStats2.getExpectedGoals() : 0.0;
        if (xG1 > xG2) {
            this.mejorxG = jugador1.getNombre() + " (" + xG1 + " vs " + xG2 + ")";
        } else if (xG2 > xG1) {
            this.mejorxG = jugador2.getNombre() + " (" + xG2 + " vs " + xG1 + ")";
        } else {
            this.mejorxG = "Empate (" + xG1 + ")";
        }
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public Estadistica getStats1() {
        return stats1;
    }

    public Estadistica getStats2() {
        return stats2;
    }

    public EstadisticaDetallada getDetailedStats1() {
        return detailedStats1;
    }

    public EstadisticaDetallada getDetailedStats2() {
        return detailedStats2;
    }

    public String getMasGoles() {
        return masGoles;
    }

    public String getMasVelocidad() {
        return masVelocidad;
    }

    public String getMasPases() {
        return masPases;
    }

    public String getMejorRating() {
        return mejorRating;
    }

    public String getMasDuelos() {
        return masDuelos;
    }

    public String getMejorxG() {
        return mejorxG;
    }
}
