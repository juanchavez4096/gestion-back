package com.empresa.consumo.masivo.gestion.convertor;

import com.empresa.consumo.masivo.gestion.DTO.EmpresaDTO;
import com.empresa.consumo.masivo.gestion.DTO.RegisterEmpresaDTO;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmpresaMapper {
	EmpresaMapper INSTANCE = Mappers.getMapper( EmpresaMapper.class );

	EmpresaDTO empresaToEmpresaDTO(Empresa empresa);

	@Mapping(source = "nombreEmpresa",target = "nombre")
	Empresa registerEmpresaDTOToEmpresa(RegisterEmpresaDTO registerEmpresaDTO);
}
