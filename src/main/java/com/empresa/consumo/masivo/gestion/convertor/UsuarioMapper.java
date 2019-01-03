package com.empresa.consumo.masivo.gestion.convertor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.empresa.consumo.masivo.gestion.DTO.RegisterDTO;
import com.empresa.consumo.masivo.gestion.DTO.TipoUsuarioDTO;
import com.empresa.consumo.masivo.gestion.DTO.UsuarioDTO;
import com.empresa.consumo.masivo.gestion.data.entity.TipoUsuario;
import com.empresa.consumo.masivo.gestion.data.entity.Usuario;

@Mapper
public interface UsuarioMapper {
	
	UsuarioMapper INSTANCE = Mappers.getMapper( UsuarioMapper.class );
	@Mappings({
	       @Mapping(source = "empresa.empresaId", target = "empresaId")
	   }) 
	UsuarioDTO usuarioToUsuarioDTO(Usuario usuario);
	
	TipoUsuarioDTO tipoUsuarioToTipoUsuarioDTO(TipoUsuario tipoUsuario);
	
	Usuario registerDTOToUsuario(RegisterDTO registerDTO);
}
