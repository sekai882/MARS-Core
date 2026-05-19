package com.mars.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mars.core.model.Estadistica;

@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Long> {
}
