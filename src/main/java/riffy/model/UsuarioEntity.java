package riffy.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/* /////////////////////////////////////////////////////////////////// */
/* //////////////// entidad usuario ////////////////////////////////// */
/* /////////////////////////////////////////////////////////////////// */

@Entity
@Table(name = "usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank
    @Size(min = 3)
    private String nombre;

    @NotBlank
    @Size(min = 4)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    private String usuario;

    @NotBlank
    @Size(min = 8)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).+$")
    private String contrasena;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    @Column(name = "rol")
    private String rol = "USER";

    @Column(name = "email")
    private String email;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    /* /////////////////////////////////////////////////////////////////// */
    /* //////////////// getters y setters //////////////////////////////// */
    /* /////////////////////////////////////////////////////////////////// */

    @PrePersist
    public void antesDeGuardar() {
        this.fechaRegistro = LocalDate.now();
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

}
