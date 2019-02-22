package com.empresa.consumo.masivo.gestion.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class ProductoDTO implements Serializable {

	@NotNull
	@Min(value = 1)
	private Long productoId;
	@NotNull
	@NotEmpty
	private String nombre;
	private Double costo;
	
	public ProductoDTO() {
		super();
	}
	
}
