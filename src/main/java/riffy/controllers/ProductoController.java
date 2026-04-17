package riffy.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import riffy.model.ProductoEntity;
import riffy.model.UsuarioEntity;
import riffy.repository.ProductoRepository;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        List<ProductoEntity> productos = productoRepository.findByPropietarioId(usuarioId);
        model.addAttribute("productos", productos);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

        return "home";
    }

    // @GetMapping("/editarproducto/{id}")
    // public String editarProducto(@PathVariable("id") Long id_producto,
    //         HttpSession session,
    //         Model model) {

    //     // Verificar sesión (reutilizando tu lógica)
    //     Long usuarioId = (Long) session.getAttribute("usuarioId");
    //     if (usuarioId == null) {
    //         return "redirect:/login";
    //     }

    //     // Buscar el producto
    //     ProductoEntity producto = productoRepository.findById(id_producto).orElse(null);

    //     // Verificar que existe
    //     if (producto == null) {
    //         return "redirect:/home?error=producto-no-encontrado";
    //     }

    //     Long usuarioId = (Long) session.getAttribute("usuarioId");

    //     if (usuarioId == null) {
    //         return "redirect:/login";
    //     }

    //     // Datos de sesión (misma estructura que en /home)
    //     model.addAttribute("usuarioId", usuarioId);
    //     model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

    //     // Datos del producto a editar
    //     model.addAttribute("producto", producto);
    //     model.addAttribute("modoEdicion", true);

    //     // Listas para selects
    //     List<String> categorias = Arrays.asList("Vinilo", "CD", "Cassette", "Merchandising");
    //     List<String> formatos = Arrays.asList("Nuevo", "Usado", "Reedición", "Edición Especial");
    //     List<String> estados = Arrays.asList("Disponible", "Vendido", "Reservado");

    //     model.addAttribute("categorias", categorias);
    //     model.addAttribute("formatos", formatos);
    //     model.addAttribute("estados", estados);

    //     return "formulario-producto"; // o la vista que uses para crear/editar
    // }

    // // 💾 POST - Guardar cambios
    // @PostMapping("/editarproducto/{id}")
    // public String actualizarProducto(@PathVariable("id") Long id_producto,
    //         @ModelAttribute("producto") ProductoEntity productoForm,
    //         HttpSession session,
    //         Model model) {

    //     // Verificar sesión
    //     Long usuarioId = (Long) session.getAttribute("usuarioId");
    //     if (usuarioId == null) {
    //         return "redirect:/login";
    //     }

    //     // Buscar producto existente
    //     ProductoEntity productoExistente = productoRepository.findById(id_producto).orElse(null);

    //     // Verificaciones
    //     if (productoExistente == null) {
    //         return "redirect:/home?error=producto-no-encontrado";
    //     }

    //     if (!productoExistente.getPropietario().getId_usuario().equals(usuarioId)) {
    //         return "redirect:/home?error=no-autorizado";
    //     }

    //     // Actualizar campos editables
    //     productoExistente.setTitulo(productoForm.getTitulo());
    //     productoExistente.setArtista(productoForm.getArtista());
    //     productoExistente.setFormato(productoForm.getFormato());
    //     productoExistente.setDescripcion(productoForm.getDescripcion());
    //     productoExistente.setPrecio(productoForm.getPrecio());
    //     productoExistente.setEstado(productoForm.getEstado());
    //     productoExistente.setCategoria(productoForm.getCategoria());
    //     productoExistente.setImagenes(productoForm.getImagenes());
    //     productoExistente.setFecha_edicion(LocalDate.now());

    //     // Guardar cambios
    //     productoRepository.save(productoExistente);

    //     return "redirect:/home?success=producto-actualizado";
    // }

    // // 🗑️ DELETE - Eliminar producto (opcional pero recomendado)
    // @GetMapping("/eliminarproducto/{id}")
    // public String eliminarProducto(@PathVariable("id") Long id_producto,
    //         HttpSession session) {

    //     Long usuarioId = (Long) session.getAttribute("usuarioId");
    //     if (usuarioId == null) {
    //         return "redirect:/login";
    //     }

    //     ProductoEntity producto = productoRepository.findById(id_producto).orElse(null);

    //     if (producto != null && producto.getPropietario().getId_usuario().equals(usuarioId)) {
    //         productoRepository.delete(producto);
    //         return "redirect:/home?success=producto-eliminado";
    //     }

    //     return "redirect:/home?error=no-autorizado";
    // }

    // // 🆕 GET - Mostrar formulario para nuevo producto
    // @GetMapping("/nuevo-producto")
    // public String nuevoProducto(HttpSession session, Model model) {

    //     Long usuarioId = (Long) session.getAttribute("usuarioId");
    //     if (usuarioId == null) {
    //         return "redirect:/login";
    //     }

    //     // Datos de sesión
    //     model.addAttribute("usuarioId", usuarioId);
    //     model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

    //     // Producto vacío para el formulario
    //     model.addAttribute("producto", new ProductoEntity());
    //     model.addAttribute("modoEdicion", false);

    //     // Datos de la app
    //     model.addAttribute("titleApp", titleApp);
    //     model.addAttribute("nameApp", nameApp);
    //     model.addAttribute("version", version);

    //     // Listas para selects
    //     model.addAttribute("categorias", Arrays.asList("Vinilo", "CD", "Cassette", "Merchandising"));
    //     model.addAttribute("formatos", Arrays.asList("Nuevo", "Usado", "Reedición", "Edición Especial"));
    //     model.addAttribute("estados", Arrays.asList("Disponible", "Vendido", "Reservado"));

    //     return "formulario-producto";
    // }

    // // 💾 POST - Crear nuevo producto
    // @PostMapping("/nuevo-producto")
    // public String crearProducto(@ModelAttribute("producto") ProductoEntity producto,
    //         HttpSession session) {

    //     Long usuarioId = (Long) session.getAttribute("usuarioId");
    //     if (usuarioId == null) {
    //         return "redirect:/login";
    //     }

    //     // Asignar propietario
    //     UsuarioEntity propietario = new UsuarioEntity();
    //     propietario.setId_usuario(usuarioId);
    //     producto.setPropietario(propietario);

    //     // Fecha de creación/edición
    //     producto.setFecha_edicion(LocalDate.now());

    //     productoRepository.save(producto);

    //     return "redirect:/home?success=producto-creado";
    // }

}