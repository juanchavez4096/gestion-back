package com.empresa.consumo.masivo.gestion.data.entity;
// Generated 28/12/2018 07:26:27 PM by Hibernate Tools 5.3.6.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Usuario generated by hbm2java
 */
@Entity
@Table(name = "usuario", schema = "gestion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements java.io.Serializable {

	@Id
	@SequenceGenerator(name="pk_sequence_usuario",sequenceName="gestion.usuario_id_sequence", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="pk_sequence_usuario")
	private Long usuarioId;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_usuario_id", nullable = false)
	private TipoUsuario tipoUsuario;
	@Column(name = "nombre", nullable = false)
	private String nombre;
	@Column(name = "email", nullable = false)
	private String email;
	@Column(name = "password", nullable = false)
	private String password;
	@Column(name = "enabled", nullable = false)
	private Boolean enabled;
	@Column(name = "codigo_recuperacion", nullable = false)
	private String codigoRecuperacion;
	@Column(name = "fecha_creacion", nullable = false)
	private LocalDateTime fechaCreacion;

	public Usuario(Long usuarioId){
		this.usuarioId = usuarioId;
	}

	public Usuario(Long usuarioId, Empresa empresa, TipoUsuario tipoUsuario, String nombre, String email,
			String password, LocalDateTime fechaCreacion) {
		this.usuarioId = usuarioId;
		this.empresa = empresa;
		this.tipoUsuario = tipoUsuario;
		this.nombre = nombre;
		this.email = email;
		this.password = password;
		this.fechaCreacion = fechaCreacion;
	}

	public Usuario(Empresa empresa, TipoUsuario tipoUsuario, String nombre, String email,
				   String password, Boolean enabled, LocalDateTime fechaCreacion) {
		this.empresa = empresa;
		this.tipoUsuario = tipoUsuario;
		this.nombre = nombre;
		this.email = email;
		this.password = password;
		this.enabled = enabled;
		this.fechaCreacion = fechaCreacion;
	}
}
