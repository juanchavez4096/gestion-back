package com.empresa.consumo.masivo.gestion.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class EmpresaDTO {

    private Long empresaId;
    @NotEmpty
    private String nombre;
    private Boolean enabled;
}
