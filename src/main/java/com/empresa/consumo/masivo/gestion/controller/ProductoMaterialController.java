package com.empresa.consumo.masivo.gestion.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import com.empresa.consumo.masivo.gestion.data.entity.*;
import com.empresa.consumo.masivo.gestion.data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.consumo.masivo.gestion.DTO.AddProductoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.MaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.ModifyProductoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.ProductoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUnidadDTO;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;

@RestController
@RequestMapping("api/productoMaterial")
public class ProductoMaterialController {
	
	//Logger log = LogManager.getLogger();
	
	@Autowired
	private ProductoRepository productoRepository;
	@Autowired
	private ProductoMaterialRepository productoMaterialRepository;
	@Autowired
	private MaterialRepository materialRepository;
	@Autowired
	private TipoUnidadRepository tipoUnidadRepository;
	@Autowired
	private ProductoHistoryRepository productoHistoryRepository;
	@Autowired
	private ProductoController productoController;
	
	
	//DONE
	@RequestMapping(value="all", method = RequestMethod.GET)
	public ResponseEntity<Page<ProductoMaterialDTO>> getAllMaterialesDeProductos(@Min(value = 1) @RequestParam(value = "productoId") Long productoId, @RequestParam(value = "search", required = false, defaultValue = "") String search, @AuthenticationPrincipal UsuarioDTO usuarioDTO,Pageable pageable) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Page<ProductoMaterialDTO> pageProductosMateriales = null;

		pageProductosMateriales = productoMaterialRepository.findByProducto_Empresa_EmpresaIdAndProducto_ProductoIdAndProducto_ActivoAndMaterial_ActivoAndMaterial_NombreContainingIgnoreCaseOrderByMaterial_Nombre(usuarioDTO.getEmpresaId(), productoId, Boolean.TRUE, Boolean.TRUE, "%"+search +"%",pageable)
					.map(ProductoMapper.INSTANCE::productoMaterialToProductoMaterialDTO);

		Page<ProductoMaterialDTO> newPageProductosMateriales = doLogic(pageProductosMateriales);

