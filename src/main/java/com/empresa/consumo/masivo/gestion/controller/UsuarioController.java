package com.empresa.consumo.masivo.gestion.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.empresa.consumo.masivo.gestion.DTO.LoginDTO;
import com.empresa.consumo.masivo.gestion.DTO.RegisterDTO;
import com.empresa.consumo.masivo.gestion.DTO.UserWithToken;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.UsuarioMapper;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.TipoUsuario;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import com.empresa.consumo.masivo.gestion.data.repository.UsuarioRepository;
import com.empresa.consumo.masivo.gestion.exception.ImageNotFoundException;
import com.empresa.consumo.masivo.gestion.exception.InvalidFileException;
import com.empresa.consumo.masivo.gestion.security.EncryptService;
import com.empresa.consumo.masivo.gestion.security.JwtService;
import com.empresa.consumo.masivo.gestion.service.UploadService;


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
	@Autowired
	private UploadService uploadService;
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	public ResponseEntity<UserWithToken> getUsuarioById(@Valid @RequestBody LoginDTO loginDTO) {
		Usuario usuario = usuarioRepository.findByEmailAndEnabled(loginDTO.getEmail(), Boolean.TRUE);

		if (!usuario.getEmpresa().getEnabled()){
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}

		UsuarioDTO usuarioDTO = UsuarioMapper.INSTANCE
									.usuarioToUsuarioDTO(usuario);
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
	
	

	@RequestMapping(path = "file/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadAttachment(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UsuarioDTO usuarioDTO)
			throws IllegalStateException, IOException {
		
		
		if (file.isEmpty())
			return new ResponseEntity<>("The File cannot be empty", HttpStatus.NOT_ACCEPTABLE);

		if (!file.getContentType().equals(MimeTypeUtils.IMAGE_JPEG_VALUE)
				&& !file.getContentType().equals(MimeTypeUtils.IMAGE_PNG_VALUE))
			return new ResponseEntity<>("Invalid File Content Type", HttpStatus.NOT_ACCEPTABLE);

		if (file.getSize() > 2000000)
			return new ResponseEntity<>("Invalid File Content size: greater than 2MB", HttpStatus.NOT_ACCEPTABLE);

		if (file.getName().length() > 100)
			return new ResponseEntity<>("Invalid File Content name: name length greater than 100",
					HttpStatus.NOT_ACCEPTABLE);

		
		String fileName = uploadService.uploadUserImage(file, usuarioDTO.getUsuarioId());

		log.info("Image uploaded with userId " + usuarioDTO.getUsuarioId());

		return new ResponseEntity<>(fileName, HttpStatus.OK);
	}

	@RequestMapping(path = "file/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadAttachment(@RequestParam("token") String token, @RequestParam("size") String size, HttpServletRequest request,
			HttpServletResponse response) throws IllegalStateException, IOException,
			InvalidFileException, ImageNotFoundException {

		Optional<String> userId = jwtService.getSubFromToken(token);
		if (!userId.isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		if(!usuarioRepository.findById(Integer.parseInt(userId.get())).isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (size == null || size.isEmpty())
			throw new InvalidFileException("The size cannot be empty: " + size);

		if (!size.equals("50x50") && !size.equals("128x128") && !size.equals("500x500") )
			throw new InvalidFileException("The file size is invalid: " + size);

		log.info("Donwload in controller");
		if (size.equals("50x50")) {
			return uploadService.downloadUserImageShort(Long.parseLong(userId.get()), request, response);
		} else if (size.equals("500x500")) {
			return uploadService.downloadUserImageBig(Long.parseLong(userId.get()), request, response);
		} else {
			return uploadService.downloadUserImageMed(Long.parseLong(userId.get()), request, response);
		}

	}

	@RequestMapping(path = "file/delete", method = RequestMethod.DELETE)
	public ResponseEntity<Long> deleteFile(@AuthenticationPrincipal UsuarioDTO usuarioDTO) throws IllegalStateException {

		Long result = uploadService.deleteUserImage(usuarioDTO.getUsuarioId() );
		log.info("Image Deleted by userId: " + usuarioDTO.getUsuarioId());

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	
}
