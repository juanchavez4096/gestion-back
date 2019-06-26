package com.empresa.consumo.masivo.gestion.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import com.empresa.consumo.masivo.gestion.DTO.*;
import com.empresa.consumo.masivo.gestion.constants.UsuarioTypesConstants;
import com.empresa.consumo.masivo.gestion.convertor.EmpresaMapper;
import com.empresa.consumo.masivo.gestion.data.repository.EmpresaRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("api/empresa")
public class EmpresaController {

	Logger log = LogManager.getLogger();

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private EmpresaRepository empresaRepository;
	@Autowired
	private EncryptService encryptService;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private UploadService uploadService;

	@RequestMapping(value="all", method=RequestMethod.GET)
	public ResponseEntity<Page<EmpresaDTO>> all(Pageable pageable, @AuthenticationPrincipal UsuarioDTO usuarioDTO) {


		if (usuarioDTO.getEmpresaId() != null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Page<EmpresaDTO> empresaDTOPage = empresaRepository.findBy(pageable).map(EmpresaMapper.INSTANCE::empresaToEmpresaDTO);
		return new ResponseEntity<>( empresaDTOPage, HttpStatus.OK);
	}

	@RequestMapping(value="myEmpresa", method=RequestMethod.GET)
	public ResponseEntity<EmpresaDTO> myEmpresa(@AuthenticationPrincipal UsuarioDTO usuarioDTO) {


		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		EmpresaDTO empresaDTO = null;
		Optional<Empresa> empresa = empresaRepository.findById(usuarioDTO.getEmpresaId());
		if (empresa.isPresent()) {
			empresaDTO = EmpresaMapper.INSTANCE.empresaToEmpresaDTO(empresa.get());
		}
		return new ResponseEntity<>( empresaDTO, HttpStatus.OK);
	}

	@RequestMapping(value="register", method=RequestMethod.POST)
	public ResponseEntity<ResultEmpresaDTO> register(@Valid @RequestBody RegisterEmpresaDTO registerEmpresaDTO, @AuthenticationPrincipal UsuarioDTO usuarioDTO) {

		if (usuarioDTO.getEmpresaId() != null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Empresa createEmpresa = EmpresaMapper.INSTANCE.registerEmpresaDTOToEmpresa(registerEmpresaDTO);
		createEmpresa.setEnabled(Boolean.TRUE);
		empresaRepository.save(createEmpresa);
		String generatedPassword = RandomStringUtils.randomAlphanumeric(10);
		usuarioRepository.save(new Usuario( createEmpresa, new TipoUsuario(UsuarioTypesConstants.USUARIO), registerEmpresaDTO.getNombre(), registerEmpresaDTO.getEmail(),encryptService.encrypt(generatedPassword) ));


		return new ResponseEntity<>(new ResultEmpresaDTO(registerEmpresaDTO.getNombreEmpresa(), registerEmpresaDTO.getNombre(), registerEmpresaDTO.getEmail(), generatedPassword), HttpStatus.CREATED);
	}

	@RequestMapping(value="changeName", method=RequestMethod.PUT)
	public ResponseEntity<Void> changeName(@NotEmpty @RequestParam(value = "name") String name, @AuthenticationPrincipal UsuarioDTO usuarioDTO) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		empresaRepository.save(new Empresa(usuarioDTO.getEmpresaId(), name, Boolean.TRUE));
		return new ResponseEntity<>( HttpStatus.OK );
	}

	@RequestMapping(value="changePreferences", method=RequestMethod.PUT)
	public ResponseEntity<EmpresaDTO> changeName( @RequestBody EmpresaDTO empresaDTO, @AuthenticationPrincipal UsuarioDTO usuarioDTO) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		empresaRepository.findById(usuarioDTO.getEmpresaId()).ifPresent(empresa -> {
			if (empresaDTO.getNombre() != null && empresaDTO.getNombre().isEmpty()){
				empresa.setNombre(empresaDTO.getNombre());
			}
			empresa.setActualizarDolarAuto(empresaDTO.getActualizarDolarAuto());
			empresa.setIva(empresaDTO.getIva());
			empresa.setValorIva(empresaDTO.getValorIva());
			empresa.setMostrarPrecioDolar(empresaDTO.getMostrarPrecioDolar());
			empresa.setPorcentajeGanancia(empresaDTO.getPorcentajeGanancia());
			empresa.setPrecioDolar(empresaDTO.getPrecioDolar());
			empresaRepository.save(empresa);
		});


		return new ResponseEntity<>(empresaDTO, HttpStatus.OK );
	}

	@RequestMapping(value="disable", method=RequestMethod.PUT)
	public ResponseEntity<Void> disable(@Min(1) @RequestParam(value = "empresaId") Long empresaId, @AuthenticationPrincipal UsuarioDTO usuarioDTO) {

		if (usuarioDTO.getEmpresaId() != null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Empresa> empresa = empresaRepository.findById(empresaId);
		empresa.ifPresent(e -> {
			e.setEnabled(Boolean.FALSE);
			empresaRepository.save(e);
		});
		return new ResponseEntity<>( HttpStatus.OK );
	}

	@RequestMapping(value="enable", method=RequestMethod.PUT)
	public ResponseEntity<Void> register(@Min(1) @RequestParam(value = "empresaId") Long empresaId, @AuthenticationPrincipal UsuarioDTO usuarioDTO) {

		if (usuarioDTO.getEmpresaId() != null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Empresa> empresa = empresaRepository.findById(empresaId);
		empresa.ifPresent(e -> {
			e.setEnabled(Boolean.TRUE);
			empresaRepository.save(e);
		});
		return new ResponseEntity<>( HttpStatus.OK );
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

	/*@RequestMapping(path = "file/download", method = RequestMethod.GET)
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
		// sc.getResourceAsStream("")

		log.info("Donwload in controller");
		if (size.equals("50x50")) {
			return uploadService.downloadUserImageShort(Long.parseLong(userId.get()), request, response);
		} else if (size.equals("500x500")) {
			return uploadService.downloadUserImageBig(Long.parseLong(userId.get()), request, response);
		} else {
			return uploadService.downloadUserImageMed(Long.parseLong(userId.get()), request, response);
		}

	}*/

	@RequestMapping(path = "file/delete", method = RequestMethod.DELETE)
	public ResponseEntity<Long> deleteFile(@AuthenticationPrincipal UsuarioDTO usuarioDTO,
										   HttpServletRequest request) throws IllegalStateException, IOException,
			InvalidFileException {

		Long result = uploadService.deleteUserImage(usuarioDTO.getUsuarioId() );
		log.info("Image Deleted by userId: " + usuarioDTO.getUsuarioId());

		return new ResponseEntity<>(result, HttpStatus.OK);
	}


}
