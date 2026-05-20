package com.mars.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.mars.core.model.Estadistica;

@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {
    List<Estadistica> findByJugadorId(Long jugadorId);
}
