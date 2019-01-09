package com.empresa.consumo.masivo.gestion.DTO;
// Generated 28/12/2018 07:26:27 PM by Hibernate Tools 5.3.6.Final

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * ProductoMaterial generated by hbm2java
 */
@Getter
@Setter
public class ModifyProductoMaterialDTO implements java.io.Serializable {

	@NotNull
	@Min(value = 1)
	private Long productoMaterialId;
	@NotNull
	private TipoUnidadDTO tipoUnidad;
	@NotNull
	@Min(value = 0)
	private Double cantidad;

	public ModifyProductoMaterialDTO() {
	}
	
}
