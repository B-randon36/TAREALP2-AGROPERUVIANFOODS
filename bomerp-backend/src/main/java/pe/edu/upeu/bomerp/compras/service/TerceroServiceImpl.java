package pe.edu.upeu.bomerp.compras.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pe.edu.upeu.bomerp.compras.dto.TerceroCreateRequest;
import pe.edu.upeu.bomerp.compras.dto.TerceroDto;
import pe.edu.upeu.bomerp.compras.entity.Tercero;
import pe.edu.upeu.bomerp.compras.entity.TipoTercero;
import pe.edu.upeu.bomerp.compras.repository.TerceroRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TerceroServiceImpl implements TerceroService {

    private final TerceroRepository terceroRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TerceroDto> listarTodos() {
        return terceroRepository.findByActivoTrue().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TerceroDto> listarPorTipo(TipoTercero tipo) {
        List<TipoTercero> tipos = tipo == TipoTercero.AMBOS ? List.of(TipoTercero.CLIENTE, TipoTercero.PROVEEDOR, TipoTercero.AMBOS) :
                List.of(tipo, TipoTercero.AMBOS);
        return terceroRepository.findByTipoTerceroInAndActivoTrue(tipos).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TerceroDto obtenerPorId(Long id) {
        return toDto(obtenerEntidadPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Tercero obtenerEntidadPorId(Long id) {
        return terceroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tercero no encontrado: " + id));
    }

    @Override
    public TerceroDto crear(TerceroCreateRequest req) {
        if (terceroRepository.findByNumeroDocumento(req.getNumeroDocumento()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un tercero con el documento: " + req.getNumeroDocumento());
        }

        Tercero tercero = Tercero.builder()
                .tipoDocumento(req.getTipoDocumento().toUpperCase())
                .numeroDocumento(req.getNumeroDocumento())
                .razonSocial(req.getRazonSocial())
                .tipoTercero(req.getTipoTercero())
                .telefono(req.getTelefono())
                .direccion(req.getDireccion())
                .correo(req.getCorreo())
                .activo(true)
                .build();

        return toDto(terceroRepository.save(tercero));
    }

    @Override
    public TerceroDto actualizar(Long id, TerceroCreateRequest req) {
        Tercero tercero = obtenerEntidadPorId(id);

        if (!tercero.getNumeroDocumento().equalsIgnoreCase(req.getNumeroDocumento()) &&
                terceroRepository.findByNumeroDocumento(req.getNumeroDocumento()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya existe un tercero con el documento: " + req.getNumeroDocumento());
        }

        tercero.setTipoDocumento(req.getTipoDocumento().toUpperCase());
        tercero.setNumeroDocumento(req.getNumeroDocumento());
        tercero.setRazonSocial(req.getRazonSocial());
        tercero.setTipoTercero(req.getTipoTercero());
        tercero.setTelefono(req.getTelefono());
        tercero.setDireccion(req.getDireccion());
        tercero.setCorreo(req.getCorreo());

        return toDto(terceroRepository.save(tercero));
    }

    @Override
    public void eliminar(Long id) {
        Tercero tercero = obtenerEntidadPorId(id);
        tercero.setActivo(false);
        terceroRepository.save(tercero);
    }

    private TerceroDto toDto(Tercero t) {
        return TerceroDto.builder()
                .id(t.getId())
                .tipoDocumento(t.getTipoDocumento())
                .numeroDocumento(t.getNumeroDocumento())
                .razonSocial(t.getRazonSocial())
                .tipoTercero(t.getTipoTercero())
                .telefono(t.getTelefono())
                .direccion(t.getDireccion())
                .correo(t.getCorreo())
                .activo(t.getActivo())
                .build();
    }
}
