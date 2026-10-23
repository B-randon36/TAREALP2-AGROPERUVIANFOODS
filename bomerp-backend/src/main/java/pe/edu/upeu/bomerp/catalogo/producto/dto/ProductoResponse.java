package pe.edu.upeu.bomerp.catalogo.producto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pe.edu.upeu.bomerp.catalogo.categoria.dto.CategoriaResumen;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Schema(description = "Detalle del producto en catálogo con stock y fotografía")
public class ProductoResponse {
    @Schema(description = "Identificador único del producto", example = "4")
    private Long id;

    @Schema(description = "Nombre comercial del producto", example = "Aceite de Coco Virgen 500ml")
    private String nombre;

    @Schema(description = "Precio unitario en soles", example = "48.00")
    private BigDecimal precio;

    @Schema(description = "Stock físico actual en almacén", example = "30")
    private Integer stock;

    @Schema(description = "Descripción y propiedades del producto", example = "Aceite de coco extra virgen prensado en frío")
    private String descripcion;

    @Schema(description = "Ruta o URL de la fotografía del producto", example = "/assets/productos/aceite-coco-500.png")
    private String imagenUrl;

    @Schema(description = "Categoría a la que pertenece el producto")
    private CategoriaResumen categoria;

    public ProductoResponse() {
    }

    public ProductoResponse(Long id, String nombre, BigDecimal precio, Integer stock, String descripcion, String imagenUrl, CategoriaResumen categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
        this.categoria = categoria;
    }

    public ProductoResponse(Long id, String nombre, BigDecimal precio, Integer stock, CategoriaResumen categoria) {
        this(id, nombre, precio, stock, null, null, categoria);
    }
}