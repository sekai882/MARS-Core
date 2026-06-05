package com.mars.core.repository;

import com.mars.core.model.Joya;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JoyaRepository extends JpaRepository<Joya, Long> {
    
    Optional<Joya> findByJugadorId(Long jugadorId);

    List<Joya> findTop5ByOrderByBusquedasDescMaxIemDesc();
}
