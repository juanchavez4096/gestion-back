package com.empresa.consumo.masivo.gestion.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import com.empresa.consumo.masivo.gestion.data.entity.ProductoMaterial;

public interface ProductoMaterialRepository extends CrudRepository<ProductoMaterial, Long> {

	Page<ProductoMaterial> findByProducto_Empresa_EmpresaIdAndProducto_ProductoIdAndProducto_ActivoAndMaterial_ActivoAndMaterial_NombreContainingIgnoreCaseOrderByMaterial_Nombre(Long empresaId, Long productoId, Boolean productoActivo, Boolean materialActivo, String search,Pageable pageable);
	Page<ProductoMaterial> findByProducto_Empresa_EmpresaIdAndProductoMaterialIdAndProducto_ActivoAndMaterial_ActivoOrderByMaterial_Nombre(Long empresaId, Long productoMaterialId, Boolean productoActivo, Boolean materialActivo, Pageable pageable);

	List<ProductoMaterial> findByProducto_Empresa_EmpresaIdAndProducto_ProductoIdAndProducto_ActivoAndMaterial_Activo(Long empresaId, Long productoId, Boolean productoActivo, Boolean materialActivo);
}
