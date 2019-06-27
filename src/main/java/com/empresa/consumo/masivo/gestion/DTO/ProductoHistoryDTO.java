package com.empresa.consumo.masivo.gestion.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProductoHistoryDTO {
    private Long productoHistoryId;
    private LocalDateTime fechaCreacion;
    private Double precioVenta;
}
