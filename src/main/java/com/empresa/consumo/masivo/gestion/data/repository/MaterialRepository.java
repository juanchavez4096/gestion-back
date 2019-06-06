package com.empresa.consumo.masivo.gestion.data.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.empresa.consumo.masivo.gestion.data.entity.Material;
import com.empresa.consumo.masivo.gestion.data.entity.ProductoMaterial;

public interface MaterialRepository extends CrudRepository<Material, Long> {

	Page<Material> findByEmpresa_EmpresaIdAndActivo(Long empresaId, Boolean activo,Pageable pageable);
	List<Material> findByEmpresa_EmpresaIdAndActivo(Long empresaId, Boolean activo);
	Page<Material> findByEmpresa_EmpresaIdAndActivoAndNombreContainingIgnoreCase(Long empresaId, Boolean activo, String search,Pageable pageable);
	Page<Material> findByMaterialIdAndEmpresa_EmpresaIdAndActivo(Long materialId,Long empresaId, Boolean activo, Pageable pageable);
	Boolean existsByMaterialIdAndEmpresa_EmpresaId(Long materialId, Long empresaId);
	List<Material> findByMaterialIdIn(Collection<Long> materialIds);
}
