package com.empresa.consumo.masivo.gestion.controller;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.Min;

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
import com.empresa.consumo.masivo.gestion.DTO.TipoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUnidadDTO;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.convertor.ProductoMapper;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.entity.Material;
import com.empresa.consumo.masivo.gestion.data.repository.MaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.TipoMaterialRepository;
import com.empresa.consumo.masivo.gestion.data.repository.TipoUnidadRepository;

@RestController
@RequestMapping("api/materiales")
public class MaterialController {
	
	//Logger log = LogManager.getLogger();
	
	@Autowired
	private MaterialRepository materialRepository;
	@Autowired
	private TipoMaterialRepository tipoMaterialRepository;
	@Autowired
	private TipoUnidadRepository tipoUnidadRepository;
	
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
	
	
}
