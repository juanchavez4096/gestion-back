package com.empresa.consumo.masivo.gestion.DTO;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
	
}
