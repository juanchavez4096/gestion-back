package com.empresa.consumo.masivo.gestion.DTO;
// Generated 28/12/2018 07:26:27 PM by Hibernate Tools 5.3.6.Final

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class MaterialDTO implements java.io.Serializable {

	private Long materialId;
	@NotNull
	private TipoMaterialDTO tipoMaterial;
	@NotNull
	private TipoUnidadDTO tipoUnidad;
	@NotNull
	@NotEmpty
	private String nombre;
	@NotNull
	private Double costo;
	@NotNull
	private Double cantidadCompra;

	public MaterialDTO() {
	}
	
	

	public MaterialDTO(Long materialId) {
		super();
		this.materialId = materialId;
	}

}
