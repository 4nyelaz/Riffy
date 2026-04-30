package riffy.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import riffy.model.ProductoEntity;
import riffy.repository.ProductoRepository;

@Controller
public class MainController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @SuppressWarnings({ "unchecked", "null" })
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        // --- VISTOS RECIENTEMENTE (desde sesión) ---
        List<Long> historialIds = (List<Long>) session.getAttribute("historialIds");
        if (historialIds == null)
            historialIds = new ArrayList<>();

        List<ProductoEntity> vistos = new ArrayList<>();
        for (Long id : historialIds) {
            productoRepository.findById(id).ifPresent(vistos::add);
        }

        // --- PRODUCTOS POR CATEGORÍA (excluyendo los propios) ---
        List<ProductoEntity> vinilos = productoRepository.findByCategoriaAndPropietarioIdUsuarioNot("Vinilo", usuarioId);
        List<ProductoEntity> cds = productoRepository.findByCategoriaAndPropietarioIdUsuarioNot("CD", usuarioId);

        // --- IMAGENES MAP (vistos + vinilos + cds) ---
        List<ProductoEntity> todos = new ArrayList<>();
        todos.addAll(vistos);
        todos.addAll(vinilos);
        todos.addAll(cds);

        Map<Long, List<String>> imagenesMap = new HashMap<>();
        for (ProductoEntity p : todos) {
            if (imagenesMap.containsKey(p.getId_producto()))
                continue;
            List<String> imgs;
            if (p.getImagenes() != null && !p.getImagenes().trim().isEmpty()) {
                imgs = Arrays.asList(p.getImagenes().split(","));
            } else {
                imgs = Arrays.asList("sin_foto.png");
            }
            imagenesMap.put(p.getId_producto(), imgs);
        }

        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        model.addAttribute("vistos", vistos);
        model.addAttribute("vinilos", vinilos);
        model.addAttribute("cds", cds);
        model.addAttribute("imagenesMap", imagenesMap);

        return "home";
    }
}