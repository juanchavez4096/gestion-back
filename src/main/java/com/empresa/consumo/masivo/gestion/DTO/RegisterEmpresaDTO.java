package com.empresa.consumo.masivo.gestion.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class RegisterEmpresaDTO {

    @NotEmpty
    private String nombreEmpresa;
    @NotEmpty
    private String nombre;
    @NotEmpty
    @Email
    private String email;
}
