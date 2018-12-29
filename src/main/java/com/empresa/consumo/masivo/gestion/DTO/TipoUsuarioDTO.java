package com.empresa.consumo.masivo.gestion.DTO;

import java.io.Serializable;

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




	public int getTipoUsuarioId() {
		return tipoUsuarioId;
	}


	public void setTipoUsuarioId(int tipoUsuarioId) {
		this.tipoUsuarioId = tipoUsuarioId;
	}


	public String getTipo() {
		return tipo;
	}


	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	
}
