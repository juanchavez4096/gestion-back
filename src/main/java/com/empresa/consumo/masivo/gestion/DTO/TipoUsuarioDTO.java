package com.empresa.consumo.masivo.gestion.DTO;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TipoUsuarioDTO implements Serializable {

	private int tipoUsuarioId;
	private String tipo;
	
	
	public TipoUsuarioDTO() {
		super();
	}

	public TipoUsuarioDTO(int tipoUsuarioId) {
		super();
		this.tipoUsuarioId = tipoUsuarioId;
	}
}
