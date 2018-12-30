package com.empresa.consumo.masivo.gestion.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.empresa.consumo.masivo.gestion.data.entity.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {

	Usuario findByEmail(String email);
	Boolean existsByEmail(String email);
}
