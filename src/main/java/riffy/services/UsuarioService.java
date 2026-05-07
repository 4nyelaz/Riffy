package riffy.services;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
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

    // TODO
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

    public List<UsuarioEntity> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    public void eliminar(@NonNull Long id) {
        usuarioRepositorio.deleteById(id);
    }

    public void actualizarRol(@NonNull Long id, String rol) {
        usuarioRepositorio.findById(id).ifPresent(u -> {
            u.setRol(rol);
            usuarioRepositorio.save(u);
        });
    }

    public void crearUsuarioAdmin(UsuarioEntity usuario, String rol) {
        usuario.setContrasena(encoder.encode(usuario.getContrasena()));
        usuario.setRol(rol);
        usuarioRepositorio.save(usuario);
    }
}