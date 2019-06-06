package com.empresa.consumo.masivo.gestion.data.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.empresa.consumo.masivo.gestion.data.entity.TipoUnidad;

public interface TipoUnidadRepository extends CrudRepository<TipoUnidad, Long> {
	
	List<TipoUnidad> findByTipoUnidadIdIn(Collection<Long> tipoUnidadIds);
	List<TipoUnidad> findByOrderByTipo();
	List<TipoUnidad> findByAgrupacionOrderByTipo(Long agrupacion);
}
