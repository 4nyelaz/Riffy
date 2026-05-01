package riffy.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    private ConversacionRepository conversacionRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Iniciar conversación desde un producto
    @PostMapping("/chat/iniciar")
    public String iniciarConversacion(@RequestParam @NonNull Long idProducto,
            HttpSession session,
            RedirectAttributes redirect) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
            return "redirect:/login";

        ProductoEntity producto = productoRepository.findById(idProducto).orElse(null);
        UsuarioEntity comprador = usuarioRepository.findById(usuarioId).orElse(null);

        if (producto == null || comprador == null)
            return "redirect:/home";
        if (producto.getPropietario().getIdUsuario().equals(usuarioId))
            return "redirect:/home";

        ConversacionEntity conversacion = conversacionRepository
                .findByProductoAndCompradorAndVendedor(producto, comprador, producto.getPropietario())
                .orElse(null);

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

    // Ver conversación
    @GetMapping("/chat/{idConversacion}")
    public String verChat(@PathVariable @NonNull Long idConversacion,
            HttpSession session,
            Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
            return "redirect:/login";

        ConversacionEntity conversacion = conversacionRepository.findById(idConversacion).orElse(null);
        if (conversacion == null)
            return "redirect:/home";

        boolean esComprador = conversacion.getComprador().getIdUsuario().equals(usuarioId);
        boolean esVendedor = conversacion.getVendedor().getIdUsuario().equals(usuarioId);
        if (!esComprador && !esVendedor)
            return "redirect:/home";

        List<MensajeEntity> mensajes = mensajeRepository
                .findByConversacionOrderByFechaEnvioAsc(conversacion);

        UsuarioEntity otro = esComprador ? conversacion.getVendedor() : conversacion.getComprador();

        model.addAttribute("idConversacion", idConversacion);
        model.addAttribute("mensajes", mensajes);
        model.addAttribute("nombreOtro", otro.getNombre());
        model.addAttribute("tituloProducto", conversacion.getProducto().getTitulo());
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

        return "chat";
    }

    // Enviar mensaje
    @PostMapping("/chat/{idConversacion}/enviar")
    public String enviarMensaje(@PathVariable @NonNull Long idConversacion,
            @RequestParam String mensaje,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
            return "redirect:/login";

        ConversacionEntity conversacion = conversacionRepository.findById(idConversacion).orElse(null);
        if (conversacion == null || !conversacion.getConversacionActiva())
            return "redirect:/home";

        // recuperamos el usuario remitente
        UsuarioEntity remitente = usuarioRepository.findById(usuarioId).orElse(null);
        if (remitente == null)
            return "redirect:/home";

        MensajeEntity msg = new MensajeEntity();
        msg.setConversacion(conversacion);
        msg.setMensaje(mensaje.trim());
        msg.setRemitente(remitente); // asignamos el remitente
        mensajeRepository.save(msg);

        return "redirect:/chat/" + idConversacion;
    }

    // Listar conversaciones del usuario
    @GetMapping("/chat/mis-conversaciones")
    public String misConversaciones(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null)
            return "redirect:/login";

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null)
            return "redirect:/home";

        List<ConversacionEntity> comoComprador = conversacionRepository.findByComprador(usuario);
        List<ConversacionEntity> comoVendedor = conversacionRepository.findByVendedor(usuario);

        model.addAttribute("comoComprador", comoComprador);
        model.addAttribute("comoVendedor", comoVendedor);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

        return "misconversaciones";
    }
}
