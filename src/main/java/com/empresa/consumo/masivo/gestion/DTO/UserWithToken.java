package com.empresa.consumo.masivo.gestion.DTO;

import java.io.Serializable;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	

}
