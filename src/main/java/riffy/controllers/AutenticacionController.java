package riffy.controllers;

import org.springframework.stereotype.Controller; // Anotación: maneja peticiones HTTP y devuelve HTML
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Anotación: coge los datos de un form y los mete en un obj Java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Anotación: permite mandar datos omitiendolo en la URL

import jakarta.servlet.http.HttpSession;
import riffy.model.UsuarioEntity;
import riffy.services.UsuarioService;

@Controller
public class AutenticacionController {

    // solo se puede usar aquí en este controller, y no es reasignable
    private final UsuarioService usuarioService; 

    /**
     * mete las dependencias por instructor
     * @param usuarioService
     */
    public AutenticacionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * @return vista de login
     */
    @GetMapping("/login")
    public String login() {
        return "autenticacion/login-signup";
    }

    /**
     * modelattribute recoge los datos del form, y los mete en el objeto usuario 
     * si hace registro, manda al login para iniciar sesion, si no es así, te manda de nuevo a registrarte
     * si no, lanza excepción -> flash attribute -> vista errorRegistro
     * @param usuario
     * @param redirect
     * @return
     */
    @PostMapping("/register")
    public String registrar(@ModelAttribute UsuarioEntity usuario, RedirectAttributes redirect) {
        try {
            usuarioService.registrar(usuario);
            return "redirect:/login?section=login";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("errorRegistro", e.getMessage());
            return "redirect:/login?section=register";
        }
    }


    /**
     * modelattribute recoge los datos del form, y los mete en el objeto usuario
     * buscarPorUsuario devuelve boolean (puede o no existir)
     * si es así, te lleva a home
     * sino, error y direcciona a login
     * @param usuario
     * @param redirect
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String login(@ModelAttribute UsuarioEntity usuario, RedirectAttributes redirect, HttpSession session) {
        return usuarioService.buscarPorUsuario(usuario.getUsuario())
                .filter(u -> usuarioService.comprobarContrasena(usuario.getContrasena(), u.getContrasena()))
                .map(u -> {
                    session.setAttribute("nombreCompletoUsuario", u.getNombre());
                    session.setAttribute("usuarioId", u.getIdUsuario());
                    session.setAttribute("nombreUsuario", u.getUsuario());
                    return "redirect:/home";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("errorLogin", "Usuario o contraseña incorrectos");
                    return "redirect:/login?section=login";
                });
    }

    /**
     * invalida la sesión actual
     * guarda mensaje flash -> cerrar sesión
     * @param session
     * @param redirect
     * @return redirige login
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirect) {
        session.invalidate();
        redirect.addFlashAttribute("mensajeLogout", "Sesión cerrada correctamente");
        return "redirect:/login";
    }

}
