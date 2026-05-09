package riffy.controllers;

import java.util.List;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import riffy.model.ProductoEntity;
import riffy.repository.ProductoRepository;

@ControllerAdvice
public class NavbarAdvice {

    @Autowired
    private ProductoRepository productoRepository;

    @ModelAttribute("pedidosPendientes")
    public List<ProductoEntity> pedidosPendientes(HttpSession session) {
        Long usuarioId =  (Long) session.getAttribute("usuarioId");
        if (usuarioId = = null) return List.of();
        return productoRepository.findByPropietarioIdAndEstado(usuarioId, "Reservado");
    }
}