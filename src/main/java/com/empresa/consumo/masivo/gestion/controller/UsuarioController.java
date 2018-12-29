package com.empresa.consumo.masivo.gestion.controller;

import org.springframework.web.bind.annotation.RestController;

import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.UsuarioMapper;
import com.empresa.consumo.masivo.gestion.convertor.UserConvertorImpl;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import com.empresa.consumo.masivo.gestion.data.repository.UsuarioRepository;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;




@RestController
@RequestMapping("api/usuario")
public class UsuarioController {
	
	Logger log = LogManager.getLogger();
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@GetMapping(value="get")
	public String getMethodName(Principal principal) {
		return principal.getName();
	}
	
	@RequestMapping(value="login", method=RequestMethod.GET)
	public ResponseEntity<UsuarioDTO> getUsuarioById(@RequestParam(name = "email") String email, 
			@RequestParam(name = "password") String password) {
		UsuarioDTO usuarioDTO =  UsuarioMapper.INSTANCE.usuarioToUsuarioDTO(usuarioRepository.findByEmailAndPassword(email, password));
		if (usuarioDTO == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}else {
			return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
		}
		
	}
	
	@RequestMapping(value="register", method=RequestMethod.POST, consumes="application/json")
	public ResponseEntity<Object> register(@RequestBody UsuarioDTO usuarioDTO) {
		
		
		log.info("klk elmio");
		return new ResponseEntity<>("Esteve", HttpStatus.CREATED);
	}
	
	
	
	/*@RequestMapping(value="login", method=RequestMethod.GET)
	public SomeData requestMethodName(@RequestParam String param) {
		return new SomeData();
	}*/
	
	
}
