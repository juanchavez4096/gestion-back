package com.empresa.consumo.masivo.gestion.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.consumo.masivo.gestion.DTO.MaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.ProductoDTO;
import com.empresa.consumo.masivo.gestion.DTO.ProductoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUnidadDTO;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.Producto;
import com.empresa.consumo.masivo.gestion.data.repository.MaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.ProductoMaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.ProductoRepository;
import com.empresa.consumo.masivo.gestion.data.repository.TipoUnidadRepository;

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
	@RequestMapping(value="add", method = RequestMethod.POST)
	public ResponseEntity<ProductoDTO> addProducto(@AuthenticationPrincipal UsuarioDTO usuarioDTO, @NotEmpty @RequestParam(value = "nombre") String nombre) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		
		ProductoDTO savedProducto = ProductoMapper.INSTANCE.productoToProductoDTO(productoRepository.save(new Producto(new Empresa(usuarioDTO.getEmpresaId()), nombre.trim() ))) ;
		
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
	
	
	
}
