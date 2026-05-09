package riffy.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import riffy.model.UsuarioEntity;
import riffy.repository.UsuarioRepository;

@Controller
public class UsuarioController {

    // ---------------------------------------------------------------
    // solo se puede usar aquí en este controller, y no es reasignable
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ---------------------------------------------------------------
    @GetMapping("/perfil/editar")
    public String editarPerfil(HttpSession session, Model model, RedirectAttributes redirect) {
        Long usuarioId =  (Long) session.getAttribute("usuarioId");
        if (usuarioId = = null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        UsuarioEntity usuario =  usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario = = null)
            return "redirect:/home";

        model.addAttribute("usuario", usuario);
        model.addAttribute("fotoPerfil", session.getAttribute("fotoPerfil"));
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        return "usuario/editarperfil";
    }

    @PostMapping("/perfil/editar")
    public String guardarPerfil(
            @RequestParam String nombre,
            @RequestParam String usuario,
            @RequestParam(required =  false) String email,
            @RequestParam(required =  false) String contrasenaActual,
            @RequestParam(required =  false) String contrasenaNueva,
            @RequestParam(required =  false) MultipartFile fotoPerfil,
            HttpSession session, RedirectAttributes redirect) {

        Long usuarioId =  (Long) session.getAttribute("usuarioId");
        if (usuarioId = = null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }

        UsuarioEntity u =  usuarioRepository.findById(usuarioId).orElse(null);
        if (u = = null)
            return "redirect:/home";

        u.setNombre(nombre);
        u.setUsuario(usuario);
        if (email != null && !email.isBlank())
            u.setEmail(email);

        // contraseña solo si rellena ambos campos y la actual es correcta
        if (contrasenaNueva != null && !contrasenaNueva.isBlank()
                && contrasenaActual != null
                && passwordEncoder.matches(contrasenaActual, u.getContrasena())) {
            u.setContrasena(passwordEncoder.encode(contrasenaNueva));
        }

        // foto de perfil — igual que productos
        if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
            String nombreArchivo =  "usu" + usuarioId + "_" + fotoPerfil.getOriginalFilename();
            Path ruta =  Paths.get("src/main/resources/static/img/fotoperfil_img/" + nombreArchivo);
            try {
                Files.write(ruta, fotoPerfil.getBytes());
                u.setFotoPerfil(nombreArchivo);
                session.setAttribute("fotoPerfil", nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        usuarioRepository.save(u);
        session.setAttribute("nombreCompletoUsuario", nombre);
        redirect.addFlashAttribute("perfilActualizado", "Perfil actualizado correctamente.");
        return "redirect:/perfil/editar";
    }
}
