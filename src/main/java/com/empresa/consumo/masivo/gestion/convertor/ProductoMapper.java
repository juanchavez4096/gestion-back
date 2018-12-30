package com.empresa.consumo.masivo.gestion.convertor;



import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.empresa.consumo.masivo.gestion.DTO.ProductoDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUsuarioDTO;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.data.entity.Producto;
import com.empresa.consumo.masivo.gestion.data.entity.TipoUsuario;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;

@Mapper
public interface ProductoMapper {
	
	ProductoMapper INSTANCE = Mappers.getMapper( ProductoMapper.class );
	@Mappings({
	       @Mapping(source = "empresa.empresaId", target = "empresaId")
	   }) 
	ProductoDTO productoToProductoDTO(Producto producto);
}
