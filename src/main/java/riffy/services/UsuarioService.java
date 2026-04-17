package riffy.services;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import riffy.model.UsuarioEntity;
import riffy.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepositorio;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public void registrar(UsuarioEntity usuario) {
        if (usuarioRepositorio.existsByUsuario(usuario.getUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        usuario.setContrasena(encoder.encode(usuario.getContrasena()));
        usuarioRepositorio.save(usuario);
    }

    public Optional<UsuarioEntity> buscarPorUsuario(String usuario) {
        return usuarioRepositorio.findByUsuario(usuario);
    }

    public boolean comprobarContrasena(String contrasenaIntroducida, String contrasenaGuardada) {
        return encoder.matches(contrasenaIntroducida, contrasenaGuardada);
    }
}