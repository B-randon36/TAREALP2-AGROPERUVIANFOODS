package pe.edu.upeu.bomerp.produccion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.bomerp.catalogo.producto.entity.Producto;
import pe.edu.upeu.bomerp.catalogo.producto.repository.ProductoRepository;
import pe.edu.upeu.bomerp.produccion.dto.DetalleRecetaDto;
import pe.edu.upeu.bomerp.produccion.dto.ItemRecetaRequest;
import pe.edu.upeu.bomerp.produccion.dto.RecetaBomCreateRequest;
import pe.edu.upeu.bomerp.produccion.dto.RecetaBomDto;
import pe.edu.upeu.bomerp.produccion.entity.DetalleRecetaInsumo;
import pe.edu.upeu.bomerp.produccion.entity.Insumo;
import pe.edu.upeu.bomerp.produccion.entity.RecetaBOM;
import pe.edu.upeu.bomerp.produccion.repository.InsumoRepository;
import pe.edu.upeu.bomerp.produccion.repository.RecetaBomRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecetaBomServiceImpl implements RecetaBomService {

    private final RecetaBomRepository recetaBomRepository;
    private final InsumoRepository insumoRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RecetaBomDto> listarTodas() {
        return recetaBomRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RecetaBomDto obtenerPorId(Long id) {
        return toDto(obtenerEntidadPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public RecetaBOM obtenerEntidadPorId(Long id) {
        return recetaBomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receta BOM no encontrada: " + id));
    }

    @Override
    public RecetaBomDto crear(RecetaBomCreateRequest req) {
        if (recetaBomRepository.findByCodigoReceta(req.getCodigoReceta()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe una receta con código: " + req.getCodigoReceta());
        }

        Producto producto = productoRepository.findById(req.getProductoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto terminado no encontrado: " + req.getProductoId()));

        RecetaBOM receta = RecetaBOM.builder()
                .codigoReceta(req.getCodigoReceta())
                .productoId(producto.getId())
                .productoNombre(producto.getNombre())
                .nombreVersion(req.getNombreVersion())
                .costoManoObraEstimado(req.getCostoManoObraEstimado())
                .costoTotalProduccion(BigDecimal.ZERO)
                .vigente(true)
                .build();

        BigDecimal costoInsumosAcumulado = BigDecimal.ZERO;

        for (ItemRecetaRequest item : req.getItems()) {
            Insumo insumo = insumoRepository.findById(item.getInsumoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo no encontrado: " + item.getInsumoId()));

            BigDecimal subtotalInsumo = item.getCantidadRequerida().multiply(insumo.getCostoUnitario())
                    .setScale(4, RoundingMode.HALF_UP);

            costoInsumosAcumulado = costoInsumosAcumulado.add(subtotalInsumo);

            DetalleRecetaInsumo detalle = DetalleRecetaInsumo.builder()
                    .insumo(insumo)
                    .cantidadRequerida(item.getCantidadRequerida())
                    .costoSubtotal(subtotalInsumo)
                    .build();

            receta.addDetalle(detalle);
        }

        BigDecimal costoTotal = costoInsumosAcumulado.add(req.getCostoManoObraEstimado())
                .setScale(2, RoundingMode.HALF_UP);
        receta.setCostoTotalProduccion(costoTotal);

        return toDto(recetaBomRepository.save(receta));
    }

    @Override
    public void cambiarVigencia(Long id, boolean vigente) {
        RecetaBOM receta = obtenerEntidadPorId(id);
        receta.setVigente(vigente);
        recetaBomRepository.save(receta);
    }

    private RecetaBomDto toDto(RecetaBOM r) {
        List<DetalleRecetaDto> detalles = r.getDetalles() == null ? List.of() :
                r.getDetalles().stream()
                        .map(d -> DetalleRecetaDto.builder()
                                .id(d.getId())
                                .insumoId(d.getInsumo().getId())
                                .insumoCodigo(d.getInsumo().getCodigo())
                                .insumoNombre(d.getInsumo().getNombre())
                                .unidadMedida(d.getInsumo().getUnidadMedida())
                                .cantidadRequerida(d.getCantidadRequerida())
                                .costoUnitarioInsumo(d.getInsumo().getCostoUnitario())
                                .costoSubtotal(d.getCostoSubtotal())
                                .build())
                        .toList();

        return RecetaBomDto.builder()
                .id(r.getId())
                .codigoReceta(r.getCodigoReceta())
                .productoId(r.getProductoId())
                .productoNombre(r.getProductoNombre())
                .nombreVersion(r.getNombreVersion())
                .costoManoObraEstimado(r.getCostoManoObraEstimado())
                .costoTotalProduccion(r.getCostoTotalProduccion())
                .vigente(r.getVigente())
                .detalles(detalles)
                .build();
    }
}
