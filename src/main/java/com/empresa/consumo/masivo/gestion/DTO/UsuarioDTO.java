package com.empresa.consumo.masivo.gestion.DTO;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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
	private Boolean enabled;
	private LocalDateTime fechaCreacion;

	
	
}
