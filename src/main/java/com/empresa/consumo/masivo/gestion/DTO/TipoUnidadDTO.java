package com.empresa.consumo.masivo.gestion.DTO;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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
	

	public Long getTipoUnidadId() {
		return tipoUnidadId;
	}

	public void setTipoUnidadId(Long tipoUnidadId) {
		this.tipoUnidadId = tipoUnidadId;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getUnidad() {
		return unidad;
	}

	public void setUnidad(String unidad) {
		this.unidad = unidad;
	}

	public Double getReferenciaEnGramos() {
		return referenciaEnGramos;
	}

	public void setReferenciaEnGramos(Double referenciaEnGramos) {
		this.referenciaEnGramos = referenciaEnGramos;
	}

}