		return new ResponseEntity<>(newPageProductosMateriales, HttpStatus.OK);
	}

	@RequestMapping(value="byId", method = RequestMethod.GET)
	public ResponseEntity<ProductoMaterialDTO> getMaterialesDeProductos(@Min(value = 1) @RequestParam(value = "productoMaterialId") Long productoMaterialId, @AuthenticationPrincipal UsuarioDTO usuarioDTO,Pageable pageable) {

		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Page<ProductoMaterialDTO> pageProductosMateriales = null;

		pageProductosMateriales = productoMaterialRepository.findByProducto_Empresa_EmpresaIdAndProductoMaterialIdAndProducto_ActivoAndMaterial_ActivoOrderByMaterial_Nombre(usuarioDTO.getEmpresaId(), productoMaterialId, Boolean.TRUE, Boolean.TRUE ,pageable)
				.map(ProductoMapper.INSTANCE::productoMaterialToProductoMaterialDTO);

		Page<ProductoMaterialDTO> newPageProductosMateriales = doLogic(pageProductosMateriales);

		if (newPageProductosMateriales.isEmpty()){
			return new ResponseEntity<>(null, HttpStatus.OK);
		}else{
			return new ResponseEntity<>(newPageProductosMateriales.getContent().get(0), HttpStatus.OK);
		}
	}

	private Page<ProductoMaterialDTO> doLogic( Page<ProductoMaterialDTO> pageProductosMateriales ){
		Set<Long> materialIds = pageProductosMateriales.get().map(m -> m.getMaterial().getMaterialId()).collect(Collectors.toSet());
		Set<Long> tipoUnidadIds = pageProductosMateriales.get().map(m -> m.getTipoUnidad().getTipoUnidadId()).collect(Collectors.toSet());

		Map<Long, TipoUnidadDTO> tipoUnidadMap = tipoUnidadRepository.findByTipoUnidadIdIn(tipoUnidadIds)
				.stream()
				.map(ProductoMapper.INSTANCE::tipoUnidadToTipoUnidadDTO)
				.collect(Collectors.toMap(TipoUnidadDTO::getTipoUnidadId, t -> t));

		Map<Long, MaterialDTO> materialMap = materialRepository.findByMaterialIdIn(materialIds)
				.stream()
				.map(ProductoMapper.INSTANCE::materialToMaterialDTO)
				.collect(Collectors.toMap(MaterialDTO::getMaterialId, m -> m));

		pageProductosMateriales.forEach(p -> {
			p.setMaterial(materialMap.get(p.getMaterial().getMaterialId()));
			p.getMaterial().setTipoUnidad(null);
			p.setTipoUnidad(tipoUnidadMap.get(p.getTipoUnidad().getTipoUnidadId()));
		});
		return pageProductosMateriales;
	}
	
	//DONE
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value="add", method = RequestMethod.POST)
	public ResponseEntity<?> addProductoMaterial(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Valid @RequestBody AddProductoMaterialDTO addProductoMaterialDTO) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		if (!productoRepository.existsByProductoIdAndEmpresa_EmpresaId(addProductoMaterialDTO.getProductoId(), usuarioDTO.getEmpresaId())) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		if (!materialRepository.existsByMaterialIdAndEmpresa_EmpresaId(addProductoMaterialDTO.getMaterialId(), usuarioDTO.getEmpresaId())) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		ProductoMaterial productoMaterial = new ProductoMaterial(new Material(addProductoMaterialDTO.getMaterialId()), new Producto(addProductoMaterialDTO.getProductoId()), ProductoMapper.INSTANCE.tipoUnidadDTOToTipoUnidad(addProductoMaterialDTO.getTipoUnidad()), addProductoMaterialDTO.getCantidad());
		productoMaterialRepository.save(productoMaterial);

		productoRepository.findById(addProductoMaterialDTO.getProductoId()).ifPresent(p -> {
			p.setActualizadoPor(new Usuario(usuarioDTO.getUsuarioId()));
			p.setFechaActualizacion(LocalDateTime.now(ZoneId.systemDefault()));
			productoRepository.save(p);
		});

		productoController.getAllProductsByMaterialIdWithUpdate(productoMaterial.getProducto().getProductoId(), usuarioDTO.getEmpresaId(), true, null, null);
		
		
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	//DONE
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value="update", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProductoMaterial(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Valid @RequestBody ModifyProductoMaterialDTO modifyProductoMaterialDTO) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		
		Optional<ProductoMaterial> productoMaterial = productoMaterialRepository.findById(modifyProductoMaterialDTO.getProductoMaterialId());
		if (!productoMaterial.isPresent()) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		Producto producto = productoRepository.findById(productoMaterial.get().getProducto().getProductoId()).get();
		
		
		if (producto.getEmpresa().getEmpresaId().longValue() == usuarioDTO.getEmpresaId().longValue()) {
			productoMaterial.get().setCantidad(modifyProductoMaterialDTO.getCantidad());
			productoMaterial.get().setTipoUnidad(ProductoMapper.INSTANCE.tipoUnidadDTOToTipoUnidad(modifyProductoMaterialDTO.getTipoUnidad()));
			productoMaterialRepository.save(productoMaterial.get());

			producto.setActualizadoPor(new Usuario(usuarioDTO.getUsuarioId()));
			producto.setFechaActualizacion(LocalDateTime.now(ZoneId.systemDefault()));
			productoRepository.save(producto);

			productoController.getAllProductsByMaterialIdWithUpdate(productoMaterial.get().getProducto().getProductoId(), usuarioDTO.getEmpresaId(), true, null, null);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	//DONE
	@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false, rollbackFor = {
			Exception.class })
	@RequestMapping(value="delete", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteProductoMaterial(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @Min(value = 1) @RequestParam(value = "productoMaterialId") Long productoMaterialId) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Optional<ProductoMaterial> productoMaterial = productoMaterialRepository.findById(productoMaterialId);
		if (!productoMaterial.isPresent()) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		Producto producto = productoRepository.findById(productoMaterial.get().getProducto().getProductoId()).get();

		if (producto.getEmpresa().getEmpresaId().longValue() == usuarioDTO.getEmpresaId().longValue()) {
			productoMaterialRepository.delete(productoMaterial.get());
			productoController.getAllProductsByMaterialIdWithUpdate(producto.getProductoId(), usuarioDTO.getEmpresaId(), true, null, null);
			return new ResponseEntity<>(HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
	}
	
	
}
