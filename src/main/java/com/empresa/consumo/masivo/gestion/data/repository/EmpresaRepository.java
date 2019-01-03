package com.empresa.consumo.masivo.gestion.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.empresa.consumo.masivo.gestion.data.entity.Empresa;

public interface EmpresaRepository extends CrudRepository<Empresa, Long> {

	Page<Empresa> findBy(Pageable pageable);
}
