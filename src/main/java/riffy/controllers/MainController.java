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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import riffy.model.ProductoEntity;
import riffy.repository.ProductoRepository;

@Controller
public class MainController {

    // ---------------------------------------------------------------
    // solo se puede usar aquí en este controller, y no es reasignable
    @Autowired
    private ProductoRepository productoRepository;
    // ---------------------------------------------------------------

    /**
     * página principal de la aplicación
     * @return redirige a index -> pagina sin login
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * home del usuario después de iniciar sesión
     * muestra productos vistos recientemente y productos por categoría
     * @param session sesión HTTP del usuario actual
     * @param model modelo para pasar atributos a la vista
     * @return Todo correcto: redirección al home; Error: login
     */
    @SuppressWarnings({ "unchecked", "null" })
    @GetMapping("/home")
    public String home(HttpSession session, Model model, RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        // Vistos recientemente -> Sesión
        // recupera historial de id de productos vistos en sesión
        // si no existe -> nueva lista
        List<Long> historialIds = (List<Long>) session.getAttribute("historialIds");
        if (historialIds == null) {
            historialIds = new ArrayList<>();
        }

        // los id del historial los convierte en un obj producto
        // coge cada id, y si existe, lo mete a la lista
        // también comprueba que el producto no sea del usuario que esté en sesión
        List<ProductoEntity> vistos = new ArrayList<>();
        for (Long id : historialIds) {
            productoRepository.findById(id).ifPresent(p -> {
                if (!p.getPropietario().getIdUsuario().equals(usuarioId)) {
                    vistos.add(p);
                }
            });
        }

        // busca productos de categoría vinilo que no sean del usuario actual
        // el usuario en sesion no le salen sus productos en esa sección
        List<ProductoEntity> vinilos = productoRepository.findByCategoriaAndPropietarioIdUsuarioNot("Vinilo",
                usuarioId);

        // busca productos de categoría cd que no sean del usuario actual
        // el usuario en sesion no le salen sus productos en esa sección
        List<ProductoEntity> cds = productoRepository.findByCategoriaAndPropietarioIdUsuarioNot("CD", usuarioId);

        // reunir todos las listas para mostrarlas en home
        List<ProductoEntity> todos = new ArrayList<>();
        todos.addAll(vistos);
        todos.addAll(vinilos);
        todos.addAll(cds);

        // crea un mapa que asocia cada producto con su lista de imágenes
        Map<Long, List<String>> imagenesMap = new HashMap<>();

        for (ProductoEntity p : todos) {
            // procesa si el id no está ya en el mapa
            if (!imagenesMap.containsKey(p.getId_producto())) {
                // sólo si es un producto nuevo
                List<String> imgs;
                if (p.getImagenes() != null && !p.getImagenes().trim().isEmpty()) {
                    imgs = Arrays.asList(p.getImagenes().split(","));
                } else {
                    imgs = Arrays.asList("sin_foto.png");
                }
                imagenesMap.put(p.getId_producto(), imgs);
            }
            // si el id ya existe -> pasa al siguiente
        }

        // añade atributos para mostrarlos en la vista
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        model.addAttribute("vistos", vistos);
        model.addAttribute("vinilos", vinilos);
        model.addAttribute("cds", cds);
        model.addAttribute("imagenesMap", imagenesMap);
        model.addAttribute("mostrarBuscador", true);

        return "home";
    }
}