package com.empresa.consumo.masivo.gestion.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;

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

import com.empresa.consumo.masivo.gestion.DTO.MaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUnidadDTO;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.Material;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import com.empresa.consumo.masivo.gestion.data.repository.MaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.TipoMaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.TipoUnidadRepository;
import com.empresa.consumo.masivo.gestion.data.repository.UsuarioRepository;
import com.empresa.consumo.masivo.gestion.exception.ImageNotFoundException;
import com.empresa.consumo.masivo.gestion.exception.InvalidFileException;
import com.empresa.consumo.masivo.gestion.security.JwtService;
import com.empresa.consumo.masivo.gestion.service.UploadService;

@RestController
@RequestMapping("api/materiales")
public class MaterialController {
	
	Logger log = LogManager.getLogger();
	
	@Autowired
	private MaterialRepository materialRepository;
	@Autowired
	private TipoMaterialRepository tipoMaterialRepository;
	@Autowired
	private TipoUnidadRepository tipoUnidadRepository;
	@Autowired
    private JwtService jwtService;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private UploadService uploadService;
	
	//DONE
	@RequestMapping(value="all", method = RequestMethod.GET)
	public ResponseEntity<Page<MaterialDTO>> getAllMateriales(@AuthenticationPrincipal UsuarioDTO usuarioDTO,Pageable pageable) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Page<MaterialDTO> pageMateriales = materialRepository.findByEmpresa_EmpresaIdAndActivo(usuarioDTO.getEmpresaId(), true, pageable)
				.map(material -> ProductoMapper.INSTANCE.materialToMaterialDTO(material));
		
		Set<Long> tipoMaterialIds = pageMateriales.get().map(m -> m.getTipoMaterial().getTipoMaterialId()).collect(Collectors.toSet());
		Set<Long> tipoUnidadIds = pageMateriales.get().map(m -> m.getTipoUnidad().getTipoUnidadId()).collect(Collectors.toSet());
		
		Map<Long, TipoMaterialDTO> tipoMaterialMap = tipoMaterialRepository.findByTipoMaterialIdIn(tipoMaterialIds)
														.stream()
														.map(t -> ProductoMapper.INSTANCE.tipoMaterialToTipoMaterialDTO(t))
														.collect(Collectors.toMap(TipoMaterialDTO::getTipoMaterialId, t -> t));
		
		Map<Long, TipoUnidadDTO> tipoUnidadMap = tipoUnidadRepository.findByTipoUnidadIdIn(tipoUnidadIds)
				.stream()
				.map(t -> ProductoMapper.INSTANCE.tipoUnidadToTipoUnidadDTO(t))
				.collect(Collectors.toMap(TipoUnidadDTO::getTipoUnidadId, t -> t));
		
		pageMateriales.forEach(p -> {
			p.setTipoMaterial(tipoMaterialMap.get(p.getTipoMaterial().getTipoMaterialId()));
			p.setTipoUnidad(tipoUnidadMap.get(p.getTipoUnidad().getTipoUnidadId()));
		});
		
