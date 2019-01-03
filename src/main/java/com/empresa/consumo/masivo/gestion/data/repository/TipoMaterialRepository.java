package com.empresa.consumo.masivo.gestion.data.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.empresa.consumo.masivo.gestion.data.entity.TipoMaterial;

public interface TipoMaterialRepository extends CrudRepository<TipoMaterial, Long> {

	Boolean existsByTipoMaterialIdAndEmpresa_EmpresaId(Long tipoMaterialId, Long empresaId);
	
	List<TipoMaterial> findByTipoMaterialIdIn(Collection<Long> tipoMaterialIds);
}
