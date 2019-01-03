package com.empresa.consumo.masivo.gestion.DTO;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ProductoDTO implements Serializable {

	@NotNull
	@Min(value = 1)
	private Long productoId;
	@NotNull
	@NotEmpty
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


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	
	
	
}
