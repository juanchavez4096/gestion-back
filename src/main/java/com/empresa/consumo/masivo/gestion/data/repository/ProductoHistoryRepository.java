package com.empresa.consumo.masivo.gestion.data.repository;

import com.empresa.consumo.masivo.gestion.data.entity.ProductoHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.empresa.consumo.masivo.gestion.data.entity.Empresa;

import java.util.List;

public interface ProductoHistoryRepository extends CrudRepository<ProductoHistory, Long> {
    List<ProductoHistory> findFirst10ByProducto_ProductoIdOrderByFechaCreacionDesc(Long productoId);

}
