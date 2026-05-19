package com.mars.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mars.core.model.Jugador;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {
}
