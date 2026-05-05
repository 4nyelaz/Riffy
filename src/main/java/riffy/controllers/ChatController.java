package riffy.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull; // Anotación: parámetro no puede ser nulo
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Anotación: pasar datos del controlador a la vista
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import riffy.model.ConversacionEntity;
import riffy.model.MensajeEntity;
import riffy.model.ProductoEntity;
import riffy.model.UsuarioEntity;
import riffy.repository.ConversacionRepository;
import riffy.repository.MensajeRepository;
import riffy.repository.ProductoRepository;
import riffy.repository.UsuarioRepository;

@Controller
public class ChatController {

    // ---------------------------------------------------------------
    // solo se puede usar aquí en este controller, y no es reasignable
    @Autowired
    private ConversacionRepository conversacionRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    // ---------------------------------------------------------------

    /**
     * inicia la conversación desde un producto
     * @param idProducto id del producto sobre el que se inicia una conversación
     * @param session sesión HTTP del usuario actual
     * @param redirect mensajes flash de errores, o información adicional
     * @return Todo correcto: redirección al chat; Error: login/home
     */
    @PostMapping("/chat/iniciar")
    public String iniciarConversacion(@RequestParam @NonNull Long idProducto, HttpSession session, RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null){
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------  

        // guarda en un obj producto/usuario (con rol de comprador) 
        // si en el repositorio se encuentra el id pasado por parámetro o sino, devuelve null
        ProductoEntity producto = productoRepository.findById(idProducto).orElse(null);
        UsuarioEntity comprador = usuarioRepository.findById(usuarioId).orElse(null);

        // si alguno de los objetos devuelve null, redirige a home
        if (producto == null || comprador == null){
            return "redirect:/home";
        }
            
        // comprueba que un usuario tenga un chat con si mismo
        // si el comprador es dueño del producto, redirige a home
        if (producto.getPropietario().getIdUsuario().equals(usuarioId))
            return "redirect:/home";

        // el vendedor es el propietario del producto
        // si existe, la recupera; si no, devuelve null
        ConversacionEntity conversacion = conversacionRepository
                .findByProductoAndCompradorAndVendedor(producto, comprador, producto.getPropietario())
                .orElse(null);

        // ni no hay conversación --> crea una nueva, y la guarda en la base de datos        
        if (conversacion == null) {
            conversacion = new ConversacionEntity();
            conversacion.setProducto(producto);
            conversacion.setComprador(comprador);
            conversacion.setVendedor(producto.getPropietario());
            conversacion.setConversacionActiva(true);
            conversacion.setFechaCreacion(java.time.LocalDate.now());
            conversacion = conversacionRepository.save(conversacion);
        }

        return "redirect:/chat/" + conversacion.getId_conversacion();
    }

    /**
     * Visualización del chat
     * @param idConversacion identificador del chat para verlo
     * @param session sesión HTTP del usuario actual
     * @param model modelo para pasar atributos a la vista
     * @param redirect mensajes flash de errores, o información adicional
     * @return Todo correcto: redirección al chat; Error: login/home
     */
    @GetMapping("/chat/{idConversacion}")
    public String verChat(@PathVariable @NonNull Long idConversacion, HttpSession session, Model model, RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null){
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------  

        // busca la conversación por id, si no existe --> home
        ConversacionEntity conversacion = conversacionRepository.findById(idConversacion).orElse(null);
        if (conversacion == null){
            return "redirect:/home";
        }
            
        // comprueba que el usuario actual pertenezca a la conversación
        // comprueba si es comprador o vendedor
        // si no es ninguno --> redirige a home
        boolean esComprador = conversacion.getComprador().getIdUsuario().equals(usuarioId);
        boolean esVendedor = conversacion.getVendedor().getIdUsuario().equals(usuarioId);
        if (!esComprador && !esVendedor){
            return "redirect:/home";
        }
            
        // recupera los mensajes y los ordena de manera ascendente (más antiguo - más nuevo)
        List<MensajeEntity> mensajes = mensajeRepository.findByConversacionOrderByFechaEnvioAsc(conversacion);

        // otro: 
        // 1.- usuario actual = comprador, otro = vendedor; 
        // 2.- usuario actual = vendedor, otro = comprador;
        UsuarioEntity otro = esComprador ? conversacion.getVendedor() : conversacion.getComprador();

        // añade atributos para mostrarlos en la vista
        model.addAttribute("idConversacion", idConversacion);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("nombreOtro", otro.getNombre());
        model.addAttribute("tituloProducto", conversacion.getProducto().getTitulo());
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

        return "chat";
    }

    /**
     * envío de mensaje
     * @param idConversacion identificador de la conversación donde se envía el mensaje
     * @param mensaje texto del mensaje
     * @param session session sesión HTTP del usuario actual
     * @param redirect mensajes flash de errores, o información adicional
     * @return Todo correcto: redirección al chat; Error: login/home
     */
    @PostMapping("/chat/{idConversacion}/enviar")
    public String enviarMensaje(@PathVariable @NonNull Long idConversacion, @RequestParam String mensaje, HttpSession session, RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null){
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------  

        // busca la conversación por id, si no existe --> home
        ConversacionEntity conversacion = conversacionRepository.findById(idConversacion).orElse(null);
        if (conversacion == null || !conversacion.getConversacionActiva()){
            return "redirect:/home";
        }
            
        // recuperamos el usuario remitente con el id en sesión
        // si no existe --> redirige a home
        UsuarioEntity remitente = usuarioRepository.findById(usuarioId).orElse(null);
        if (remitente == null){
            return "redirect:/home";
        }
            
        // crea un nuevo mensaje
        MensajeEntity msg = new MensajeEntity();
        msg.setConversacion(conversacion);
        msg.setMensaje(mensaje.trim());
        msg.setRemitente(remitente); 
        mensajeRepository.save(msg);

        return "redirect:/chat/" + idConversacion;
    }

    /**
     * lista de chats del usuario en sesion
     * @param session session sesión HTTP del usuario actual
     * @param model modelo para pasar atributos a la vista
     * @param redirect mensajes flash de errores, o información adicional
     * @return Todo correcto: redirección a misconversaciones; Error: login/home
     */
    @GetMapping("/chat/mis-conversaciones")
    public String misConversaciones(HttpSession session, Model model, RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null){
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------  

        // busca el usuario por id, si no existe --> home
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null){
            return "redirect:/home";
        }
        
        // listas de conversación:
        // comoComprador: usuario es comprador
        // comoVendedor: usuario es vendedor
        List<ConversacionEntity> comoComprador = conversacionRepository.findByComprador(usuario);
        List<ConversacionEntity> comoVendedor = conversacionRepository.findByVendedor(usuario);

        // añade atributos para enseñar en la vista
        model.addAttribute("comoComprador", comoComprador);
        model.addAttribute("comoVendedor", comoVendedor);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

        return "misconversaciones";
    }
}
