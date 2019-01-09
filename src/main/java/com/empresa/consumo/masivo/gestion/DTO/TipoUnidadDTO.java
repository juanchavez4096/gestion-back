package com.empresa.consumo.masivo.gestion.DTO;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoUnidadDTO implements java.io.Serializable {

	@NotNull
	@Min(value = 1)
	private Long tipoUnidadId;
	private String tipo;
	private String unidad;
	private Double referenciaEnGramos;

	public TipoUnidadDTO() {
	}
	
	

	public TipoUnidadDTO(Long tipoUnidadId) {
		super();
		this.tipoUnidadId = tipoUnidadId;
	}



	public TipoUnidadDTO(Long tipoUnidadId, String tipo, String unidad, Double referenciaEnGramos) {
		this.tipoUnidadId = tipoUnidadId;
		this.tipo = tipo;
		this.unidad = unidad;
		this.referenciaEnGramos = referenciaEnGramos;
	}


}
