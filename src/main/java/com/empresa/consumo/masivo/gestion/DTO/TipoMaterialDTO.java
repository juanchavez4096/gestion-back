package com.empresa.consumo.masivo.gestion.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TipoMaterialDTO implements java.io.Serializable {
	
	private Long tipoMaterialId;
	private String tipo;

	public TipoMaterialDTO() {
	}

	public TipoMaterialDTO(Long tipoMaterialId) {
		super();
		this.tipoMaterialId = tipoMaterialId;
	}

	
}
