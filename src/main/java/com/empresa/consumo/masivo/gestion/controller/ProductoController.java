package com.empresa.consumo.masivo.gestion.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

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

import com.empresa.consumo.masivo.gestion.DTO.ProductoDTO;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.Producto;
import com.empresa.consumo.masivo.gestion.data.repository.ProductoRepository;

@RestController
@RequestMapping("api/productos")
public class ProductoController {
	
	//Logger log = LogManager.getLogger();
	
	@Autowired
	private ProductoRepository productoRepository;
	
	//TODO precios
	@RequestMapping(value="all", method = RequestMethod.GET)
	public ResponseEntity<Page<ProductoDTO>> getAllProducts(@AuthenticationPrincipal UsuarioDTO usuarioDTO,Pageable pageable) {
		
		if (usuarioDTO.getEmpresaId() == null) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		Page<ProductoDTO> pageProductos = productoRepository.findByEmpresa_EmpresaIdAndActivo(usuarioDTO.getEmpresaId(), true, pageable)
				.map(producto -> ProductoMapper.INSTANCE.productoToProductoDTO(producto));
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
