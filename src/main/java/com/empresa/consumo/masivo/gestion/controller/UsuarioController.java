package com.empresa.consumo.masivo.gestion.controller;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.consumo.masivo.gestion.DTO.LoginDTO;
import com.empresa.consumo.masivo.gestion.DTO.RegisterDTO;
import com.empresa.consumo.masivo.gestion.DTO.UserWithToken;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.UsuarioMapper;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.TipoUsuario;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import com.empresa.consumo.masivo.gestion.data.repository.UsuarioRepository;
import com.empresa.consumo.masivo.gestion.security.EncryptService;
import com.empresa.consumo.masivo.gestion.security.JwtService;




@RestController
@RequestMapping("api/usuario")
public class UsuarioController {
	
	Logger log = LogManager.getLogger();
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private EncryptService encryptService;
	@Autowired
    private JwtService jwtService;
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	public ResponseEntity<UserWithToken> getUsuarioById(@Valid @RequestBody LoginDTO loginDTO) {
		UsuarioDTO usuarioDTO =  UsuarioMapper.INSTANCE
									.usuarioToUsuarioDTO(usuarioRepository.findByEmail(loginDTO.getEmail()));
		if (usuarioDTO == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}else {
			if (encryptService.check(loginDTO.getPassword(), usuarioDTO.getPassword())) {
				return new ResponseEntity<>(new UserWithToken(usuarioDTO, jwtService.toToken(usuarioDTO)), HttpStatus.OK);
			}else {
				return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
			}
			
		}
		
	}
	
	@RequestMapping(value="register", method=RequestMethod.POST)
	public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO, @AuthenticationPrincipal UsuarioDTO usuarioDTO) {
		
		if (usuarioRepository.existsByEmail(registerDTO.getEmail())) {
			return new ResponseEntity<>(HttpStatus.FOUND); 
		}
		
		Usuario newUsuario = UsuarioMapper.INSTANCE.registerDTOToUsuario(registerDTO);
		newUsuario.setNombre(newUsuario.getNombre().trim());
		newUsuario.setEmpresa(new Empresa(usuarioDTO.getEmpresaId()));
		newUsuario.setTipoUsuario(new TipoUsuario(usuarioDTO.getTipoUsuario().getTipoUsuarioId()));
		newUsuario.setPassword(encryptService.encrypt(newUsuario.getPassword()));
		usuarioRepository.save(newUsuario);
		
		UsuarioDTO newUsuarioDTO = UsuarioMapper.INSTANCE.usuarioToUsuarioDTO(newUsuario);
		return new ResponseEntity<>(new UserWithToken(newUsuarioDTO, jwtService.toToken(newUsuarioDTO)) , HttpStatus.CREATED);
	}
	
	/*@RequestMapping(value="forgotPassword", method=RequestMethod.POST)
	public ResponseEntity<UserWithToken> getUsuarioById(@Valid @RequestBody LoginDTO loginDTO) {
		UsuarioDTO usuarioDTO =  UsuarioMapper.INSTANCE
									.usuarioToUsuarioDTO(usuarioRepository.findByEmail(loginDTO.getEmail()));
		if (usuarioDTO == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}else {
			if (encryptService.check(loginDTO.getPassword(), usuarioDTO.getPassword())) {
				return new ResponseEntity<>(new UserWithToken(usuarioDTO, jwtService.toToken(usuarioDTO)), HttpStatus.OK);
			}else {
				return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
			}
			
		}
		
	}*/
	
	
}
