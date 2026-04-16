package riffy.services;

import java.util.List;

import org.springframework.stereotype.Service;

import riffy.entity.ProductoEntity;
import riffy.repository.ProductoRepository;

@Service
public class ProductoService {
    private final ProductoRepository productoRepositorio;

    public ProductoService(ProductoRepository productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    public List<ProductoEntity> findByPropietarioId(Long propietarioId) {
        return productoRepositorio.findByPropietarioId(propietarioId);
    }

    public List<ProductoEntity> findAll() {
        return productoRepositorio.findAll();
    }

}