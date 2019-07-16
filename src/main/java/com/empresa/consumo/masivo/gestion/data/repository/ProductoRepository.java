package com.empresa.consumo.masivo.gestion.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.empresa.consumo.masivo.gestion.data.entity.Producto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ProductoRepository extends CrudRepository<Producto, Long> {

	Page<Producto> findByEmpresa_EmpresaIdAndActivoOrderByNombre(Long empresaId, Boolean activo,Pageable pageable);
	Page<Producto> findByEmpresa_EmpresaIdAndActivoAndNombreContainingIgnoreCaseOrderByNombre(Long empresaId, Boolean activo, String search,Pageable pageable);

	List<Producto> findByProductoMaterials_Material_MaterialId(Long materialId);
	List<Producto> findByProductoId(Long productoId);

	Page<Producto> findByProductoIdAndEmpresa_EmpresaIdAndActivo(Long productoId, Long empresaId, Boolean activo,Pageable pageable);
	Boolean existsByProductoIdAndEmpresa_EmpresaId(Long productoId, Long empresaId);

	List<Producto> findByEmpresa_EmpresaIdAndActivoAndFechaCreacionBetweenOrderByNombre(Long empresaId, Boolean activo, LocalDateTime desde, LocalDateTime hasta);
	//Producto findByNombre(String nombre);
}
