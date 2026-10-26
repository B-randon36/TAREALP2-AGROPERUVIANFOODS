package pe.edu.upeu.bomerp.produccion.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.bomerp.produccion.dto.InsumoCreateRequest;
import pe.edu.upeu.bomerp.produccion.dto.InsumoDto;
import pe.edu.upeu.bomerp.produccion.entity.Insumo;
import pe.edu.upeu.bomerp.produccion.repository.InsumoRepository;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InsumoServiceImpl implements InsumoService {

    private final InsumoRepository insumoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InsumoDto> listarTodos() {
        return insumoRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InsumoDto obtenerPorId(Long id) {
        return toDto(obtenerEntidadPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Insumo obtenerEntidadPorId(Long id) {
        return insumoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Insumo no encontrado: " + id));
    }

    @Override
    public InsumoDto crear(InsumoCreateRequest req) {
        if (insumoRepository.findByCodigo(req.getCodigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un insumo con el código: " + req.getCodigo());
        }

        Insumo insumo = Insumo.builder()
                .codigo(req.getCodigo())
                .nombre(req.getNombre())
                .unidadMedida(req.getUnidadMedida())
                .costoUnitario(req.getCostoUnitario())
                .stockActual(req.getStockActual())
                .stockMinimo(req.getStockMinimo())
                .activo(true)
                .build();

        return toDto(insumoRepository.save(insumo));
    }

    @Override
    public InsumoDto actualizar(Long id, InsumoCreateRequest req) {
        Insumo insumo = obtenerEntidadPorId(id);

        if (!insumo.getCodigo().equalsIgnoreCase(req.getCodigo()) &&
                insumoRepository.findByCodigo(req.getCodigo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un insumo con el código: " + req.getCodigo());
        }

        insumo.setCodigo(req.getCodigo());
        insumo.setNombre(req.getNombre());
        insumo.setUnidadMedida(req.getUnidadMedida());
        insumo.setCostoUnitario(req.getCostoUnitario());
        insumo.setStockActual(req.getStockActual());
        insumo.setStockMinimo(req.getStockMinimo());

        return toDto(insumoRepository.save(insumo));
    }

    @Override
    public void eliminar(Long id) {
        Insumo insumo = obtenerEntidadPorId(id);
        insumo.setActivo(false);
        insumoRepository.save(insumo);
    }

    @Override
    public void actualizarStock(Long id, BigDecimal deltaCantidad, BigDecimal nuevoCostoPonderado) {
        Insumo insumo = obtenerEntidadPorId(id);
        BigDecimal nuevoStock = insumo.getStockActual().add(deltaCantidad);
        if (nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Stock insuficiente para el insumo '" + insumo.getNombre() + "'. Stock actual: " + insumo.getStockActual());
        }
        insumo.setStockActual(nuevoStock);
        if (nuevoCostoPonderado != null && nuevoCostoPonderado.compareTo(BigDecimal.ZERO) > 0) {
            insumo.setCostoUnitario(nuevoCostoPonderado);
        }
        insumoRepository.save(insumo);
    }

    private InsumoDto toDto(Insumo i) {
        return InsumoDto.builder()
                .id(i.getId())
                .codigo(i.getCodigo())
                .nombre(i.getNombre())
                .unidadMedida(i.getUnidadMedida())
                .costoUnitario(i.getCostoUnitario())
                .stockActual(i.getStockActual())
                .stockMinimo(i.getStockMinimo())
                .activo(i.getActivo())
                .build();
    }
}
