package com.empresa.consumo.masivo.gestion.DTO;
// Generated 28/12/2018 07:26:27 PM by Hibernate Tools 5.3.6.Final

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Material generated by hbm2java
 */
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



	public Long getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Long materialId) {
		this.materialId = materialId;
	}

	public TipoMaterialDTO getTipoMaterial() {
		return tipoMaterial;
	}

	public void setTipoMaterial(TipoMaterialDTO tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}

	public TipoUnidadDTO getTipoUnidad() {
		return tipoUnidad;
	}

	public void setTipoUnidad(TipoUnidadDTO tipoUnidad) {
		this.tipoUnidad = tipoUnidad;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public Double getCantidadCompra() {
		return cantidadCompra;
	}

	public void setCantidadCompra(Double cantidadCompra) {
		this.cantidadCompra = cantidadCompra;
	}

}
