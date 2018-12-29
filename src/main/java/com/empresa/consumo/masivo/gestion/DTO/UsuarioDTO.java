package com.empresa.consumo.masivo.gestion.DTO;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

public class UsuarioDTO implements Serializable {

	
	@Null
	private int usuarioId;
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

	public int getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Long getEmpresaId() {
		return empresaId;
	}

	public void setEmpresaId(Long empresaId) {
		this.empresaId = empresaId;
	}

	public TipoUsuarioDTO getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuarioDTO tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
