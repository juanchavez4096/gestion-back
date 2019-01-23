package com.empresa.consumo.masivo.gestion.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.empresa.consumo.masivo.gestion.DTO.MaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.ProductoDTO;
import com.empresa.consumo.masivo.gestion.DTO.ProductoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUnidadDTO;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.Producto;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
import com.empresa.consumo.masivo.gestion.data.repository.MaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.ProductoMaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.ProductoRepository;
import com.empresa.consumo.masivo.gestion.data.repository.TipoUnidadRepository;
import com.empresa.consumo.masivo.gestion.data.repository.UsuarioRepository;
import com.empresa.consumo.masivo.gestion.exception.ImageNotFoundException;
import com.empresa.consumo.masivo.gestion.exception.InvalidFileException;
import com.empresa.consumo.masivo.gestion.security.JwtService;
import com.empresa.consumo.masivo.gestion.service.UploadService;

@RestController
@RequestMapping("api/productos")
public class ProductoController {
	
	Logger log = LogManager.getLogger();
	
	@Autowired
	private ProductoRepository productoRepository;
	@Autowired
	private ProductoMaterialRepository productoMaterialRepository;
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
	
	//TODO precios
	@RequestMapping(value="all", method = RequestMethod.GET)
	public ResponseEntity<Page<ProductoDTO>> getAllProducts(@AuthenticationPrincipal UsuarioDTO usuarioDTO,Pageable pageable) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Page<ProductoDTO> pageProductos = productoRepository.findByEmpresa_EmpresaIdAndActivo(usuarioDTO.getEmpresaId(), true, pageable)
				.map(producto -> ProductoMapper.INSTANCE.productoToProductoDTO(producto));
		
