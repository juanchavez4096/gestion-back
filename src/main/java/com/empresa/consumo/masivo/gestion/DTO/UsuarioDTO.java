package com.empresa.consumo.masivo.gestion.DTO;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO implements Serializable {

	
	private Long usuarioId;
	@NotNull
	private Long empresaId;
	@NotNull
	private TipoUsuarioDTO tipoUsuario;
	@NotNull
	@NotEmpty
	private String nombre;
	@NotNull
	@NotEmpty
	private String email;
	@NotNull
	@NotEmpty
	private String password;
	
	
	public UsuarioDTO() {
		super();
	}
	
	
}
