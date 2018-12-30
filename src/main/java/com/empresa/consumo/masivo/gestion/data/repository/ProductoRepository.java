package com.empresa.consumo.masivo.gestion.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.empresa.consumo.masivo.gestion.data.entity.Producto;

public interface ProductoRepository extends CrudRepository<Producto, Long> {

	Page<Producto> findByEmpresa_EmpresaId(Long empresaId,Pageable pageable);
}
