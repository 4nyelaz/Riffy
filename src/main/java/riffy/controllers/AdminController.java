package riffy.controllers;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import riffy.model.ConversacionEntity;
import riffy.model.UsuarioEntity;
import riffy.repository.ConversacionRepository;
import riffy.repository.MensajeRepository;
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
    private final MensajeRepository mensajeRepository;
    // ---------------------------------------------------------------

    public AdminController(UsuarioService usuarioService,
            ProductoService productoService,
            ConversacionRepository conversacionRepository,
            MensajeRepository mensajeRepository) {
        this.usuarioService =  usuarioService;
        this.productoService =  productoService;
        this.conversacionRepository =  conversacionRepository;
        this.mensajeRepository =  mensajeRepository;
    }

    // comprueba que quien llama es admin — si no lo es, fuera
    private boolean esAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("rolUsuario"));
    }

    // ---------------------------------------------------------------
    // dashboard
    // ---------------------------------------------------------------

    /**
     * muestra el panel principal de administración
     * carga usuarios, productos y conversaciones de una vez
     * los mensajes se consultan aparte, por conversación
     * 
     * @param model   modelo de Thymeleaf donde se inyectan los datos
     * @param session sesión HTTP del usuario actual
     * @return plantilla admin/tablaadmin, o redirección a login si no es ADMIN
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId =  (Long) session.getAttribute("usuarioId");
        if (usuarioId = = null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        if (!esAdmin(session))
            return "redirect:/login";

        // tres consultas, tres atributos — Thymeleaf hace el resto
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("conversaciones", conversacionRepository.findAll());
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

        return "admin/tablaadmin";
    }

    // ---------------------------------------------------------------
    // usuarios
    // ---------------------------------------------------------------

    /**
     * elimina un usuario de la plataforma de forma permanente
     * cuidado con esto — no tiene vuelta atrás
     * 
     * @param id      id del usuario a eliminar
     * @param session sesión HTTP del usuario actual
     * @return redirección al dashboard en la pestaña de usuarios, o login si no es
     *         ADMIN
     */

    @PostMapping("/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable @NonNull Long id,
            HttpSession session,
            RedirectAttributes redirect) {
        if (!esAdmin(session))
            return "redirect:/login";
        usuarioService.eliminar(id);
        redirect.addFlashAttribute("toastExito", "Usuario eliminado correctamente.");
        return "redirect:/admin/dashboard?seccion=usuarios";
    }

    /**
     * cambia el rol de un usuario entre USER y ADMIN
     * el rol llega como parámetro de formulario, sin AJAX
     * 
     * @param id      id del usuario a modificar
     * @param rol     nuevo rol, esperado USER o ADMIN
     * @param session sesión HTTP del usuario actual
     * @return redirección al dashboard en la pestaña de usuarios, o login si no es
     *         ADMIN
     */
    @PostMapping("/usuarios/{id}/rol")
    public String cambiarRol(@PathVariable @NonNull Long id,
            @RequestParam String rol,
            HttpSession session) {
        if (!esAdmin(session))
            return "redirect:/login";
        usuarioService.actualizarRol(id, rol);
        return "redirect:/admin/dashboard?seccion=usuarios";
    }

    /**
     * muestra el formulario para dar de alta a un nuevo usuario interno
     * pensado para empleados nuevos que necesitan acceso desde el primer día
     * flujo separado del registro público — aquí el admin controla el rol
     * directamente
     * 
     * @param session sesión HTTP del usuario actual
     * @param model   modelo donde se inyecta el objeto vacío del formulario
     * @return plantilla admin/nuevo-usuario, o login si no es ADMIN
     */
    @GetMapping("/usuarios/nuevo")
    public String formularioNuevoUsuario(HttpSession session, Model model) {
        if (!esAdmin(session))
            return "redirect:/login";
        model.addAttribute("usuario", new UsuarioEntity());
        return "admin/nuevousuario";
    }

    /**
     * procesa el alta de un nuevo usuario interno desde el panel
     * la contraseña se hashea igual que en el registro normal
     * sin verificación de email ni nada de eso — es un alta directa
     * 
     * @param usuario objeto con nombre, email y contraseña del formulario
     * @param rol     rol a asignar, esperado USER o ADMIN
     * @param session sesión HTTP del usuario actual
     * @return redirección al dashboard en la pestaña de usuarios, o login si no es
     *         ADMIN
     */
    @PostMapping("/usuarios/nuevo")
    public String crearUsuario(@ModelAttribute UsuarioEntity usuario,
            @RequestParam String rol,
            HttpSession session) {
        if (!esAdmin(session))
            return "redirect:/login";
        usuarioService.crearUsuarioAdmin(usuario, rol);
        return "redirect:/admin/dashboard?seccion=usuarios";
    }

    // ---------------------------------------------------------------
    // productos
    // ---------------------------------------------------------------

    /**
     * elimina un producto de la plataforma
     * para moderar contenido inapropiado o erróneo — también permanente
     * 
     * @param id      id del producto a eliminar
     * @param session sesión HTTP del usuario actual
     * @return redirección al dashboard en la pestaña de productos, o login si no es
     *         ADMIN
     */
    @PostMapping("/productos/{id}/eliminar")
    public String eliminarProducto(@PathVariable @NonNull Long id,
            HttpSession session,
            RedirectAttributes redirect) {
        if (!esAdmin(session))
            return "redirect:/login";
        productoService.eliminar(id);
        redirect.addFlashAttribute("toastExito", "Producto eliminado correctamente.");
        return "redirect:/admin/dashboard?seccion=productos";
    }
    // ---------------------------------------------------------------
    // conversaciones
    // ---------------------------------------------------------------

    /**
     * alterna el estado de una conversación entre activa y cerrada
     * si estaba activa la cierra, si estaba cerrada la reactiva
     * útil para mediar conflictos sin tener que eliminar nada
     * 
     * @param id      id de la conversación a modificar
     * @param session sesión HTTP del usuario actual
     * @return redirección al dashboard en la pestaña de conversaciones, o login si
     *         no es ADMIN
     */
    @PostMapping("/conversaciones/{id}/toggle")
    public String toggleConversacion(@PathVariable @NonNull Long id, HttpSession session) {
        if (!esAdmin(session))
            return "redirect:/login";

        // si no existe simplemente no hace nada — sin explosiones
        conversacionRepository.findById(id).ifPresent(c -> {
            c.setConversacionActiva(!c.getConversacionActiva());
            conversacionRepository.save(c);
        });

        return "redirect:/admin/dashboard?seccion=conversaciones";
    }

    // ---------------------------------------------------------------
    // mensajes (solo lectura)
    // ---------------------------------------------------------------

    /**
     * muestra los mensajes de una conversación concreta
     * el admin puede leerlos para contextualizar una denuncia, pero no tocar nada
     * se abre en página aparte para no saturar el dashboard con todos los mensajes
     * 
     * @param id      id de la conversación cuyos mensajes se quieren ver
     * @param model   modelo donde se inyectan la conversación y sus mensajes
     * @param session sesión HTTP del usuario actual
     * @return plantilla admin/mensajes, o login si no es ADMIN
     */
    @GetMapping("/conversaciones/{id}/mensajes")
    public String verMensajes(@PathVariable @NonNull Long id,
            Model model,
            HttpSession session) {
        if (!esAdmin(session))
            return "redirect:/login";

        // si la conversación no existe, de vuelta al dashboard
        ConversacionEntity conv =  conversacionRepository.findById(id).orElse(null);
        if (conv = = null)
            return "redirect:/admin/dashboard?seccion=conversaciones";

        // el repo necesita la entidad entera, no solo el id
        model.addAttribute("conversacion", conv);
        model.addAttribute("mensajes", mensajeRepository.findByConversacionOrderByFechaEnvioAsc(conv));

        return "admin/mensajes";
    }

    // Añade estos dos métodos dentro de AdminController, junto a los otros de
    // usuarios

    /**
     * edita los campos básicos de un usuario directamente desde la tabla
     * nombre, usuario (login), email — sin tocar la contraseña
     *
     * @param id      id del usuario a editar
     * @param nombre  nuevo nombre
     * @param usuario nuevo nombre de usuario / login
     * @param email   nuevo email
     * @param session sesión HTTP del usuario actual
     * @return redirección al dashboard en la pestaña de usuarios, o login si no es
     *         ADMIN
     */
    @PostMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable @NonNull Long id,
            @RequestParam String nombre,
            @RequestParam String usuario,
            @RequestParam String email,
            HttpSession session) {
        if (!esAdmin(session))
            return "redirect:/login";
        usuarioService.actualizarDatos(id, nombre, usuario, email);
        return "redirect:/admin/dashboard?seccion=usuarios";
    }
}