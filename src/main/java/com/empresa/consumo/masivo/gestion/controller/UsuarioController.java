package com.empresa.consumo.masivo.gestion.controller;

import com.empresa.consumo.masivo.gestion.DTO.*;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;

import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.TipoUsuario;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import com.empresa.consumo.masivo.gestion.data.repository.UsuarioRepository;
import com.empresa.consumo.masivo.gestion.exception.BusinessServiceException;
import com.empresa.consumo.masivo.gestion.exception.ImageNotFoundException;
import com.empresa.consumo.masivo.gestion.exception.InvalidFileException;
import com.empresa.consumo.masivo.gestion.exception.UsuarioException;
import com.empresa.consumo.masivo.gestion.security.EncryptService;
import com.empresa.consumo.masivo.gestion.security.JwtService;
import com.empresa.consumo.masivo.gestion.service.UploadService;
import com.empresa.consumo.masivo.gestion.service.UsuarioService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;


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
	@Autowired
	private UsuarioService usuarioService;
	
	@RequestMapping(value="login", method=RequestMethod.POST)
	public ResponseEntity<UserWithToken> getUsuarioById(@Valid @RequestBody LoginDTO loginDTO) {
		Usuario usuario = usuarioRepository.findByEmailAndEnabledAndEmpresa_Enabled(loginDTO.getEmail(), Boolean.TRUE, Boolean.TRUE);

		UsuarioDTO usuarioDTO = ProductoMapper.INSTANCE
									.usuarioToUsuarioDTO(usuario);
		if (usuarioDTO == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}else {
			if (encryptService.check(loginDTO.getPassword(), usuarioDTO.getPassword())) {
				return new ResponseEntity<>(new UserWithToken(usuarioDTO, jwtService.toToken(usuarioDTO)), HttpStatus.OK);
			}else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			
		}
		
	}

	@RequestMapping(value="currentUser", method=RequestMethod.GET)
	public ResponseEntity<UsuarioDTO> currentUser(@AuthenticationPrincipal UsuarioDTO usuarioDTO) {

		usuarioDTO.setPassword(null);
		usuarioDTO.setEmpresaId(null);
		usuarioDTO.setUsuarioId(null);
		return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);
	}

	@RequestMapping(value="register", method=RequestMethod.POST)
	public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO, @AuthenticationPrincipal UsuarioDTO usuarioDTO) {
		
		if (usuarioRepository.existsByEmail(registerDTO.getEmail())) {
			return new ResponseEntity<>(HttpStatus.FOUND); 
		}
		
		Usuario newUsuario = ProductoMapper.INSTANCE.registerDTOToUsuario(registerDTO);
		newUsuario.setNombre(newUsuario.getNombre().trim());
		newUsuario.setEmpresa(new Empresa(usuarioDTO.getEmpresaId()));
		newUsuario.setTipoUsuario(new TipoUsuario(usuarioDTO.getTipoUsuario().getTipoUsuarioId()));
		newUsuario.setPassword(encryptService.encrypt(newUsuario.getPassword()));
		newUsuario.setEnabled(Boolean.TRUE);
		usuarioRepository.save(newUsuario);
		
		UsuarioDTO newUsuarioDTO = ProductoMapper.INSTANCE.usuarioToUsuarioDTO(newUsuario);
		return new ResponseEntity<>(new UserWithToken(newUsuarioDTO, jwtService.toToken(newUsuarioDTO)) , HttpStatus.CREATED);
	}

	@RequestMapping(value="changePassword", method=RequestMethod.POST)
	public ResponseEntity<Void> changePassword(@NotEmpty @RequestParam(value = "currentPassword") String currentPassword, @NotEmpty @RequestParam(value = "newPassword") String newPassword, @AuthenticationPrincipal UsuarioDTO usuarioDTO) {

		if (encryptService.check(currentPassword, usuarioDTO.getPassword())){
			usuarioDTO.setPassword(encryptService.encrypt(newPassword));
			Usuario usuario = ProductoMapper.INSTANCE.usuarioDTOToUsuario(usuarioDTO);
			usuario.setEnabled(Boolean.TRUE);
			usuarioRepository.save(usuario);
			return new ResponseEntity<>(HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}

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
	public ResponseEntity<byte[]> downloadAttachment(@RequestParam("token") String token, @RequestParam("usuarioId") Long usuarioId, @RequestParam("size") String size, HttpServletRequest request,
			HttpServletResponse response) throws IllegalStateException, IOException,
			InvalidFileException, ImageNotFoundException {

		Optional<String> userId = jwtService.getSubFromToken(token);
		if (!userId.isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		if(!usuarioRepository.findById(Long.parseLong(userId.get())).isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (size == null || size.isEmpty())
			throw new InvalidFileException("The size cannot be empty: " + size);

		if (!size.equals("50x50") && !size.equals("128x128") && !size.equals("500x500") )
			throw new InvalidFileException("The file size is invalid: " + size);

		log.info("Donwload in controller");
		if (size.equals("50x50")) {
			return uploadService.downloadUserImageShort(usuarioId, request, response);
		} else if (size.equals("500x500")) {
			return uploadService.downloadUserImageBig(usuarioId, request, response);
		} else {
			return uploadService.downloadUserImageMed(usuarioId, request, response);
		}

	}

	@RequestMapping(path = "file/delete", method = RequestMethod.DELETE)
	public ResponseEntity<Long> deleteFile(@AuthenticationPrincipal UsuarioDTO usuarioDTO) throws IllegalStateException {

		Long result = uploadService.deleteUserImage(usuarioDTO.getUsuarioId() );
		log.info("Image Deleted by userId: " + usuarioDTO.getUsuarioId());

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@RequestMapping(path = "forgotPassword", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Boolean> forgotPassword(@RequestParam(value = "email") String email) throws UsuarioException, IOException {

		boolean success = false;
		success = usuarioService.generatePasswordCodeBase64(email);
		return new ResponseEntity<>(success, HttpStatus.OK);

	}

	@RequestMapping(path = "changeForgottenPassword", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Boolean> changeForgottenPassword(@RequestParam(value = "email") String email,
														@RequestParam(value = "password") String password,
														@RequestParam(value = "codigoVerificacion") String codigoVerificion) throws UsuarioException {

		boolean success = false;
		success = usuarioService.changeForgottenPassword(email, password, codigoVerificion);
		return new ResponseEntity<>(success, HttpStatus.OK);
	}

	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value = "add", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addUsuario(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "nombre") String nombre, @RequestParam(value = "email") String email,
										@RequestParam(value = "tipoUsuario") Long tipoUsuario,
										@RequestParam(value = "imagen", required = false) MultipartFile file) throws IOException, BusinessServiceException {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		if (nombre == null || nombre.isEmpty()){
			throw new BusinessServiceException();
		}

		if (file != null) {
			ResponseEntity<String> responseEntity = this.validateFile(file);
			if (responseEntity != null ) {
				return responseEntity;
			}
		}
		if (usuarioRepository.existsByEmail(email)){
			throw new UsuarioException("La dirección de email ya se encuentra utilizada por un usuario.");
		}

		UsuarioDTO savedUser = ProductoMapper.INSTANCE.usuarioToUsuarioDTO(usuarioRepository.save(new Usuario(new Empresa(usuarioDTO.getEmpresaId()), new TipoUsuario(tipoUsuario.intValue()), nombre, email, encryptService.encrypt("password"), Boolean.TRUE, LocalDateTime.now(ZoneId.systemDefault()) ))) ;
		if (file != null) {
			String fileName = uploadService.uploadUserImage(file, savedUser.getUsuarioId());
		}

		return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
	}

	private ResponseEntity<String> validateFile(MultipartFile file) {
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
		return null;
	}

	@RequestMapping(value="all", method = RequestMethod.GET)
	public ResponseEntity<Page<UsuarioDTO>> getAllUsers(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "search", required = false, defaultValue = "") String search, Pageable pageable) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Page<UsuarioDTO> pageUsuarios = null;

		pageUsuarios = usuarioRepository.findByEmpresa_EmpresaIdAndNombreContainingIgnoreCaseOrderByNombre(usuarioDTO.getEmpresaId(), search, pageable)
				.map(ProductoMapper.INSTANCE::usuarioToUsuarioDTOForDisplay);

		return new ResponseEntity<>(pageUsuarios, HttpStatus.OK);
	}

	@RequestMapping(value="byId", method = RequestMethod.GET)
	public ResponseEntity<UsuarioDTO> getUser(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "usuarioId") Long usuarioId) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		UsuarioDTO usuario = null;

		usuario = ProductoMapper.INSTANCE.usuarioToUsuarioDTOForDisplay(usuarioRepository.findByEmpresa_EmpresaIdAndUsuarioId(usuarioDTO.getEmpresaId(), usuarioId));
		return new ResponseEntity<>(usuario, HttpStatus.OK);
	}

	@RequestMapping(value="changeStatus", method = RequestMethod.GET)
	public ResponseEntity<Void> changeStatus(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "usuarioId") Long usuarioId) throws UsuarioException {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		if (usuarioId.longValue() == usuarioDTO.getUsuarioId().longValue()){
			throw new UsuarioException("No puede cambiar su propio estado.");
		}

		usuarioRepository.findById(usuarioId).ifPresent(u -> {
			u.setEnabled(!u.getEnabled());
			usuarioRepository.save(u);
		});

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value="updateUser", method = RequestMethod.GET)
	public ResponseEntity<Void> updateUser(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "nombre") String nombre, @RequestParam(value = "email") String email) throws UsuarioException {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}

		usuarioRepository.findById(usuarioDTO.getUsuarioId()).ifPresent(u -> {
			u.setNombre(nombre.trim());
			u.setEmail(email.trim());
			usuarioRepository.save(u);
		});

		return new ResponseEntity<>(HttpStatus.OK);
	}

}
