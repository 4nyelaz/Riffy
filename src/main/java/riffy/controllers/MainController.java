package riffy.controllers;

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

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        List<ProductoEntity> productos = productoRepository.findByPropietarioId(usuarioId);

        Map<Long, List<String>> imagenesMap = new HashMap<>();
        for (ProductoEntity p : productos) {
            List<String> imgs;
            if (p.getImagenes() != null && !p.getImagenes().trim().isEmpty()) {
                imgs = Arrays.asList(p.getImagenes().split(","));
            } else {
                imgs = Arrays.asList("sin_foto.png");
            }
            imagenesMap.put(p.getId_producto(), imgs);
        }

        model.addAttribute("imagenesMap", imagenesMap);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        return "home";
    }
}