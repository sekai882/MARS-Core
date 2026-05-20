package com.mars.core.repository;

import com.mars.core.model.EstadisticaDetallada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadisticaDetalladaRepository extends JpaRepository<EstadisticaDetallada, Long> {
    Optional<EstadisticaDetallada> findByJugadorId(Long jugadorId);
}
