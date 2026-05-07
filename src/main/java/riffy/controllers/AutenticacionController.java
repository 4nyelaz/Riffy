package riffy.controllers;

import org.springframework.stereotype.Controller; // Anotación: maneja peticiones HTTP y devuelve HTML
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // Anotación: coge los datos de un form y los mete en un obj Java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Anotación: permite mandar datos omitiendolo en la URL
import jakarta.validation.Valid;


import jakarta.servlet.http.HttpSession;
import riffy.model.UsuarioEntity;
import riffy.services.UsuarioService;

@Controller
public class AutenticacionController {

    // solo se puede usar aquí en este controller, y no es reasignable
    private final UsuarioService usuarioService;

    /**
     * mete las dependencias por instructor
     * 
     * @param usuarioService
     */
    public AutenticacionController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * @return redirige a login
     */
    @GetMapping("/login")
    public String login() {
        return "autenticacion/login-signup";
    }

    /**
     * modelattribute recoge los datos del form, y los mete en el objeto usuario
     * si hace registro, manda al login para iniciar sesion, si no es así, te manda
     * de nuevo a registrarte
     * si no, lanza excepción -> flash attribute -> vista errorRegistro
     * 
     * @param usuario  objeto con los datos del formulario
     * @param redirect mensajes flash de errores, o información adicional
     * @return
     */
    @PostMapping("/register")
    public String registrar(
            @Valid @ModelAttribute UsuarioEntity usuario,
            BindingResult result,
            RedirectAttributes redirect) {

        
        if (result.hasErrors()) {

            redirect.addFlashAttribute(
                    "errorRegistro",
                    "Revisa los campos del formulario.");

            redirect.addFlashAttribute(
                    "org.springframework.validation.BindingResult.usuario",
                    result);

            redirect.addFlashAttribute("usuario", usuario);

            return "redirect:/login?section=register";
        }

        
        if (usuarioService.existeUsuario(usuario.getUsuario())) {

            redirect.addFlashAttribute(
                    "errorRegistro",
                    "Este usuario ya está registrado. Elige otro nombre.");

            redirect.addFlashAttribute("usuario", usuario);

            return "redirect:/login?section=register";
        }

        
        usuarioService.registrar(usuario);

        redirect.addFlashAttribute(
                "registroExito",
                "Cuenta creada correctamente. Ya puedes iniciar sesión.");

        return "redirect:/login?section=login";
    }

    @GetMapping("/api/usuario-existe")
    @ResponseBody
    public boolean usuarioExiste(@RequestParam String usuario) {
        return usuarioService.existeUsuario(usuario);
    }

    /**
     * modelattribute recoge los datos del form, y los mete en el objeto usuario
     * buscarPorUsuario devuelve boolean (puede o no existir)
     * si es así, te lleva a home
     * sino, error y direcciona a login
     * 
     * @param usuario  objeto con los datos del formulario
     * @param redirect mensajes flash de errores, o información adicional
     * @param session  session sesión HTTP del usuario actual
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
                    session.setAttribute("rolUsuario", u.getRol());

                    redirect.addFlashAttribute(
                            "loginExito",
                            "Bienvenido de nuevo, " + u.getNombre());

                    if ("ADMIN".equals(u.getRol())) {
                        return "redirect:/admin/dashboard";
                    }
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
     * 
     * @param session  session sesión HTTP del usuario actual
     * @param redirect mensajes flash de errores, o información adicional
     * @return redirige login
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirect) {
        session.invalidate();
        redirect.addFlashAttribute("mensajeLogout", "Sesión cerrada correctamente");
        return "redirect:/login";
    }

}