		return new ResponseEntity<>(pageMateriales, HttpStatus.OK);
	}
	
	//DONE
	@RequestMapping(value="add", method = RequestMethod.POST)
	public ResponseEntity<?> addMaterial(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Valid @RequestBody MaterialDTO materialDTO) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (!tipoMaterialRepository.existsByTipoMaterialIdAndEmpresa_EmpresaId(materialDTO.getTipoMaterial().getTipoMaterialId(), usuarioDTO.getEmpresaId())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		Material material = ProductoMapper.INSTANCE.materialDTOToMaterial(materialDTO);
		material.setNombre(material.getNombre().trim());
		material.setEmpresa(new Empresa(usuarioDTO.getEmpresaId()));
		material.setActivo(Boolean.TRUE);
		MaterialDTO savedMaterial = ProductoMapper.INSTANCE.materialToMaterialDTO(materialRepository.save(material)) ;
		
		return new ResponseEntity<>(savedMaterial, HttpStatus.CREATED);
	}
	
	//DONE
	@RequestMapping(value="update", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMaterial(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Valid @RequestBody MaterialDTO materialDTO) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		if (materialDTO.getMaterialId() == null || materialDTO.getMaterialId() < 1) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		if (!tipoMaterialRepository.existsByTipoMaterialIdAndEmpresa_EmpresaId(materialDTO.getTipoMaterial().getTipoMaterialId(), usuarioDTO.getEmpresaId())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		Material material = materialRepository.findById(materialDTO.getMaterialId()).get();
		if (material.getEmpresa().getEmpresaId().longValue() == usuarioDTO.getEmpresaId().longValue()) {
			material = ProductoMapper.INSTANCE.materialDTOToMaterial(materialDTO);
			material.setNombre(material.getNombre().trim());
			material.setEmpresa(new Empresa(usuarioDTO.getEmpresaId()));
			material.setActivo(Boolean.TRUE);
			materialRepository.save(material);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	//DONE
	@RequestMapping(value="delete", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMaterial(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Min(value = 1) @RequestParam(value = "materialId") Long materialId) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Material material = materialRepository.findById(materialId).get();
		if (material.getEmpresa().getEmpresaId().longValue() == usuarioDTO.getEmpresaId().longValue()) {
			material.setActivo(Boolean.FALSE);
			materialRepository.save(material);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	@RequestMapping(path = "file/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadAttachment(@RequestParam("file") MultipartFile file,
			 @RequestParam("materialId") String materialId, @AuthenticationPrincipal UsuarioDTO usuarioDTO)
			throws IllegalStateException, IOException {
		
		
		if (!materialRepository.existsByMaterialIdAndEmpresa_EmpresaId(Long.parseLong(materialId), usuarioDTO.getEmpresaId())) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
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

		
		String fileName = uploadService.uploadMaterialImage(file, Long.parseLong(materialId));

		log.info("Image uploaded with userId " + usuarioDTO.getUsuarioId());

		return new ResponseEntity<>(fileName, HttpStatus.OK);
	}

	@RequestMapping(path = "file/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadAttachment(@RequestParam("token") String token,@RequestParam("materialId") String materialId , @RequestParam("size") String size, HttpServletRequest request,
			HttpServletResponse response) throws IllegalStateException, IOException,
			InvalidFileException, ImageNotFoundException {

		Optional<String> userId = jwtService.getSubFromToken(token);
		if (!userId.isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Optional<Usuario> usuario = usuarioRepository.findById(Integer.parseInt(userId.get()));
		if(!usuario.isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (!materialRepository.existsByMaterialIdAndEmpresa_EmpresaId(Long.parseLong(materialId), usuario.get().getEmpresa().getEmpresaId())) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (size == null || size.isEmpty())
			throw new InvalidFileException("The size cannot be empty: " + size);

		if (!size.equals("50x50") && !size.equals("128x128") && !size.equals("500x500") )
			throw new InvalidFileException("The file size is invalid: " + size);
		

		log.info("Donwload in controller");
		if (size.equals("50x50")) {
			return uploadService.downloadMaterialImageShort(Long.parseLong(materialId), request, response);
		} else if (size.equals("500x500")) {
			return uploadService.downloadMaterialImageBig(Long.parseLong(materialId), request, response);
		} else {
			return uploadService.downloadMaterialImageMed(Long.parseLong(materialId), request, response);
		}

	}

	@RequestMapping(path = "file/delete", method = RequestMethod.DELETE)
	public ResponseEntity<Long> deleteFile(@RequestParam("materialId") String materialId, @AuthenticationPrincipal UsuarioDTO usuarioDTO,
			 HttpServletRequest request) throws IllegalStateException, IOException,
			 InvalidFileException {

		
		
		if (!materialRepository.existsByMaterialIdAndEmpresa_EmpresaId(Long.parseLong(materialId), usuarioDTO.getEmpresaId())) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}

		Long result = uploadService.deleteMaterialImage(Long.parseLong(materialId));
		log.info("Image Deleted by userId: " + usuarioDTO.getUsuarioId());

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	
}
