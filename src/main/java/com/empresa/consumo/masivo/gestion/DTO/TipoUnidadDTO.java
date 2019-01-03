package com.empresa.consumo.masivo.gestion.DTO;

public class TipoUnidadDTO implements java.io.Serializable {

	
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



	public TipoUnidadDTO(Long tipoUnidadId, String tipo, String unidad, double referenciaEnGramos) {
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

	public double getReferenciaEnGramos() {
		return referenciaEnGramos;
	}

	public void setReferenciaEnGramos(double referenciaEnGramos) {
		this.referenciaEnGramos = referenciaEnGramos;
	}

}
