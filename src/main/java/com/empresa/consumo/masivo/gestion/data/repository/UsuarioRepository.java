package com.empresa.consumo.masivo.gestion.data.repository;

import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {

	Usuario findByEmailAndEnabledAndEmpresa_Enabled(String email, Boolean enabled, Boolean empresaEnabled);
	Boolean existsByEmail(String email);
	Optional<Usuario> findByEmail(String email);
}
