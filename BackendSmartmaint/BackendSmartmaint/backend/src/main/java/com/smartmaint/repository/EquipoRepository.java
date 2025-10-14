package com.smartmaint.repository;

import com.smartmaint.model.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {
    List<Equipo> findByEmpresaId(Integer empresaId);
}
