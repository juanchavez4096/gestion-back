package com.empresa.consumo.masivo.gestion.DTO;

import java.io.Serializable;

public class ProductoDTO implements Serializable {

	private Long productoId;
	private Long empresaId;
	private String nombre;
	
	public ProductoDTO() {
		super();
	}


	public Long getProductoId() {
		return productoId;
	}


	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}


	public Long getEmpresaId() {
		return empresaId;
	}


	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
	
	
}
