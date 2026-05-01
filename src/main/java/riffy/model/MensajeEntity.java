package riffy.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "mensaje")
public class MensajeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_mensaje;

    @ManyToOne
    @JoinColumn(name = "id_conversacion", nullable = false)
    private ConversacionEntity conversacion;

    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "leido")
    private Boolean leido = false;

    @ManyToOne
    @JoinColumn(name = "id_remitente", nullable = false)
    private UsuarioEntity remitente;

    @PrePersist
    protected void onCreate() {
        fechaEnvio = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId_mensaje() {
        return id_mensaje;
    }

    public void setId_mensaje(Long id_mensaje) {
        this.id_mensaje = id_mensaje;
    }

    public ConversacionEntity getConversacion() {
        return conversacion;
    }

    public void setConversacion(ConversacionEntity conversacion) {
        this.conversacion = conversacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }

    public UsuarioEntity getRemitente() {
        return remitente;
    }

    public void setRemitente(UsuarioEntity remitente) {
        this.remitente = remitente;
    }
}