package com.empresa.consumo.masivo.gestion.data.entity;
// Generated 28/12/2018 07:26:27 PM by Hibernate Tools 5.3.6.Final

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * TipoUsuario generated by hbm2java
 */
@Entity
@Table(name = "tipo_usuario", schema = "gestion")
public class TipoUsuario implements java.io.Serializable {

	@Id
	@GeneratedValue
	private int tipoUsuarioId;
	@Column(name = "tipo", nullable = false)
	private String tipo;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoUsuario")
	private Set<Usuario> usuarios = new HashSet<>(0);

	public TipoUsuario() {
	}
	
	

	public TipoUsuario(int tipoUsuarioId) {
		super();
		this.tipoUsuarioId = tipoUsuarioId;
	}



	public TipoUsuario(int tipoUsuarioId, String tipo) {
		this.tipoUsuarioId = tipoUsuarioId;
		this.tipo = tipo;
	}

	public TipoUsuario(int tipoUsuarioId, String tipo, Set<Usuario> usuarios) {
		this.tipoUsuarioId = tipoUsuarioId;
		this.tipo = tipo;
		this.usuarios = usuarios;
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

	public Set<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(Set<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	
}
