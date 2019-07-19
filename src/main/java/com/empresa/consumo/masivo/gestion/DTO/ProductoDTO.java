package com.empresa.consumo.masivo.gestion.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductoDTO implements Serializable {

	@NotNull
	@Min(value = 1)
	private Long productoId;
	@NotNull
	@NotEmpty
	private String nombre;
	private String costoProduccion;
	private String precioVenta;
	private String ganancia;
	private Double depreciacion;
	private String precioVentaDolares;
	private LocalDateTime fechaCreacion;
	private UsuarioDTO creadoPor;
	private LocalDateTime fechaActualizacion;
	private UsuarioDTO actualizadoPor;

	public ProductoDTO() {
		super();
	}
	
}
