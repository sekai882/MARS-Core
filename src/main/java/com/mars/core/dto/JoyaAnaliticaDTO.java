package com.mars.core.dto;

/**
 * DTO para la serialización segura de los datos analíticos de las Joyas Ocultas (Top Gems).
 * Previene la recursión infinita en la serialización JSON de relaciones JPA.
 */
public record JoyaAnaliticaDTO(
    Long id,
    String nombre,
    String posicion,
    String club,
    Double costo,
    Double iem,
    Integer busquedas
) {}
