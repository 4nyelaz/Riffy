package riffy.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import riffy.model.ProductoEntity;
import riffy.model.UsuarioEntity;
import riffy.repository.ProductoRepository;
import riffy.repository.UsuarioRepository;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        List<ProductoEntity> productos = productoRepository.findByPropietarioId(usuarioId);

        Map<Long, List<String>> imagenesMap = new HashMap<>();
        for (ProductoEntity p : productos) {
            List<String> imgs;
            if (p.getImagenes() != null && !p.getImagenes().trim().isEmpty()) {
                imgs = Arrays.asList(p.getImagenes().split(","));
            } else {
                imgs = Arrays.asList("sin_foto.png");
            }
            imagenesMap.put(p.getId_producto(), imgs);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("imagenesMap", imagenesMap);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

        return "home";
    }

    @GetMapping("/editarproducto/{id}")
    public String editarProducto(@PathVariable("id") @NonNull Long id_producto,
            HttpSession session,
            Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        ProductoEntity producto = productoRepository.findById(id_producto).orElse(null);
        if (producto == null) {
            return "redirect:/home";
        }

        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        model.addAttribute("producto", producto);
        model.addAttribute("modoEdicion", true);

        List<String> categorias = Arrays.asList("Vinilo", "CD");
        List<String> formatos = Arrays.asList("Nuevo", "Muy Bueno", "Bueno", "Usado");
        List<String> estados = Arrays.asList("Disponible", "Vendido", "Reservado");

        model.addAttribute("categorias", categorias);
        model.addAttribute("formatos", formatos);
        model.addAttribute("estados", estados);

        return "formulario-producto";
    }

    @PostMapping("/editarproducto/{id}")
    public String actualizarProducto(@PathVariable("id") @NonNull Long id_producto,
            @RequestParam("titulo") String titulo,
            @RequestParam("artista") String artista,
            @RequestParam("formato") String formato,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("estado") String estado,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        ProductoEntity productoExistente = productoRepository.findById(id_producto).orElse(null);

        if (productoExistente == null) {
            return "redirect:/home";
        }

        if (!productoExistente.getPropietario().getIdUsuario().equals(usuarioId)) {
            return "redirect:/home";
        }

        productoExistente.setTitulo(titulo);
        productoExistente.setArtista(artista);
        productoExistente.setFormato(formato);
        productoExistente.setDescripcion(descripcion);
        productoExistente.setPrecio(precio);
        productoExistente.setEstado(estado);
        productoExistente.setCategoria(categoria);
        productoExistente.setFecha_edicion(LocalDate.now());

        // Guardar imágenes si se han subido
        if (imagenes != null && !imagenes.isEmpty() && !imagenes.get(0).isEmpty()) {
            System.out.println("Imágenes recibidas: " + imagenes.size());
            for (MultipartFile img : imagenes) {
                System.out.println("Archivo: " + img.getOriginalFilename() + " | Tamaño: " + img.getSize());
            }

            List<String> nombresImagenes = new ArrayList<>();
            for (MultipartFile img : imagenes) {
                if (!img.isEmpty()) {
                    String nombreArchivo = "usu" + usuarioId + "_" + img.getOriginalFilename();
                    Path ruta = Paths.get("src/main/resources/static/img/productos_img/" + nombreArchivo);
                    try {
                        Files.write(ruta, img.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    nombresImagenes.add(nombreArchivo);
                }
            }
            productoExistente.setImagenes(String.join(",", nombresImagenes));
        }
        // Si no se suben imágenes nuevas, se mantienen las anteriores

        productoRepository.save(productoExistente);

        return "redirect:/home";
    }

    @GetMapping("/eliminarproducto/{id}")
    public String eliminarProducto(@PathVariable("id") @NonNull Long id_producto,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        ProductoEntity producto = productoRepository.findById(id_producto).orElse(null);

        if (producto != null && producto.getPropietario().getIdUsuario().equals(usuarioId)) {
            productoRepository.delete(producto);
            return "redirect:/home"; // TO DO: Poner que redirija a un error
        }

        return "redirect:/home"; // TO DO: Poner que redirija a un error
    }

    @SuppressWarnings("null")
    @GetMapping("/img/productos_img/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> servirImagen(@PathVariable String filename) throws IOException {
        Path ruta = Paths
                .get(System.getProperty("user.dir") + "/src/main/resources/static/img/productos_img/" + filename);
        FileSystemResource resource = new FileSystemResource(ruta);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

    // ── GET ──────────────────────────────────────────────────────────────────────
    @GetMapping("/nuevo-producto")
    public String nuevoProducto(HttpSession session, Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        model.addAttribute("producto", new ProductoEntity());
        model.addAttribute("modoEdicion", false);

        // Mismas listas que editarProducto
        model.addAttribute("categorias", Arrays.asList("Vinilo", "CD"));
        model.addAttribute("formatos", Arrays.asList("Nuevo", "Muy Bueno", "Bueno", "Usado"));
        model.addAttribute("estados", Arrays.asList("Disponible", "Vendido", "Reservado"));

        return "formulario-producto";
    }

    // ── POST ─────────────────────────────────────────────────────────────────────
    @PostMapping("/nuevo-producto")
    public String crearProducto(@RequestParam("titulo") String titulo,
            @RequestParam("artista") String artista,
            @RequestParam("formato") String formato,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("estado") String estado,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }

        ProductoEntity nuevoProducto = new ProductoEntity();
        nuevoProducto.setTitulo(titulo);
        nuevoProducto.setArtista(artista);
        nuevoProducto.setFormato(formato);
        nuevoProducto.setDescripcion(descripcion);
        nuevoProducto.setPrecio(precio);
        nuevoProducto.setEstado(estado);
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setFecha_edicion(LocalDate.now());

        // Asignar propietario desde BD (igual que editarProducto comprueba el
        // propietario)
        UsuarioEntity propietario = usuarioRepository.findById(usuarioId).orElse(null);
        nuevoProducto.setPropietario(propietario);

        // Guardar imágenes si se han subido
        if (imagenes != null && !imagenes.isEmpty() && !imagenes.get(0).isEmpty()) {
            List<String> nombresImagenes = new ArrayList<>();
            for (MultipartFile img : imagenes) {
                if (!img.isEmpty()) {
                    String nombreArchivo = "usu" + usuarioId + "_" + img.getOriginalFilename();
                    Path ruta = Paths.get("src/main/resources/static/img/productos_img/" + nombreArchivo);
                    try {
                        Files.write(ruta, img.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    nombresImagenes.add(nombreArchivo);
                }
            }
            nuevoProducto.setImagenes(String.join(",", nombresImagenes));
        }

        productoRepository.save(nuevoProducto);

        return "redirect:/home";
    }

}