package com.empresa.consumo.masivo.gestion.convertor;

import com.empresa.consumo.masivo.gestion.DTO.MaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.ProductoDTO;
import com.empresa.consumo.masivo.gestion.DTO.ProductoMaterialDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUnidadDTO;
import com.empresa.consumo.masivo.gestion.data.entity.Material;
import com.empresa.consumo.masivo.gestion.data.entity.Producto;
import com.empresa.consumo.masivo.gestion.data.entity.ProductoMaterial;
import com.empresa.consumo.masivo.gestion.data.entity.TipoUnidad;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductoMapper {
	
	ProductoMapper INSTANCE = Mappers.getMapper( ProductoMapper.class );
	//To DTO
	@Mappings({
			@Mapping(target = "costo", ignore = true)
	   })
	ProductoDTO productoToProductoDTO(Producto producto); 
	@Mappings({
		@Mapping(target = "material", expression = "java( new MaterialDTO( productoMaterial.getMaterial().getMaterialId() ) )"),
		@Mapping(target = "tipoUnidad", expression = "java( new TipoUnidadDTO( productoMaterial.getTipoUnidad().getTipoUnidadId() ) )")
	   }) 
	ProductoMaterialDTO productoMaterialToProductoMaterialDTO(ProductoMaterial productoMaterial);
	
	@Mappings({
		@Mapping(target = "tipoUnidad", expression = "java( new TipoUnidadDTO( material.getTipoUnidad().getTipoUnidadId() ) )")
	   }) 
	MaterialDTO materialToMaterialDTO(Material material);
	TipoUnidadDTO tipoUnidadToTipoUnidadDTO(TipoUnidad tipoUnidad);
	
	//To Entity

	@Mappings({
			@Mapping(target = "activo", ignore = true),
			@Mapping(target = "empresa", ignore = true),
			@Mapping(target = "productoMaterials", ignore = true)
	})
	Material materialDTOToMaterial(MaterialDTO materialDTO);
	@Mappings({
		@Mapping(target = "tipo", ignore = true),
		@Mapping(target = "unidad", ignore = true),
		@Mapping(target = "referenciaEnGramos", ignore = true),
		@Mapping(target = "materials", ignore = true),
		@Mapping(target = "productoMaterials", ignore = true)
	   }) 
	TipoUnidad tipoUnidadDTOToTipoUnidad(TipoUnidadDTO tipoUnidadDTO);
}
