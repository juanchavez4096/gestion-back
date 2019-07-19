package com.empresa.consumo.masivo.gestion.convertor;

import com.empresa.consumo.masivo.gestion.DTO.*;
import com.empresa.consumo.masivo.gestion.data.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductoMapper {

	ProductoMapper INSTANCE = Mappers.getMapper( ProductoMapper.class );
	//To DTO
	@Mappings({
			@Mapping(target = "costoProduccion", ignore = true),
			@Mapping(target = "creadoPor", expression = "java( this.usuarioToUsuarioDTOForDisplay(producto.getCreadoPor()) )"),
			@Mapping(target = "actualizadoPor", expression = "java( this.usuarioToUsuarioDTOForDisplay(producto.getActualizadoPor()) )")
	})
	ProductoDTO productoToProductoDTO(Producto producto);
	@Mappings({
			@Mapping(target = "material", expression = "java( new MaterialDTO( productoMaterial.getMaterial().getMaterialId() ) )"),
			@Mapping(target = "tipoUnidad", expression = "java( new TipoUnidadDTO( productoMaterial.getTipoUnidad().getTipoUnidadId() ) )")
	})
	ProductoMaterialDTO productoMaterialToProductoMaterialDTO(ProductoMaterial productoMaterial);

	@Mappings({
			@Mapping(target = "tipoUnidad", expression = "java( new TipoUnidadDTO( material.getTipoUnidad().getTipoUnidadId() ) )"),
			@Mapping(target = "creadoPor", expression = "java( this.usuarioToUsuarioDTOForDisplay(material.getCreadoPor()) )"),
			@Mapping(target = "actualizadoPor", expression = "java( this.usuarioToUsuarioDTOForDisplay(material.getActualizadoPor()) )")
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


	ProductoHistoryDTO productoHistoryToProductoHistoryDTO(ProductoHistory productoHistory);

	@Mappings({
			@Mapping(source = "empresa.empresaId", target = "empresaId")
	})
	UsuarioDTO usuarioToUsuarioDTO(Usuario usuario);

	@Mappings({
			@Mapping(target = "empresaId", ignore = true),
			@Mapping(target = "password", ignore = true)
	})
	UsuarioDTO usuarioToUsuarioDTOForDisplay(Usuario usuario);

	@Mappings({
			@Mapping(target = "empresa.empresaId", source = "empresaId")
	})
	Usuario usuarioDTOToUsuario(UsuarioDTO usuarioDTO);

	TipoUsuarioDTO tipoUsuarioToTipoUsuarioDTO(TipoUsuario tipoUsuario);

	Usuario registerDTOToUsuario(RegisterDTO registerDTO);

	EmpresaDTO empresaToEmpresaDTO(Empresa empresa);

	@Mapping(source = "nombreEmpresa",target = "nombre")
	Empresa registerEmpresaDTOToEmpresa(RegisterEmpresaDTO registerEmpresaDTO);

}
