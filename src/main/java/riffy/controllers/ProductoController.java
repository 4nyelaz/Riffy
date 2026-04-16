package riffy.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import riffy.entity.ProductoEntity;
import riffy.repository.ProductoRepository;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        //     // CONTAR TODOS LOS PRODUCTOS
        // long totalProductos = productoRepository.count();
        // System.out.println("📊 TOTAL de productos en BD: " + totalProductos);
        

        // // LISTAR TODOS LOS PRODUCTOS
        // List<ProductoEntity> todos = productoRepository.findAll();
        // System.out.println("📋 LISTA COMPLETA:");
        // for (ProductoEntity p : todos) {
        //     System.out.println("   - ID: " + p.getId_producto() + " | Título: " + p.getTitulo() + " | Propietario: " + p.getPropietario().getIdUsuario());
        // }

        if (usuarioId == null) {
            return "redirect:/login";
        }

        List<ProductoEntity> productos = productoRepository.findByPropietarioId(usuarioId);
        model.addAttribute("productos", productos);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

        return "home";
    }
}