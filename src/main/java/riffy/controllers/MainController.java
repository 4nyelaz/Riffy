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

    // solo se puede usar aquí en este controller, y no es reasignable
    @Autowired
    private ProductoRepository productoRepository;

    /**
     * página principal de la aplicación (landing page)
     * @return vista index: página de bienvenida sin log in
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * home del usuario después de iniciar sesión
     * muestra productos vistos recientemente y productos por categoría
     * @param session sesión HTTP del usuario actual (guarda historial de productos vistos)
     * @param model   modelo para pasar atributos a la vista
     * @return vista home si el usuario está logueado, o redirección al login si no
     */
    @SuppressWarnings({ "unchecked", "null" })
    @GetMapping("/home")
    public String home(HttpSession session, Model model, RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null){
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------ 

        // --- VISTOS RECIENTEMENTE (desde sesión) ---
        // ------------------------------------------------------------------
        // recupera el historial de IDs de productos vistos desde la sesión
        // el historial se guarda como una lista de Long en la sesión
        // si no existe, crea una nueva lista vacía
        // ------------------------------------------------------------------
        List<Long> historialIds = (List<Long>) session.getAttribute("historialIds");
        if (historialIds == null)
            historialIds = new ArrayList<>();

        // ------------------------------------------------------------------
        // convierte los IDs del historial en objetos ProductoEntity
        // para cada ID, busca el producto en la BD y si existe lo añade a la lista
        // ------------------------------------------------------------------
        List<ProductoEntity> vistos = new ArrayList<>();
        for (Long id : historialIds) {
            productoRepository.findById(id).ifPresent(vistos::add);
        }

        // --- PRODUCTOS POR CATEGORÍA (excluyendo los propios) ---
        // ------------------------------------------------------------------
        // busca productos de categoría "Vinilo" que NO sean del usuario actual
        // así el usuario no ve sus propios productos en el home
        // ------------------------------------------------------------------
        List<ProductoEntity> vinilos = productoRepository.findByCategoriaAndPropietarioIdUsuarioNot("Vinilo",
                usuarioId);

        // ------------------------------------------------------------------
        // busca productos de categoría "CD" que NO sean del usuario actual
        // ------------------------------------------------------------------
        List<ProductoEntity> cds = productoRepository.findByCategoriaAndPropietarioIdUsuarioNot("CD", usuarioId);

        // --- IMAGENES MAP (vistos + vinilos + cds) ---
        // ------------------------------------------------------------------
        // agrupa todos los productos que se van a mostrar en el home
        // (vistos recientemente + vinilos + CDs)
        // ------------------------------------------------------------------
        List<ProductoEntity> todos = new ArrayList<>();
        todos.addAll(vistos);
        todos.addAll(vinilos);
        todos.addAll(cds);

        // ------------------------------------------------------------------
        // crea un mapa que asocia cada producto con su lista de imágenes
        // evita duplicados comprobando si el producto ya tiene entrada en el mapa
        // ------------------------------------------------------------------
        Map<Long, List<String>> imagenesMap = new HashMap<>();
        for (ProductoEntity p : todos) {
            if (imagenesMap.containsKey(p.getId_producto()))
                continue;
            List<String> imgs;
            // ------------------------------------------------------------------
            // si el producto tiene imágenes guardadas (separadas por comas)
            // las divide en una lista; si no, usa una imagen por defecto "sin_foto.png"
            // ------------------------------------------------------------------
            if (p.getImagenes() != null && !p.getImagenes().trim().isEmpty()) {
                imgs = Arrays.asList(p.getImagenes().split(","));
            } else {
                imgs = Arrays.asList("sin_foto.png");
            }
            imagenesMap.put(p.getId_producto(), imgs);
        }

        // ------------------------------------------------------------------
        // añade atributos al modelo para que estén disponibles en la vista home:
        // - usuarioId: identificador del usuario actual
        // - nombreCompletoUsuario: nombre del usuario desde la sesión
        // - vistos: lista de productos vistos recientemente
        // - vinilos: lista de vinilos de otros usuarios
        // - cds: lista de CDs de otros usuarios
        // - imagenesMap: mapa de imágenes para cada producto
        // - mostrarBuscador: booleano para mostrar u ocultar el buscador en la vista
        // ------------------------------------------------------------------
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        model.addAttribute("vistos", vistos);
        model.addAttribute("vinilos", vinilos);
        model.addAttribute("cds", cds);
        model.addAttribute("imagenesMap", imagenesMap);
        model.addAttribute("mostrarBuscador", true);

        // ------------------------------------------------------------------
        // devuelve la vista llamada "home" (home.html o home.jsp)
        // ------------------------------------------------------------------
        return "home";
    }
}