		for (ProductoDTO productoDTO: pageProductos.getContent()) {
			List<ProductoMaterialDTO> pageProductosMateriales = productoMaterialRepository.findByProducto_Empresa_EmpresaIdAndProducto_ProductoId(usuarioDTO.getEmpresaId(), productoDTO.getProductoId())
					.stream()
					.map(productoMaterial -> ProductoMapper.INSTANCE.productoMaterialToProductoMaterialDTO(productoMaterial))
					.collect(Collectors.toList());
			
			log.info(pageProductosMateriales.stream().map(ProductoMaterialDTO::getProductoMaterialId).collect(Collectors.toList()) + ": productoMaterialId");
			Set<Long> materialIds = pageProductosMateriales.stream().map(m -> m.getMaterial().getMaterialId()).collect(Collectors.toSet());
			Set<Long> tipoUnidadIds = pageProductosMateriales.stream().map(m -> m.getTipoUnidad().getTipoUnidadId()).collect(Collectors.toSet());
			
			Map<Long, TipoUnidadDTO> tipoUnidadMap = tipoUnidadRepository.findByTipoUnidadIdIn(tipoUnidadIds)
					.stream()
					.map(t -> ProductoMapper.INSTANCE.tipoUnidadToTipoUnidadDTO(t))
					.collect(Collectors.toMap(TipoUnidadDTO::getTipoUnidadId, t -> t));
			
			Map<Long, MaterialDTO> materialMap = materialRepository.findByMaterialIdIn(materialIds)
					.stream()
					.map(m -> ProductoMapper.INSTANCE.materialToMaterialDTO(m))
					.collect(Collectors.toMap(MaterialDTO::getMaterialId, m -> m));
			
			pageProductosMateriales.forEach(p -> {
				p.setMaterial(materialMap.get(p.getMaterial().getMaterialId()));
				p.getMaterial().setTipoMaterial(null);
				p.getMaterial().setTipoUnidad(null);
				p.setTipoUnidad(tipoUnidadMap.get(p.getTipoUnidad().getTipoUnidadId()));
			});
			
			Double total = 0d;
			for(ProductoMaterialDTO pmDTO:pageProductosMateriales) {
				
				Double referenciaEnGramos = pmDTO.getTipoUnidad().getReferenciaEnGramos();
				Double cantidadCompra = pmDTO.getMaterial().getCantidadCompra();
				Double costo = pmDTO.getMaterial().getCosto();
				
				Double cantidadTotal = cantidadCompra/referenciaEnGramos;
				total += cantidadTotal*costo;
			}
			productoDTO.setCosto(total);
		}
		
		
		return new ResponseEntity<>(pageProductos, HttpStatus.OK);
	}
	
	//DONE
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value="add", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addProducto(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @NotEmpty @RequestParam(value = "nombre") String nombre,
			@RequestParam(value = "file", required = false) MultipartFile file) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (file != null) {
			ResponseEntity<String> responseEntity = this.validateFile(file);
			if (responseEntity != null ) {
				return responseEntity;
			}
		}
		
		
		
		ProductoDTO savedProducto = ProductoMapper.INSTANCE.productoToProductoDTO(productoRepository.save(new Producto(new Empresa(usuarioDTO.getEmpresaId()), nombre.trim() ))) ;
		if (file != null) {
			String fileName = uploadService.uploadProductoImage(file, savedProducto.getProductoId());
		}
		
		
		return new ResponseEntity<>(savedProducto, HttpStatus.CREATED);
	}
	
	//DONE
	@RequestMapping(value="update", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProducto(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Valid @RequestBody ProductoDTO productoDTO) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Producto producto = productoRepository.findById(productoDTO.getProductoId()).get();
		if (producto.getEmpresa().getEmpresaId().longValue() == usuarioDTO.getEmpresaId().longValue()) {
			producto.setNombre(productoDTO.getNombre().trim());
			productoRepository.save(producto);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	//DONE
	@RequestMapping(value="delete", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProducto(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Min(value = 1) @RequestParam(value = "productoId") Long productoId) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Producto producto = productoRepository.findById(productoId).get();
		if (producto.getEmpresa().getEmpresaId().longValue() == usuarioDTO.getEmpresaId().longValue()) {
			producto.setActivo(Boolean.FALSE);
			productoRepository.save(producto);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	@RequestMapping(path = "file/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> uploadAttachment(@RequestParam("file") MultipartFile file,
			 @RequestParam("productoId") String productoId, @AuthenticationPrincipal UsuarioDTO usuarioDTO)
			throws IllegalStateException, IOException {
		
		
		if (!productoRepository.existsByProductoIdAndEmpresa_EmpresaId(Long.parseLong(productoId), usuarioDTO.getEmpresaId())) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		ResponseEntity<String> responseEntity = this.validateFile(file);
		if (responseEntity != null ) {
			return responseEntity;
		}
		
		

		
		String fileName = uploadService.uploadProductoImage(file, Long.parseLong(productoId));

		log.info("Image uploaded with userId " + usuarioDTO.getUsuarioId());

		return new ResponseEntity<>(fileName, HttpStatus.OK);
	}

	@RequestMapping(path = "file/download", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadAttachment(@RequestParam("token") String token,@RequestParam("productoId") String productoId , @RequestParam("size") String size, HttpServletRequest request,
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
		
		if (!productoRepository.existsByProductoIdAndEmpresa_EmpresaId(Long.parseLong(productoId), usuario.get().getEmpresa().getEmpresaId())) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		if (size == null || size.isEmpty())
			throw new InvalidFileException("The size cannot be empty: " + size);

		if (!size.equals("50x50") && !size.equals("128x128") && !size.equals("500x500") )
			throw new InvalidFileException("The file size is invalid: " + size);
		

		log.info("Donwload in controller");
		if (size.equals("50x50")) {
			return uploadService.downloadProductoImageShort(Long.parseLong(productoId), request, response);
		} else if (size.equals("500x500")) {
			return uploadService.downloadProductoImageBig(Long.parseLong(productoId), request, response);
		} else {
			return uploadService.downloadProductoImageMed(Long.parseLong(productoId), request, response);
		}

	}

	@RequestMapping(path = "file/delete", method = RequestMethod.DELETE)
	public ResponseEntity<Long> deleteFile(@RequestParam("productoId") String productoId, @AuthenticationPrincipal UsuarioDTO usuarioDTO,
			 HttpServletRequest request) throws IllegalStateException, IOException,
			 InvalidFileException {

		
		
		if (!productoRepository.existsByProductoIdAndEmpresa_EmpresaId(Long.parseLong(productoId), usuarioDTO.getEmpresaId())) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}

		Long result = uploadService.deleteProductoImage(Long.parseLong(productoId));
		log.info("Image Deleted by userId: " + usuarioDTO.getUsuarioId());

		return new ResponseEntity<>(result, HttpStatus.OK);
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
	
}
