package com.empresa.consumo.masivo.gestion.DTO;
// Generated 28/12/2018 07:26:27 PM by Hibernate Tools 5.3.6.Final

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoMaterialDTO implements java.io.Serializable {
	
	private Long productoMaterialId;
	private MaterialDTO material;
	private TipoUnidadDTO tipoUnidad;
	private Double cantidad;

	public ProductoMaterialDTO() {
	}

	
}
