package com.empresa.consumo.masivo.gestion.data.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "producto_history", schema = "gestion")
@Getter
@Setter
@NoArgsConstructor
public class ProductoHistory {

    @Id
    @SequenceGenerator(name="pk_sequence_producto_history",sequenceName="gestion.producto_history_id_sequence", allocationSize=1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_sequence_producto_history")
    @Column(name = "producto_history_id", nullable = false)
    private Long productoHistoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "precio_venta", nullable = false)
    private Double precioVenta;
}
