package com.empresa.consumo.masivo.gestion.controller;

import com.empresa.consumo.masivo.gestion.DTO.MaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUnidadDTO;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.Material;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import com.empresa.consumo.masivo.gestion.data.repository.MaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.TipoUnidadRepository;
import com.empresa.consumo.masivo.gestion.data.repository.UsuarioRepository;
import com.empresa.consumo.masivo.gestion.exception.ImageNotFoundException;
import com.empresa.consumo.masivo.gestion.exception.InvalidFileException;
import com.empresa.consumo.masivo.gestion.exception.InvalidMaterialException;
import com.empresa.consumo.masivo.gestion.security.JwtService;
import com.empresa.consumo.masivo.gestion.service.UploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/materiales")
public class MaterialController {
	
	Logger log = LogManager.getLogger();
	
	@Autowired
	private MaterialRepository materialRepository;
	@Autowired
	private TipoUnidadRepository tipoUnidadRepository;
	@Autowired
    private JwtService jwtService;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private UploadService uploadService;
	@Autowired
	private ProductoController productoController;
	
	//DONE
	@RequestMapping(value="all", method = RequestMethod.GET)
	public ResponseEntity<Page<MaterialDTO>> getAllMateriales(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "search", required = false) String search,Pageable pageable) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Page<MaterialDTO> pageMateriales = null;
		if (search != null && !search.isEmpty()){
			pageMateriales = materialRepository.findByEmpresa_EmpresaIdAndActivoAndNombreContainingIgnoreCase(usuarioDTO.getEmpresaId(), true, search, pageable)
					.map(ProductoMapper.INSTANCE::materialToMaterialDTO);

		}else{
			pageMateriales = materialRepository.findByEmpresa_EmpresaIdAndActivo(usuarioDTO.getEmpresaId(), true, pageable)
					.map(ProductoMapper.INSTANCE::materialToMaterialDTO);
		}

		Page<MaterialDTO> newPageMaterials = doLogic(pageMateriales, usuarioDTO);
		
		return new ResponseEntity<>(newPageMaterials, HttpStatus.OK);
	}

	@RequestMapping(value="byId", method = RequestMethod.GET)
	public ResponseEntity<MaterialDTO> getById(@AuthenticationPrincipal UsuarioDTO usuarioDTO,@Min(value = 1) @RequestParam(value = "materialId") Long materialId, Pageable pageable) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Page<MaterialDTO> pageMaterials = materialRepository.findByMaterialIdAndEmpresa_EmpresaIdAndActivo(materialId, usuarioDTO.getEmpresaId(), true, pageable)
				.map(ProductoMapper.INSTANCE::materialToMaterialDTO);

		Page<MaterialDTO> newPageMaterials = doLogic(pageMaterials, usuarioDTO);

		if (pageMaterials.isEmpty()){
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(newPageMaterials.getContent().get(0), HttpStatus.OK);
		}
	}

	private Page<MaterialDTO> doLogic(Page<MaterialDTO> pageMateriales, UsuarioDTO usuarioDTO){
		Set<Long> tipoUnidadIds = pageMateriales.get().map(m -> m.getTipoUnidad().getTipoUnidadId()).collect(Collectors.toSet());

		Map<Long, TipoUnidadDTO> tipoUnidadMap = tipoUnidadRepository.findByTipoUnidadIdIn(tipoUnidadIds)
				.stream()
				.map(ProductoMapper.INSTANCE::tipoUnidadToTipoUnidadDTO)
				.collect(Collectors.toMap(TipoUnidadDTO::getTipoUnidadId, t -> t));

		pageMateriales.forEach(p -> {

			p.setTipoUnidad(tipoUnidadMap.get(p.getTipoUnidad().getTipoUnidadId()));
		});
		return pageMateriales;
	}

	@RequestMapping(value="allWithOutPage", method = RequestMethod.GET)
	public ResponseEntity<List<MaterialDTO>> getAllMaterialesWithOutPages(@AuthenticationPrincipal UsuarioDTO usuarioDTO) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}

		List<MaterialDTO> listMateriales = materialRepository.findByEmpresa_EmpresaIdAndActivo(usuarioDTO.getEmpresaId(), true)
				.stream()
				.map(ProductoMapper.INSTANCE::materialToMaterialDTO)
				.collect(Collectors.toList());

		return new ResponseEntity<>(listMateriales, HttpStatus.OK);
	}

	@RequestMapping(value="allTipoUnidad", method = RequestMethod.GET)
	public ResponseEntity<List<TipoUnidadDTO>> getAllTipoUnidad(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "tipoUnidadId", required = false) Long tipoUnidadId) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		List<TipoUnidadDTO> listTipoUnidad = new ArrayList<>();
		if (tipoUnidadId != null){
			tipoUnidadRepository.findById(tipoUnidadId).ifPresent(tipoUnidad ->
				listTipoUnidad.addAll(tipoUnidadRepository.findByAgrupacionOrderByTipo(tipoUnidad.getAgrupacion())
						.stream()
						.map(ProductoMapper.INSTANCE::tipoUnidadToTipoUnidadDTO)
						.collect(Collectors.toList()))
			);
		}else{
			listTipoUnidad.addAll(tipoUnidadRepository.findByOrderByTipo()
					.stream()
					.map(ProductoMapper.INSTANCE::tipoUnidadToTipoUnidadDTO)
					.collect(Collectors.toList()));
		}


		return new ResponseEntity<>(listTipoUnidad, HttpStatus.OK);
	}
	
	//DONE
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value="add", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addMaterial(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "materialDTO") String materialDTOString,
										 @RequestParam(value = "imagen", required = false) MultipartFile file) throws InvalidMaterialException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.registerModule(new JavaTimeModule());
		MaterialDTO materialDTO = null;
		try {
			materialDTO = mapper.readValue(materialDTOString, MaterialDTO.class);
		} catch (IOException e) {
			throw new InvalidMaterialException("Invalid material");
		}

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		Material material = ProductoMapper.INSTANCE.materialDTOToMaterial(materialDTO);
		material.setNombre(material.getNombre().trim());
		material.setEmpresa(new Empresa(usuarioDTO.getEmpresaId()));
		material.setActivo(Boolean.TRUE);
		MaterialDTO savedMaterial = ProductoMapper.INSTANCE.materialToMaterialDTO(materialRepository.save(material)) ;
		if (file != null) {
			String fileName = uploadService.uploadMaterialImage(file, savedMaterial.getMaterialId());
		}
		
		return new ResponseEntity<>(savedMaterial, HttpStatus.CREATED);
	}
	
	//DONE
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value="update", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMaterial(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Valid @RequestBody MaterialDTO materialDTO) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		if (materialDTO.getMaterialId() == null || materialDTO.getMaterialId() < 1) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		Material material = materialRepository.findById(materialDTO.getMaterialId()).get();
		if (material.getEmpresa().getEmpresaId().longValue() == usuarioDTO.getEmpresaId().longValue()) {
			material = ProductoMapper.INSTANCE.materialDTOToMaterial(materialDTO);
			material.setNombre(material.getNombre().trim());
			material.setEmpresa(new Empresa(usuarioDTO.getEmpresaId()));
			material.setActivo(Boolean.TRUE);
			materialRepository.save(material);
			productoController.getAllProductsByMaterialIdWithUpdate(material.getMaterialId(), usuarioDTO.getEmpresaId(),false);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}


		
	}
	
	//DONE
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value="delete", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMaterial(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Min(value = 1) @RequestParam(value = "materialId") Long materialId) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Material material = materialRepository.findById(materialId).get();
		if (material.getEmpresa().getEmpresaId().longValue() == usuarioDTO.getEmpresaId().longValue()) {
			material.setActivo(Boolean.FALSE);
			materialRepository.save(material);
			productoController.getAllProductsByMaterialIdWithUpdate(material.getMaterialId(), usuarioDTO.getEmpresaId(), false);
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
			 HttpServletRequest request) throws IllegalStateException{

		if (!materialRepository.existsByMaterialIdAndEmpresa_EmpresaId(Long.parseLong(materialId), usuarioDTO.getEmpresaId())) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}

		Long result = uploadService.deleteMaterialImage(Long.parseLong(materialId));
		log.info("Image Deleted by userId: " + usuarioDTO.getUsuarioId());

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	
}
