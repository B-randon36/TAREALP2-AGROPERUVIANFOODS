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
@Configuration
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
            log.info("Inicializando catálogo demo (Categorías y Productos)...");

            Categoria cat1 = new Categoria();
            cat1.setNombre("Derivados de Cacao");
            cat1.setDescripcion("Pasta pura, nibs tostados, manteca y licor de cacao orgánico");
            categoriaRepository.save(cat1);

            Categoria cat2 = new Categoria();
            cat2.setNombre("Aceites Prensados");
            cat2.setDescripcion("Aceite de coco extra virgen prensado en frío comestible y cosmético");
            categoriaRepository.save(cat2);

            Categoria cat3 = new Categoria();
            cat3.setNombre("Empaques e Insumos");
            cat3.setDescripcion("Envases de vidrio, tapas inviolables y etiquetas para frascos");
            categoriaRepository.save(cat3);

            Producto p1 = new Producto();
            p1.setNombre("Pasta Pura de Cacao 500g");
            p1.setPrecio(new BigDecimal("32.00"));
            p1.setStock(40);
            p1.setCategoria(cat1);
            productoRepository.save(p1);

            Producto p2 = new Producto();
            p2.setNombre("Nibs de Cacao Horneados 200g");
            p2.setPrecio(new BigDecimal("18.50"));
            p2.setStock(65);
            p2.setCategoria(cat1);
            productoRepository.save(p2);

            Producto p3 = new Producto();
            p3.setNombre("Aceite de Coco Virgen 250ml");
            p3.setPrecio(new BigDecimal("28.00"));
            p3.setStock(50);
            p3.setCategoria(cat2);
            productoRepository.save(p3);

            Producto p4 = new Producto();
            p4.setNombre("Aceite de Coco Virgen 500ml");
            p4.setPrecio(new BigDecimal("48.00"));
            p4.setStock(30);
            p4.setCategoria(cat2);
            productoRepository.save(p4);

            Producto p5 = new Producto();
            p5.setNombre("Frasco de Vidrio Hexagonal 250ml");
            p5.setPrecio(new BigDecimal("2.50"));
            p5.setStock(200);
            p5.setCategoria(cat3);
            productoRepository.save(p5);

            log.info("Catálogo demo inicializado con 3 categorías y 5 productos.");
        }
    }
}
