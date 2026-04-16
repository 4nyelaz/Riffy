package riffy.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_producto;

    @Column(nullable = false)
    private String titulo;

    private String artista;

    private String formato;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    @ManyToOne
    @JoinColumn(name = "propietario_id", referencedColumnName = "id_usuario", nullable = false)
    private UsuarioEntity propietario;

    private LocalDate fecha_edicion;

    private String estado;

    private String categoria;

    @Column(name = "imagenes", length = 1000)
    private String imagenes;

    // para guardarlas en lista: List<String> lista =
    // Arrays.asList(producto.getImagenes().split(","));
    // para guardarlas otra vez en el server: producto.setImagenes(String.join(",",
    // listaDeUrls));

    // Getters y setters
    public Long getId_producto() {
        return id_producto;
    }

    public void setId_producto(Long id_producto) {
        this.id_producto = id_producto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public UsuarioEntity getPropietario() {
        return propietario;
    }

    public void setPropietario(UsuarioEntity propietario) {
        this.propietario = propietario;
    }

    public LocalDate getFecha_edicion() {
        return fecha_edicion;
    }

    public void setFecha_edicion(LocalDate fecha_edicion) {
        this.fecha_edicion = fecha_edicion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagenes() {
        return imagenes;
    }

    public void setImagenes(String imagenes) {
        this.imagenes = imagenes;
    }

}