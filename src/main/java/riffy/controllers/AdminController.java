package riffy.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import riffy.repository.ConversacionRepository;
import riffy.services.ProductoService;
import riffy.services.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // ---------------------------------------------------------------
    // solo se puede usar aquí en este controller, y no es reasignable
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final ConversacionRepository conversacionRepository;
    // ---------------------------------------------------------------

    /**
     * inyección de dependencias por constructor
     * 
     * @param usuarioService         servicio de usuarios
     * @param productoService        servicio de productos
     * @param conversacionRepository repositorio de conversaciones
     */
    public AdminController(UsuarioService usuarioService,
            ProductoService productoService,
            ConversacionRepository conversacionRepository) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.conversacionRepository = conversacionRepository;
    }

    /**
     * comprueba si el usuario en sesión tiene rol ADMIN
     * 
     * @param session sesión HTTP del usuario actual
     * @return true si es ADMIN, false si no lo es
     */
    private boolean esAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("rolUsuario"));
    }

    /**
     * muestra el panel de administración
     * si el usuario no es ADMIN redirige al login
     * 
     * @param session sesión HTTP del usuario actual
     * @return plantilla admin/tablaadmin o redirección a login
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (!esAdmin(session))
            return "redirect:/login";
        return "admin/tablaadmin";
    }

    // ---------------------------------------------------------------
    // usuarios
    // ---------------------------------------------------------------

    /**
     * devuelve la lista completa de usuarios registrados
     * @param session sesión HTTP del usuario actual
     * @return 200 con la lista de usuarios, o 403 si no es ADMIN
     */
    @GetMapping("/api/usuarios")
    @ResponseBody

    public Map<String, Object> listarUsuarios(
            @RequestParam(value = "jtStartIndex", defaultValue = "0") int startIndex,
            @RequestParam(value = "jtPageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "jtSorting", required = false) String sorting,
            HttpSession session) {

        if (!esAdmin(session)) {
            return Map.of("Result", "ERROR", "Message", "No autorizado");
        }

        return Map.of(
                "Result", "OK",
                "Records", usuarioService.listarTodos());
    }

    /**
     * elimina un usuario por su id
     * 
     * @param id      id del usuario a eliminar
     * @param session sesión HTTP del usuario actual
     * @return 200 si se eliminó correctamente, o 403 si no es ADMIN
     */
    @DeleteMapping("/api/usuarios/{id}")
    @ResponseBody
    public Map<String, Object> eliminarUsuario(@PathVariable @NonNull Long id, HttpSession session) {
        if (!esAdmin(session)) {
            return Map.of("Result", "ERROR");
        }

        usuarioService.eliminar(id);

        return Map.of("Result", "OK");
    }

    /**
     * cambia el rol de un usuario entre USER y ADMIN
     * espera un JSON con el campo rol en el cuerpo de la petición
     * 
     * @param id      id del usuario a modificar
     * @param body    mapa con la clave rol y el nuevo valor
     * @param session sesión HTTP del usuario actual
     * @return 200 si se actualizó correctamente, o 403 si no es ADMIN
     */
    @PatchMapping("/api/usuarios/{id}/rol")
    @ResponseBody
    public ResponseEntity<?> cambiarRol(@PathVariable @NonNull Long id,
            @RequestBody Map<String, String> body,
            HttpSession session) {
        if (!esAdmin(session))
            return ResponseEntity.status(403).build();
        usuarioService.actualizarRol(id, body.get("rol"));
        return ResponseEntity.ok().build();
    }

    // ---------------------------------------------------------------
    // productos
    // ---------------------------------------------------------------

    /**
     * devuelve la lista completa de productos publicados en la plataforma
     * 
     * @param session sesión HTTP del usuario actual
     * @return 200 con la lista de productos, o 403 si no es ADMIN
     */
    @GetMapping("/api/productos")
    @ResponseBody
    public Map<String, Object> listarProductos(HttpSession session) {
        if (!esAdmin(session)) {
            return Map.of("Result", "ERROR", "Message", "No autorizado");
        }

        return Map.of(
                "Result", "OK",
                "Records", productoService.findAll());
    }

    /**
     * elimina un producto por su id
     * 
     * @param id      id del producto a eliminar
     * @param session sesión HTTP del usuario actual
     * @return 200 si se eliminó correctamente, o 403 si no es ADMIN
     */
    @DeleteMapping("/api/productos/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarProducto(@PathVariable @NonNull Long id, HttpSession session) {
        if (!esAdmin(session))
            return ResponseEntity.status(403).build();
        productoService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    // ---------------------------------------------------------------
    // conversaciones
    // ---------------------------------------------------------------

    /**
     * devuelve la lista completa de conversaciones entre usuarios
     * 
     * @param session sesión HTTP del usuario actual
     * @return 200 con la lista de conversaciones, o 403 si no es ADMIN
     */
    @GetMapping("/api/conversaciones")
    @ResponseBody
    public Map<String, Object> listarConversaciones(HttpSession session) {
        if (!esAdmin(session)) {
            return Map.of("Result", "ERROR", "Message", "No autorizado");
        }

        return Map.of(
                "Result", "OK",
                "Records", conversacionRepository.findAll());
    }

    /**
     * activa o desactiva una conversación según su estado actual
     * si estaba activa la cierra, si estaba cerrada la reactiva
     * 
     * @param id      id de la conversación a modificar
     * @param session sesión HTTP del usuario actual
     * @return 200 si se cambió el estado, o 403 si no es ADMIN
     */
    @PatchMapping("/api/conversaciones/{id}/estado")
    @ResponseBody
    public ResponseEntity<?> toggleConversacion(@PathVariable @NonNull Long id, HttpSession session) {
        if (!esAdmin(session))
            return ResponseEntity.status(403).build();
        conversacionRepository.findById(id).ifPresent(c -> {
            c.setConversacionActiva(!c.getConversacionActiva());
            conversacionRepository.save(c);
        });
        return ResponseEntity.ok().build();
    }
}