package pe.edu.upeu.bomerp.catalogo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import pe.edu.upeu.bomerp.catalogo.categoria.entity.Categoria;
import pe.edu.upeu.bomerp.catalogo.categoria.repository.CategoriaRepository;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import pe.edu.upeu.bomerp.catalogo.producto.repository.ProductoRepository;

import java.math.BigDecimal;

@Slf4j
@Configuration(proxyBeanMethods = false)
@org.springframework.core.annotation.Order(1)
@ConditionalOnProperty(name = "app.init.demo-data", havingValue = "true", matchIfMissing = true)
public class CatalogoDataInitializer implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public CatalogoDataInitializer(
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() == 0) {
            log.info("Inicializando catálogo real Agro Peruvian Foods con productos de productosimagenes...");

            Categoria cat1 = new Categoria();
            cat1.setNombre("Aceites Prensados de Coco");
            cat1.setDescripcion("Aceite de coco extra virgen 100% natural prensado en frío comestible y cosmético");
            categoriaRepository.save(cat1);

            Categoria cat2 = new Categoria();
            cat2.setNombre("Granos y Derivados de Cacao");
            cat2.setDescripcion("Cacao grano entero sin tostar de aroma intenso y derivados 100% naturales");
            categoriaRepository.save(cat2);

            Categoria cat3 = new Categoria();
            cat3.setNombre("Subproductos de Coco");
            cat3.setDescripcion("Coco rallado en hebra larga con grasa natural sin conservantes ni aditivos");
            categoriaRepository.save(cat3);

            Categoria cat4 = new Categoria();
            cat4.setNombre("Ventas Mayoristas y Bandejas");
            cat4.setDescripcion("Bandejas selladas con precios especiales por mayor para distribuidores");
            categoriaRepository.save(cat4);

            // 1. Aceite de Coco 30ml
            Producto p1 = new Producto();
            p1.setNombre("Aceite de Coco Virgen 30ml");
            p1.setPrecio(new BigDecimal("5.00"));
            p1.setStock(50);
            p1.setDescripcion("Prensado en frío, infaltable 100% natural sin preservantes ni aditivos.");
            p1.setImagenUrl("/assets/productos/aceite-coco-30ml.jpeg");
            p1.setCategoria(cat1);
            productoRepository.save(p1);

            // 2. Aceite de Coco 120ml
            Producto p2 = new Producto();
            p2.setNombre("Aceite de Coco Virgen 120ml");
            p2.setPrecio(new BigDecimal("12.00"));
            p2.setStock(60);
            p2.setDescripcion("Prensado en frío, esencial, 100% natural sin preservantes ni aditivos.");
            p2.setImagenUrl("/assets/productos/aceite-coco-120ml.jpeg");
            p2.setCategoria(cat1);
            productoRepository.save(p2);

            // 3. Aceite de Coco 250ml
            Producto p3 = new Producto();
            p3.setNombre("Aceite de Coco Virgen 250ml");
            p3.setPrecio(new BigDecimal("20.00"));
            p3.setStock(70);
            p3.setDescripcion("Prensado en frío, rutina diaria, 100% natural sin preservantes ni aditivos.");
            p3.setImagenUrl("/assets/productos/aceite-coco-250ml.jpeg");
            p3.setCategoria(cat1);
            productoRepository.save(p3);

            // 4. Aceite de Coco 460ml
            Producto p4 = new Producto();
            p4.setNombre("Aceite de Coco Virgen 460ml");
            p4.setPrecio(new BigDecimal("35.00"));
            p4.setStock(80);
            p4.setDescripcion("Prensado en frío, rinde más, 100% natural sin preservantes ni aditivos.");
            p4.setImagenUrl("/assets/productos/aceite-coco-460ml.jpeg");
            p4.setCategoria(cat1);
            productoRepository.save(p4);

            // 5. Aceite de Coco 1 Litro
            Producto p5 = new Producto();
            p5.setNombre("Aceite de Coco Virgen 1L");
            p5.setPrecio(new BigDecimal("65.00"));
            p5.setStock(40);
            p5.setDescripcion("Presentación familiar prensado en frío, 100% natural sin preservantes ni aditivos.");
            p5.setImagenUrl("/assets/productos/aceite-coco-1L.jpeg");
            p5.setCategoria(cat1);
            productoRepository.save(p5);

            // 6. Balde 4L
            Producto p6 = new Producto();
            p6.setNombre("Aceite de Coco Virgen Balde de 4L");
            p6.setPrecio(new BigDecimal("200.00"));
            p6.setStock(15);
            p6.setDescripcion("Para hogares y emprendimientos, 100% natural prensado en frío.");
            p6.setImagenUrl("/assets/productos/aceite-coco-balde-4L.jpeg");
            p6.setCategoria(cat1);
            productoRepository.save(p6);

            // 7. Balde 20L
            Producto p7 = new Producto();
            p7.setNombre("Aceite de Coco Virgen Balde de 20L");
            p7.setPrecio(new BigDecimal("730.00"));
            p7.setStock(10);
            p7.setDescripcion("Presentación industrial para negocios y restaurantes, 100% natural.");
            p7.setImagenUrl("/assets/productos/aceite-coco-balde-20L.jpeg");
            p7.setCategoria(cat1);
            productoRepository.save(p7);

            // 8. Cacao Grano Entero 500g
            Producto p8 = new Producto();
            p8.setNombre("Cacao Grano Entero 500g");
            p8.setPrecio(new BigDecimal("14.00"));
            p8.setStock(90);
            p8.setDescripcion("Sin tostar, aroma intenso, 100% natural calidad seleccionada.");
            p8.setImagenUrl("/assets/productos/cacao-grano-500g.jpeg");
            p8.setCategoria(cat2);
            productoRepository.save(p8);

            // 9. Cacao Grano Entero 1 Kg
            Producto p9 = new Producto();
            p9.setNombre("Cacao Grano Entero 1kg");
            p9.setPrecio(new BigDecimal("28.00"));
            p9.setStock(60);
            p9.setDescripcion("Grano entero de 1 kg sin tostar, aroma intenso, 100% natural calidad seleccionada.");
            p9.setImagenUrl("/assets/productos/cacao-grano-1kg.jpeg");
            p9.setCategoria(cat2);
            productoRepository.save(p9);

            // 10. Coco Rallado 250g
            Producto p10 = new Producto();
            p10.setNombre("Coco Rallado Doypack 250g");
            p10.setPrecio(new BigDecimal("8.00"));
            p10.setStock(75);
            p10.setDescripcion("Hebra larga con grasa natural, 100% natural de excelente calidad.");
            p10.setImagenUrl("/assets/productos/coco-rallado-250g.jpeg");
            p10.setCategoria(cat3);
            productoRepository.save(p10);

            // 11. Coco Rallado 500g
            Producto p11 = new Producto();
            p11.setNombre("Coco Rallado Doypack 500g");
            p11.setPrecio(new BigDecimal("16.00"));
            p11.setStock(55);
            p11.setDescripcion("Hebra larga con grasa natural, presentación de 500g, 100% natural de excelente calidad.");
            p11.setImagenUrl("/assets/productos/coco-rallado-500g.jpeg");
            p11.setCategoria(cat3);
            productoRepository.save(p11);

            // 12. Bandeja 120ml
            Producto p12 = new Producto();
            p12.setNombre("Bandeja Aceite Coco 120ml x 36 unid");
            p12.setPrecio(new BigDecimal("380.00"));
            p12.setStock(20);
            p12.setDescripcion("Bandeja de 36 unidades, precio especial por mayor distribuidor.");
            p12.setImagenUrl("/assets/productos/bandeja-aceite-coco-120ml.jpeg");
            p12.setCategoria(cat4);
            productoRepository.save(p12);

            // 13. Bandeja 460ml
            Producto p13 = new Producto();
            p13.setNombre("Bandeja Aceite Coco 460ml x 24 unid");
            p13.setPrecio(new BigDecimal("680.00"));
            p13.setStock(20);
            p13.setDescripcion("Bandeja de 24 unidades, precio especial por mayor distribuidor.");
            p13.setImagenUrl("/assets/productos/bandeja-aceite-coco-460ml.jpeg");
            p13.setCategoria(cat4);
            productoRepository.save(p13);

            // 14. Bandeja 1L
            Producto p14 = new Producto();
            p14.setNombre("Bandeja Aceite Coco 1L x 25 unid");
            p14.setPrecio(new BigDecimal("1350.00"));
            p14.setStock(15);
            p14.setDescripcion("Bandeja de 25 unidades, precio especial por mayor distribuidor.");
            p14.setImagenUrl("/assets/productos/bandeja-aceite-coco-1L.jpeg");
            p14.setCategoria(cat4);
            productoRepository.save(p14);

            // 15. Bandeja 30ml
            Producto p15 = new Producto();
            p15.setNombre("Bandeja Aceite Coco 30ml x 36 unid");
            p15.setPrecio(new BigDecimal("150.00"));
            p15.setStock(25);
            p15.setDescripcion("Bandeja de 36 unidades, precio especial por mayor distribuidor.");
            p15.setImagenUrl("/assets/productos/bandeja-aceite-coco-30ml.jpeg");
            p15.setCategoria(cat4);
            productoRepository.save(p15);

            log.info("Catálogo Agro Peruvian Foods inicializado con 4 categorías y 15 productos reales.");
        }
    }
}
