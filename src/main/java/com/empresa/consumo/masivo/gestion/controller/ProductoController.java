package com.empresa.consumo.masivo.gestion.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.empresa.consumo.masivo.gestion.DTO.*;
import com.empresa.consumo.masivo.gestion.data.entity.ProductoHistory;
import com.empresa.consumo.masivo.gestion.data.repository.*;
import com.empresa.consumo.masivo.gestion.exception.BusinessServiceException;
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

import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.Producto;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;
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
	@Autowired
	private EmpresaRepository empresaRepository;
	@Autowired
	private ProductoHistoryRepository productoHistoryRepository;
	
	//TODO precios
	@RequestMapping(value="all", method = RequestMethod.GET)
	public ResponseEntity<Page<ProductoDTO>> getAllProducts(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "search", required = false, defaultValue = "") String search,Pageable pageable) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}

		Page<ProductoDTO> pageProductos = productoRepository.findByEmpresa_EmpresaIdAndActivoAndNombreContainingIgnoreCaseOrderByNombre(usuarioDTO.getEmpresaId(), true, "%"+search+"%", pageable)
					.map(ProductoMapper.INSTANCE::productoToProductoDTO);


		Page<ProductoDTO> newPageProductos = doLogic(pageProductos, usuarioDTO);
		
		
		return new ResponseEntity<>(newPageProductos, HttpStatus.OK);
	}

	@RequestMapping(value="last10History", method = RequestMethod.GET)
	public ResponseEntity<List<ProductoHistoryDTO>> getLast10Products(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "productoId") Long productoId) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}

		List<ProductoHistoryDTO> productoHistoryDTOS = productoHistoryRepository.findFirst10ByProducto_ProductoIdOrderByFechaCreacionDesc(productoId)
				.stream()
				.map(ProductoMapper.INSTANCE::productoHistoryToProductoHistoryDTO)
				.collect(Collectors.toList());

		return new ResponseEntity<>(productoHistoryDTOS, HttpStatus.OK);
	}

	@RequestMapping(value="byId", method = RequestMethod.GET)
	public ResponseEntity<ProductoDTO> getById(@AuthenticationPrincipal UsuarioDTO usuarioDTO,@Min(value = 1) @RequestParam(value = "productoId") Long productoId,Pageable pageable) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Page<ProductoDTO> pageProductos = productoRepository.findByProductoIdAndEmpresa_EmpresaIdAndActivo(productoId, usuarioDTO.getEmpresaId(), true, pageable)
				.map(ProductoMapper.INSTANCE::productoToProductoDTO);

		Page<ProductoDTO> newPageProductos = doLogic(pageProductos, usuarioDTO);

		if (pageProductos.isEmpty()){
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(newPageProductos.getContent().get(0), HttpStatus.OK);
		}
	}

	public List<ProductoDTO> getAllProductsByMaterialIdWithUpdate(Long materialIdOrProductoId, Long empresaId, Boolean productoId, Date desde, Date hasta) {

		List<ProductoDTO> productoDTOS = new ArrayList<>();
		if (materialIdOrProductoId != null){
			if (productoId){
				productoDTOS = productoRepository.findByProductoId(materialIdOrProductoId).stream().map
						(ProductoMapper.INSTANCE::productoToProductoDTO).collect(Collectors.toList());
			}else {
				productoDTOS = productoRepository.findByProductoMaterials_Material_MaterialId(materialIdOrProductoId).stream().map
						(ProductoMapper.INSTANCE::productoToProductoDTO).collect(Collectors.toList());
			}
		}else {
			productoDTOS = productoRepository.findByEmpresa_EmpresaIdAndActivoAndFechaCreacionBetweenOrderByNombre(empresaId, Boolean.TRUE, LocalDateTime.ofInstant(desde.toInstant(), ZoneId.systemDefault()), LocalDateTime.ofInstant(hasta.toInstant(), ZoneId.systemDefault()))
					.stream().map(ProductoMapper.INSTANCE::productoToProductoDTO).collect(Collectors.toList());
		}



		for (ProductoDTO productoDTO:productoDTOS){
			List<ProductoMaterialDTO> pageProductosMateriales = productoMaterialRepository.findByProducto_Empresa_EmpresaIdAndProducto_ProductoIdAndProducto_ActivoAndMaterial_Activo(empresaId, productoDTO.getProductoId(), Boolean.TRUE, Boolean.TRUE)
					.stream()
					.map(ProductoMapper.INSTANCE::productoMaterialToProductoMaterialDTO)
					.collect(Collectors.toList());

			Set<Long> materialIds = pageProductosMateriales.stream().map(m -> m.getMaterial().getMaterialId()).collect(Collectors.toSet());
			Set<Long> tipoUnidadIds = pageProductosMateriales.stream().map(m -> m.getTipoUnidad().getTipoUnidadId()).collect(Collectors.toSet());

			Map<Long, MaterialDTO> materialMap = materialRepository.findByMaterialIdIn(materialIds)
					.stream()
					.map(ProductoMapper.INSTANCE::materialToMaterialDTO)
					.collect(Collectors.toMap(MaterialDTO::getMaterialId, m -> m));

			tipoUnidadIds.addAll(materialMap.values().stream().map(m -> m.getTipoUnidad().getTipoUnidadId()).collect(Collectors.toList()));

			Map<Long, TipoUnidadDTO> tipoUnidadMap = tipoUnidadRepository.findByTipoUnidadIdIn(tipoUnidadIds)
					.stream()
					.map(ProductoMapper.INSTANCE::tipoUnidadToTipoUnidadDTO)
					.collect(Collectors.toMap(TipoUnidadDTO::getTipoUnidadId, t -> t));

			materialMap.forEach( (k,v) -> {
				v.setTipoUnidad(tipoUnidadMap.get(v.getTipoUnidad().getTipoUnidadId()));
			} );

			pageProductosMateriales.forEach(p -> {
				p.setMaterial(materialMap.get(p.getMaterial().getMaterialId()));
				p.setTipoUnidad(tipoUnidadMap.get(p.getTipoUnidad().getTipoUnidadId()));
			});

			Double total = 0D;
			for(ProductoMaterialDTO pmDTO:pageProductosMateriales) {

				Double costoCompra = pmDTO.getMaterial().getCosto();
				//CONVIERTE LA CANTIDAD COMPRADA DE CUALQUIER UNIDAD A GRAMOS
				Double cantidadCompra = pmDTO.getMaterial().getCantidadCompra() * pmDTO.getMaterial().getTipoUnidad().getReferenciaEnGramos();
				Double cantidadUsada = pmDTO.getCantidad() * pmDTO.getTipoUnidad().getReferenciaEnGramos();

				total += (costoCompra*cantidadUsada)/cantidadCompra;
			}

			if (productoDTO.getDepreciacion() != null && productoDTO.getDepreciacion() > 0d){
				total += total*(productoDTO.getDepreciacion()/100);
			}


			DecimalFormat df = new DecimalFormat("#,###,##0.00");
			if (productoDTO.getDepreciacion() != null && productoDTO.getDepreciacion() > 0d){
				total += total*(productoDTO.getDepreciacion()/100);
			}
			productoDTO.setCostoProduccion(df.format(total));
			Optional<Empresa> optionalEmpresa = empresaRepository.findById(empresaId);
			Double ganancia = 0d;
			Double precioDolares = 0d;
			if (optionalEmpresa.isPresent()){
				if (optionalEmpresa.get().getIva()){
					if (optionalEmpresa.get().getValorIva() != null && optionalEmpresa.get().getValorIva() > 0d){
						total += total*(optionalEmpresa.get().getValorIva()/100);
					}else{
						total += total*0.16;
					}
				}
				if (optionalEmpresa.get().getPorcentajeGanancia() != null && optionalEmpresa.get().getPorcentajeGanancia() > 0){
					ganancia = total*(optionalEmpresa.get().getPorcentajeGanancia()/100);
					total += ganancia;
				}
				if (optionalEmpresa.get().getPrecioDolar() != null && optionalEmpresa.get().getPrecioDolar() > 0d){
					precioDolares = total/optionalEmpresa.get().getPrecioDolar();
				}
			}
			productoDTO.setPrecioVenta(df.format(total));
			productoDTO.setGanancia(df.format(ganancia));
			productoDTO.setPrecioVentaDolares(df.format(precioDolares));

			if (materialIdOrProductoId != null){
				ProductoHistory productoHistory = new ProductoHistory();
				productoHistory.setPrecioVenta(total);
				productoHistory.setProducto(new Producto(productoDTO.getProductoId()));
				productoHistory.setFechaCreacion(LocalDateTime.now(ZoneId.systemDefault()));
				productoHistoryRepository.save(productoHistory);
			}
		}
		return productoDTOS;
	}

	private Page<ProductoDTO> doLogic(Page<ProductoDTO> pageProductos, UsuarioDTO usuarioDTO){

		for (ProductoDTO productoDTO: pageProductos.getContent()) {
			List<ProductoMaterialDTO> pageProductosMateriales = productoMaterialRepository.findByProducto_Empresa_EmpresaIdAndProducto_ProductoIdAndProducto_ActivoAndMaterial_Activo(usuarioDTO.getEmpresaId(), productoDTO.getProductoId(), Boolean.TRUE, Boolean.TRUE)
					.stream()
					.map(ProductoMapper.INSTANCE::productoMaterialToProductoMaterialDTO)
					.collect(Collectors.toList());

			Set<Long> materialIds = pageProductosMateriales.stream().map(m -> m.getMaterial().getMaterialId()).collect(Collectors.toSet());
			Set<Long> tipoUnidadIds = pageProductosMateriales.stream().map(m -> m.getTipoUnidad().getTipoUnidadId()).collect(Collectors.toSet());

			Map<Long, MaterialDTO> materialMap = materialRepository.findByMaterialIdIn(materialIds)
					.stream()
					.map(ProductoMapper.INSTANCE::materialToMaterialDTO)
					.collect(Collectors.toMap(MaterialDTO::getMaterialId, m -> m));

			tipoUnidadIds.addAll(materialMap.values().stream().map(m -> m.getTipoUnidad().getTipoUnidadId()).collect(Collectors.toList()));

			Map<Long, TipoUnidadDTO> tipoUnidadMap = tipoUnidadRepository.findByTipoUnidadIdIn(tipoUnidadIds)
					.stream()
					.map(ProductoMapper.INSTANCE::tipoUnidadToTipoUnidadDTO)
					.collect(Collectors.toMap(TipoUnidadDTO::getTipoUnidadId, t -> t));

			materialMap.forEach( (k,v) -> {
				v.setTipoUnidad(tipoUnidadMap.get(v.getTipoUnidad().getTipoUnidadId()));
			} );

			pageProductosMateriales.forEach(p -> {
				p.setMaterial(materialMap.get(p.getMaterial().getMaterialId()));
				p.setTipoUnidad(tipoUnidadMap.get(p.getTipoUnidad().getTipoUnidadId()));
			});

			Double total = 0D;
			for(ProductoMaterialDTO pmDTO:pageProductosMateriales) {

				Double costoCompra = pmDTO.getMaterial().getCosto();
				//CONVIERTE LA CANTIDAD COMPRADA DE CUALQUIER UNIDAD A GRAMOS
				Double cantidadCompra = pmDTO.getMaterial().getCantidadCompra() * pmDTO.getMaterial().getTipoUnidad().getReferenciaEnGramos();
				Double cantidadUsada = pmDTO.getCantidad() * pmDTO.getTipoUnidad().getReferenciaEnGramos();

				total += (costoCompra*cantidadUsada)/cantidadCompra;
			}

			DecimalFormat df = new DecimalFormat("#,###,##0.00");
			if (productoDTO.getDepreciacion() != null && productoDTO.getDepreciacion() > 0d){
				total += total*(productoDTO.getDepreciacion()/100);
			}
			productoDTO.setCostoProduccion(df.format(total));
			Optional<Empresa> optionalEmpresa = empresaRepository.findById(usuarioDTO.getEmpresaId());
			Double ganancia = 0d;
			Double precioDolares = 0d;
			if (optionalEmpresa.isPresent()){
				if (optionalEmpresa.get().getIva()){
					if (optionalEmpresa.get().getValorIva() != null && optionalEmpresa.get().getValorIva() > 0d){
						total += total*(optionalEmpresa.get().getValorIva()/100);
					}else{
						total += total*0.16;
					}
				}

				if (optionalEmpresa.get().getPorcentajeGanancia() != null && optionalEmpresa.get().getPorcentajeGanancia() > 0){
					ganancia = total*(optionalEmpresa.get().getPorcentajeGanancia()/100);
					total += ganancia;
				}

				if (optionalEmpresa.get().getPrecioDolar() != null && optionalEmpresa.get().getPrecioDolar() > 0d){
					precioDolares = total/optionalEmpresa.get().getPrecioDolar();
				}

			}
			productoDTO.setPrecioVenta(df.format(total));
			productoDTO.setGanancia(df.format(ganancia));
			productoDTO.setPrecioVentaDolares(df.format(precioDolares));

		}
		return pageProductos;
	}
	
	//DONE
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value = "add", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> addProducto(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @RequestParam(value = "nombre") String nombre,
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

		ProductoDTO savedProducto = ProductoMapper.INSTANCE.productoToProductoDTO(productoRepository.save(new Producto(new Empresa(usuarioDTO.getEmpresaId()), nombre.trim(), 0d, LocalDateTime.now(ZoneId.systemDefault()) ))) ;
		if (file != null) {
			String fileName = uploadService.uploadProductoImage(file, savedProducto.getProductoId());
		}
		
		
		return new ResponseEntity<>(savedProducto, HttpStatus.CREATED);
	}
	
	//DONE
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value="update", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProducto(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Valid @RequestBody ProductoDTO productoDTO) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Producto producto = productoRepository.findById(productoDTO.getProductoId()).get();
		if (producto.getEmpresa().getEmpresaId().longValue() == usuarioDTO.getEmpresaId().longValue()) {
			producto.setNombre(productoDTO.getNombre().trim());
			producto.setDepreciacion(productoDTO.getDepreciacion());
			productoRepository.save(producto);
			this.getAllProductsByMaterialIdWithUpdate(producto.getProductoId(), usuarioDTO.getEmpresaId(),true, null, null);
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
