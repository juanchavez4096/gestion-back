package com.empresa.consumo.masivo.gestion.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empresa.consumo.masivo.gestion.DTO.ProductoDTO;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;
import com.empresa.consumo.masivo.gestion.data.repository.ProductoRepository;

@RestController
@RequestMapping("api/producto")
public class ProductoController {
	
	//Logger log = LogManager.getLogger();
	
	@Autowired
	private ProductoRepository productoRepository;
	
	@RequestMapping(value="all", method = RequestMethod.GET)
	public ResponseEntity<Page<ProductoDTO>> getAllProducts(@RequestParam(name = "empresaId") Long empresaId,Pageable pageable) {
		
		Page<ProductoDTO> pageProducts = productoRepository.findByEmpresa_EmpresaId(empresaId,pageable)
				.map(producto -> ProductoMapper.INSTANCE.productoToProductoDTO(producto));
		return new ResponseEntity<>(pageProducts, HttpStatus.OK);
	}
	
	
}
