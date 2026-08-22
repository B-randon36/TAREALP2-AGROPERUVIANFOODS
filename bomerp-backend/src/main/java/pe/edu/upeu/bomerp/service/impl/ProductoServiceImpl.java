package pe.edu.upeu.bomerp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.bomerp.dto.request.ProductoRequest;
import pe.edu.upeu.bomerp.dto.response.ProductoResponse;
import pe.edu.upeu.bomerp.entity.Producto;
import pe.edu.upeu.bomerp.exception.ResourceNotFoundException;
import pe.edu.upeu.bomerp.mapper.ProductoMapper;
import pe.edu.upeu.bomerp.repository.ProductoRepository;
import pe.edu.upeu.bomerp.service.ProductoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un producto registrado con el código: " + request.getCodigo());
        }
        Producto producto = productoMapper.toEntity(request);
        Producto guardado = productoRepository.save(producto);
        return productoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        return productoMapper.toResponse(producto);
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        
        if (productoRepository.existsByCodigoAndIdNot(request.getCodigo(), id)) {
            throw new IllegalArgumentException("Ya existe otro producto registrado con el código: " + request.getCodigo());
        }

        productoMapper.updateEntityFromRequest(request, producto);
        Producto actualizado = productoRepository.save(producto);
        return productoMapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con el ID: " + id);
        }
        productoRepository.deleteById(id);
    }
}
