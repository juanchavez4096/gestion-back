package com.empresa.consumo.masivo.gestion.security;

import org.springframework.stereotype.Service;

import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;

import java.util.Optional;

@Service
public interface JwtService {
    String toToken(UsuarioDTO usuarioDTO);

    Optional<String> getSubFromToken(String token);
}
