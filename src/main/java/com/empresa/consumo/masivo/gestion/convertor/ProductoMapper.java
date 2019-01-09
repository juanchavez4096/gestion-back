package com.empresa.consumo.masivo.gestion.convertor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.empresa.consumo.masivo.gestion.DTO.MaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.ProductoDTO;
import com.empresa.consumo.masivo.gestion.DTO.ProductoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUnidadDTO;
import com.empresa.consumo.masivo.gestion.data.entity.Material;
import com.empresa.consumo.masivo.gestion.data.entity.Producto;
import com.empresa.consumo.masivo.gestion.data.entity.ProductoMaterial;
import com.empresa.consumo.masivo.gestion.data.entity.TipoMaterial;
import com.empresa.consumo.masivo.gestion.data.entity.TipoUnidad;

@Mapper
public interface ProductoMapper {
	
	ProductoMapper INSTANCE = Mappers.getMapper( ProductoMapper.class );
	
	//To DTO
	@Mappings({
	   }) 
	ProductoDTO productoToProductoDTO(Producto producto); 
	@Mappings({
		@Mapping(target = "material", expression = "java( new MaterialDTO( productoMaterial.getMaterial().getMaterialId() ) )"),
		@Mapping(target = "tipoUnidad", expression = "java( new TipoUnidadDTO( productoMaterial.getTipoUnidad().getTipoUnidadId() ) )")
	   }) 
	ProductoMaterialDTO productoMaterialToProductoMaterialDTO(ProductoMaterial productoMaterial);
	
	@Mappings({
		@Mapping(target = "tipoMaterial", expression = "java( new TipoMaterialDTO( material.getTipoMaterial().getTipoMaterialId() ) )"),
		@Mapping(target = "tipoUnidad", expression = "java( new TipoUnidadDTO( material.getTipoUnidad().getTipoUnidadId() ) )")
	   }) 
	MaterialDTO materialToMaterialDTO(Material material);
	TipoUnidadDTO tipoUnidadToTipoUnidadDTO(TipoUnidad tipoUnidad);
	
	TipoMaterialDTO tipoMaterialToTipoMaterialDTO(TipoMaterial tipoMaterial);
	
	//To Entity
	
	Material materialDTOToMaterial(MaterialDTO materialDTO);
	@Mappings({
		@Mapping(target = "tipo", ignore = true),
		@Mapping(target = "unidad", ignore = true),
		@Mapping(target = "referenciaEnGramos", ignore = true),
		@Mapping(target = "materials", ignore = true),
		@Mapping(target = "productoMaterials", ignore = true)
	   }) 
	TipoUnidad tipoUnidadDTOToTipoUnidad(TipoUnidadDTO tipoUnidadDTO);
	TipoMaterial tipoMaterialDTOToTipoMaterial(TipoMaterialDTO tipoMaterialDTO);
}
