package com.empresa.consumo.masivo.gestion.DTO;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserWithToken implements Serializable {
    private String email;
    private String nombre;
    private String token;
    public UserWithToken() {
		super();
	}

	public UserWithToken(UsuarioDTO usuarioDTO, String token) {
        this.email = usuarioDTO.getEmail();
        this.nombre = usuarioDTO.getNombre();
        this.token = token;
    }
}
