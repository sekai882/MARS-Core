package com.mars.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mars.core.model.Jugador;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Long> {

    @Query("SELECT DISTINCT j FROM Jugador j LEFT JOIN FETCH j.club LEFT JOIN FETCH j.estadisticaDetallada")
    List<Jugador> findAllEager();
}
