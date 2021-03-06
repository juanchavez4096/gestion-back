package com.empresa.consumo.masivo.gestion.DTO;
// Generated 28/12/2018 07:26:27 PM by Hibernate Tools 5.3.6.Final

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Getter
@Setter
public class MaterialDTO implements java.io.Serializable {

	private Long materialId;
	@NotNull
	private TipoUnidadDTO tipoUnidad;
	@NotNull
	@NotEmpty
	private String nombre;
	@NotNull
	private Double costo;
	@NotNull
	private Double cantidadCompra;
	private LocalDateTime fechaCreacion;
	private UsuarioDTO creadoPor;
	private LocalDateTime fechaActualizacion;
	private UsuarioDTO actualizadoPor;

	public MaterialDTO() {
	}
	
	

	public MaterialDTO(Long materialId) {
		super();
		this.materialId = materialId;
	}

}
