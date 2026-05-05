package riffy.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/* /////////////////////////////////////////////////////////////////// */
/* //////////////// entidad conversacion ///////////////////////////// */
/* /////////////////////////////////////////////////////////////////// */

@Entity
@Table(name = "conversacion")
public class ConversacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_conversacion;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private ProductoEntity producto;

    @ManyToOne
    @JoinColumn(name = "id_comprador")
    private UsuarioEntity comprador;

    @ManyToOne
    @JoinColumn(name = "id_vendedor")
    private UsuarioEntity vendedor;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    @Column(name = "conversacion_activa")
    private Boolean conversacionActiva;

    /* /////////////////////////////////////////////////////////////////// */
    /* //////////////// getters y setters //////////////////////////////// */
    /* /////////////////////////////////////////////////////////////////// */

    public Long getId_conversacion() {
        return id_conversacion;
    }

    public void setId_conversacion(Long id_conversacion) {
        this.id_conversacion = id_conversacion;
    }

    public ProductoEntity getProducto() {
        return producto;
    }

    public void setProducto(ProductoEntity producto) {
        this.producto = producto;
    }

    public UsuarioEntity getComprador() {
        return comprador;
    }

    public void setComprador(UsuarioEntity comprador) {
        this.comprador = comprador;
    }

    public UsuarioEntity getVendedor() {
        return vendedor;
    }

    public void setVendedor(UsuarioEntity vendedor) {
        this.vendedor = vendedor;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getConversacionActiva() {
        return conversacionActiva;
    }

    public void setConversacionActiva(Boolean conversacionActiva) {
        this.conversacionActiva = conversacionActiva;
    }
}