package pe.edu.upeu.bomerp.catalogo.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductoRequest {

    @NotBlank
    @Size(max = 120)
    @Schema(description = "Nombre comercial del producto agroindustrial", example = "Aceite de Coco Virgen 500ml")
    private String nombre;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Schema(description = "Precio unitario de venta al público en soles", example = "48.00")
    private BigDecimal precio;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Cantidad de stock inicial en unidades", example = "30")
    private Integer stock;

    @NotNull
    @Schema(description = "Identificador de la categoría padre", example = "2")
    private Long categoriaId;

    @Size(max = 1000)
    @Schema(description = "Descripción detallada del producto, propiedades y presentación", example = "Aceite de coco extra virgen 100% orgánico prensado en frío")
    private String descripcion;

    @Schema(description = "URL relativa o absoluta de la fotografía del producto o imagen en base64", example = "/assets/productos/aceite-coco-500.png")
    private String imagenUrl;
}