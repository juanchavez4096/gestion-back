package com.empresa.consumo.masivo.gestion.data.repository;

import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

	Usuario findByEmailAndEnabledAndEmpresa_Enabled(String email, Boolean enabled, Boolean empresaEnabled);
	Boolean existsByEmail(String email);
	Optional<Usuario> findByEmail(String email);
	Page<Usuario> findByEmpresa_EmpresaIdAndNombreContainingIgnoreCaseOrderByNombre(Long empresaId, String nombre, Pageable pageable);
	List<Usuario> findByEmpresa_EmpresaIdAndFechaCreacionBetweenIgnoreCaseOrderByNombre(Long empresaId, LocalDateTime desde, LocalDateTime hasta);
	Usuario findByEmpresa_EmpresaIdAndUsuarioId(Long empresaId, Long usuarioId);
}
