package com.empresa.consumo.masivo.gestion.DTO;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AddProductoMaterialDTO implements java.io.Serializable {

	
	@NotNull
	@Min(value = 1)
	private Long materialId;
	@NotNull
	@Min(value = 1)
	private Long productoId;
	@NotNull
	@Valid
	private TipoUnidadDTO tipoUnidad;
	@NotNull
	@Min(value = 0)
	private Double cantidad;

	public AddProductoMaterialDTO() {
	}
	
}
