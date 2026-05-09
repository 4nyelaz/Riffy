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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import riffy.model.ProductoEntity;
import riffy.model.UsuarioEntity;
import riffy.repository.ProductoRepository;
import riffy.repository.UsuarioRepository;

@Controller
public class ProductoController {

    // ---------------------------------------------------------------
    // solo se puede usar aquí en este controller, y no es reasignable
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    // ---------------------------------------------------------------

    /**
     * metodo de los productos del usuario en sesión
     * 
     * @param session  sesión HTTP del usuario actual
     * @param model    modelo para pasar atributos a la vista
     * @param redirect mensajes flash de errores, o información adicional
     * @return Todo correcto: redirección al chat; Error: login
     */
    @GetMapping("/mis-productos")
    public String misproductoString(HttpSession session, Model model, RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null)
            return "redirect:/home";

        // recoge todos los productos del usuario en sesión
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

        // añade atributos para mostrarlos en la vista
        model.addAttribute("productos", productos);
        model.addAttribute("imagenesMap", imagenesMap);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));

        return "producto/misproductos";
    }

    @GetMapping("/editarproducto/{id}")
    public String editarProducto(@PathVariable("id") @NonNull Long id_producto, HttpSession session, Model model,
            RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        // guarda en un obj producto
        // si en el repositorio se encuentra el id pasado por parámetro o sino, devuelve
        // null
        ProductoEntity producto = productoRepository.findById(id_producto).orElse(null);

        // si no existe, redirige a home
        if (producto == null) {
            return "redirect:/home";
        }

        // añade atributos para mostrarlos en la vista
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

        return "producto/formulario-producto";
    }

    /**
     * actualiza un producto existente del usuario en sesión
     * 
     * @param id_producto identificador del producto a editar
     * @param titulo      nuevo título del producto
     * @param artista     nueva artista del producto
     * @param formato     nuevo formato del producto
     * @param descripcion nueva descripcion del producto
     * @param precio      nuevo precio del producto
     * @param estado      nuevo estado del producto
     * @param categoria   nueva categoria del producto
     * @param imagenes    nueva/nuevas imagene del producto
     * @param session     session sesión HTTP del usuario actual
     * @return Todo correcto: redirección al misproductos; Error: login/home
     */
    @PostMapping("/editarproducto/{id}")
    public String actualizarProducto(@PathVariable("id") @NonNull Long id_producto, RedirectAttributes redirect,
            String titulo,
            String artista,
            String formato,
            String descripcion,
            BigDecimal precio,
            String estado,
            String categoria,
            @RequestParam(required = false) List<MultipartFile> imagenes,
            HttpSession session) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        // guarda en un obj producto
        // si en el repositorio se encuentra el id pasado por parámetro o sino, devuelve
        // null
        ProductoEntity producto = productoRepository.findById(id_producto).orElse(null);

        // si no existe, redirige a home
        if (producto == null) {
            return "redirect:/home";
        }

        // comprueba que el producto pertenece al usuario de la sesión
        if (!producto.getPropietario().getIdUsuario().equals(usuarioId)) {
            return "redirect:/mis-productos";
        }

        // establece los campos nuevos del form en el obj que ya existe
        producto.setTitulo(titulo);
        producto.setArtista(artista);
        producto.setFormato(formato);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setEstado(estado);
        producto.setCategoria(categoria);
        producto.setFecha_edicion(LocalDate.now());

        // guarda imagenes si se han subido

        if (imagenes != null && !imagenes.isEmpty() && !imagenes.get(0).isEmpty()) {
            System.out.println("Imágenes recibidas: " + imagenes.size());
            for (MultipartFile img : imagenes) {
                System.out.println("Archivo: " + img.getOriginalFilename() + " | Tamaño: " + img.getSize());
            }

            // recorre todas las imágenes del formulario y las guarda en el sistema de
            // archivos
            List<String> nombresImagenes = new ArrayList<>();
            for (MultipartFile img : imagenes) {
                if (!img.isEmpty()) {
                    // genera un nombre único para la imagen
                    String nombreArchivo = "usu" + usuarioId + "_" + img.getOriginalFilename();
                    // define la ruta donde se guardará la imagen
                    Path ruta = Paths.get("src/main/resources/static/img/productos_img/" + nombreArchivo);
                    // guarda el archivo físicamente en el disco
                    try {
                        Files.write(ruta, img.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace(); // si hay error, imprime la traza pero continúa
                    }
                    // añade el nombre del archivo a la lista
                    nombresImagenes.add(nombreArchivo);
                }
            }
            // convierte la lista en un String separado por comas
            producto.setImagenes(String.join(",", nombresImagenes));
        }

        // Si no se suben imágenes nuevas, se mantienen las anteriores
        // guarda en la bd
        productoRepository.save(producto);

        return "redirect:/mis-productos";
    }

    /**
     * elimina un producto de la base de datos
     * 
     * @param id_producto identificador del producto a eliminar
     * @param session     sesión HTTP del usuario actual
     * @param redirect    mensajes flash de errores, o información adicional
     * @return Todo correcto: redirección al misproductos; Error: login
     */
    @GetMapping("/eliminarproducto/{id}")
    public String eliminarProducto(@PathVariable("id") @NonNull Long id_producto,
            HttpSession session,
            RedirectAttributes redirect) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }

        ProductoEntity producto = productoRepository.findById(id_producto).orElse(null);

        if (producto != null && producto.getPropietario().getIdUsuario().equals(usuarioId)) {
            productoRepository.delete(producto);
            redirect.addFlashAttribute("toastExito", "Producto eliminado correctamente.");
        } else {
            redirect.addFlashAttribute("toastError", "No se pudo eliminar el producto.");
        }

        return "redirect:/mis-productos";
    }

    /**
     * sirve imágenes de productos desde el sistema de archivos
     * 
     * @param filename nombre del archivo de imagen a servir
     * @return Todo correcot: ResponseEntity con la imagen; Error: 404
     * @throws IOException si hay error al leer el archivo
     */
    @SuppressWarnings("null")
    @GetMapping("/img/productos_img/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> servirImagen(@PathVariable String filename) throws IOException {
        // construimos la ruta completa, con user.dir nos dice la raíz del proyecto
        Path ruta = Paths
                .get(System.getProperty("user.dir") + "/src/main/resources/static/img/productos_img/" + filename);
        // !! wrapper: añade métodos útiles al obj Path
        FileSystemResource resource = new FileSystemResource(ruta);

        // si no existe -> error 404
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // si va bien -> 200
        // wrapper (body) -> se convierte en el contenido de la imagen
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
    }

    /**
     * elimina un producto de la base de datos
     * 
     * @param id_producto identificador del producto a eliminar
     * @param session     sesión HTTP del usuario actual
     * @param redirect    mensajes flash de errores, o información adicional
     * @return Todo correcto: redirección a misproductos; Error: login
     */
    @GetMapping("/nuevo-producto")
    public String nuevoProducto(HttpSession session, Model model, RedirectAttributes redirect) {
        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null)
            return "redirect:/home";

        // añade atributos para enseñar en la vista
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        model.addAttribute("producto", new ProductoEntity());
        model.addAttribute("modoEdicion", false);

        // mismas listas que editarProducto
        model.addAttribute("categorias", Arrays.asList("Vinilo", "CD"));
        model.addAttribute("formatos", Arrays.asList("Nuevo", "Muy Bueno", "Bueno", "Usado"));

        return "producto/crearproducto";
    }

    @PostMapping("/nuevo-producto")
    public String crearProducto(@RequestParam("titulo") String titulo, RedirectAttributes redirect,
            String artista,
            String formato,
            String descripcion,
            BigDecimal precio,
            String categoria,
            @RequestParam(required = false) List<MultipartFile> imagenes,
            HttpSession session, Model model) {

        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null)
            return "redirect:/home";

        // crea nuevo obj producto y se le asigna los campos del form a los atr del obj
        ProductoEntity nuevoProducto = new ProductoEntity();
        nuevoProducto.setTitulo(titulo);
        nuevoProducto.setArtista(artista);
        nuevoProducto.setFormato(formato);
        nuevoProducto.setDescripcion(descripcion);
        nuevoProducto.setPrecio(precio);
        nuevoProducto.setEstado("Disponible");
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setFecha_edicion(LocalDate.now());

        model.addAttribute("usuario", usuario);

        // buscar usuario en bd por id
        // establecemos al nuevo obj el propietario
        UsuarioEntity propietario = usuarioRepository.findById(usuarioId).orElse(null);
        nuevoProducto.setPropietario(propietario);

        // guardar imágenes si se han subido
        if (imagenes != null && !imagenes.isEmpty() && !imagenes.get(0).isEmpty()) {
            // recorre todas las imágenes del formulario y las guarda en el sistema de
            // archivos
            List<String> nombresImagenes = new ArrayList<>();
            for (MultipartFile img : imagenes) {
                if (!img.isEmpty()) {
                    // genera un nombre único para la imagen
                    String nombreArchivo = "usu" + usuarioId + "_" + img.getOriginalFilename();
                    // define la ruta donde se guardará la imagen
                    Path ruta = Paths.get("src/main/resources/static/img/productos_img/" + nombreArchivo);
                    try {
                        Files.write(ruta, img.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace(); // si hay error, imprime la traza pero continúa
                    }
                    // añade el nombre del archivo a la lista
                    nombresImagenes.add(nombreArchivo);
                }
            }
            // convierte la lista en un String separado por comas
            nuevoProducto.setImagenes(String.join(",", nombresImagenes));
        }

        productoRepository.save(nuevoProducto);

        return "redirect:/home";
    }

    /**
     * visualización de informacion de producto
     * 
     * @param id_producto identificador del producto a comprar
     * @param redirect    mensajes flash de errores, o información adicional
     * @param session     sesión HTTP del usuario actual
     * @param model       modelo para pasar atributos a la vista
     * @return Todo correcto: redirección al detalleproducto; Error: login
     */
    @SuppressWarnings("unchecked")
    @GetMapping("/producto/{id}")
    public String verProducto(@PathVariable("id") @NonNull Long id_producto, RedirectAttributes redirect,
            HttpSession session, Model model) {

        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null)
            return "redirect:/home";

        // guarda en un obj producto
        // si en el repositorio se encuentra el id pasado por parámetro o sino, devuelve
        // null
        ProductoEntity producto = productoRepository.findById(id_producto).orElse(null);

        // si no existe, redirige a home
        if (producto == null) {
            return "redirect:/home";
        }

        // conf de historial
        if (!producto.getPropietario().getIdUsuario().equals(usuarioId)) {

            // recupera el historial actual de la sesión
            List<Long> historialIds = (List<Long>) session.getAttribute("historialIds");
            if (historialIds == null)
                historialIds = new ArrayList<>();

            // elimina el producto si ya estaba
            historialIds.remove(id_producto);

            // lo añadimos al principio
            historialIds.add(0, id_producto);

            // sólo saldrán 10 tarjetas
            if (historialIds.size() > 10) {
                historialIds = historialIds.subList(0, 10);
            }

            // uarda el historial en la sesión
            session.setAttribute("historialIds", historialIds);
        }

        // separa el string -> imagenes
        List<String> imgs;
        if (producto.getImagenes() != null && !producto.getImagenes().trim().isEmpty()) {
            imgs = Arrays.asList(producto.getImagenes().split(","));
        } else {
            // foto por defecto
            imgs = Arrays.asList("sin_foto.png");
        }

        // añade atributos para mostrarlos en la vista
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        model.addAttribute("producto", producto);
        model.addAttribute("imagenes", imgs);

        return "producto/detalleproducto";
    }

    /**
     * SIMULACIÓN DE COMPRA -> marca un producto como vendido
     * 
     * @param id_producto identificador del producto a comprar
     * @param session     sesión HTTP del usuario actual
     * @param redirect    mensajes flash de errores, o información adicional
     * @return Todo correcto: redirige detalle del producto, Error: login/home
     */
    @GetMapping("/producto/comprar/{id}")
    public String comprarProducto(@PathVariable("id") @NonNull Long id_producto, HttpSession session,
            RedirectAttributes redirect) {

        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        // guarda en un obj producto
        // si en el repositorio se encuentra el id pasado por parámetro o sino, devuelve
        // null
        ProductoEntity producto = productoRepository.findById(id_producto).orElse(null);

        // comprobaciones: existe, no es tuyo, y está disponible
        if (producto == null || producto.getPropietario().getIdUsuario().equals(usuarioId)
                || !producto.getEstado().equals("Disponible")) {
            return "redirect:/home";
        }

        // cambia el estado del producto de disponible a venidido
        // lo guarda en la bd
        producto.setEstado("Reservado");
        productoRepository.save(producto);

        return "redirect:/producto/" + id_producto;
    }

    /**
     * busca productos por texto, categoría, estado y rango de precios
     * 
     * @param q         término de búsqueda
     * @param categoria filtro por categoría
     * @param estado    filtro por estado
     * @param precioMin precio mínimo para filtrar
     * @param precioMax precio máximo para filtrar
     * @param session   sesión HTTP del usuario actual
     * @param model     modelo para pasar atributos a la vista
     * @param redirect  mensajes flash de errores, o información adicional
     * @return vista con los resultados de búsqueda
     */
    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String formato,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            HttpSession session,
            Model model,
            RedirectAttributes redirect) {

        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null)
            return "redirect:/home";

        // ------------------------------------------------------------------
        // si q es null o está en blanco, se queda como null
        String qFinal = (q != null && !q.isBlank()) ? q.trim() : null;
        String catFinal = (categoria != null && !categoria.isBlank()) ? categoria : null;
        String fmtFinal = (formato != null && !formato.isBlank()) ? formato : null;
        // ------------------------------------------------------------------

        // llama al método personalizado del repositorio que hace la consulta dinámica
        List<ProductoEntity> resultados = productoRepository.buscar(usuarioId, qFinal, catFinal, fmtFinal, precioMin, precioMax);


        Map<Long, List<String>> imagenesMap = new HashMap<>();
        for (ProductoEntity p : resultados) {
            List<String> imgs = (p.getImagenes() != null && !p.getImagenes().isBlank())
                    ? Arrays.asList(p.getImagenes().split(","))
                    : Arrays.asList("sin_foto.png");
            imagenesMap.put(p.getId_producto(), imgs);
        }

        // añade atributos para mostrarlos en la vista
        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", resultados);
        model.addAttribute("imagenesMap", imagenesMap);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        model.addAttribute("q", q);
        model.addAttribute("categoria", categoria);
        model.addAttribute("formato", formato);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);
        model.addAttribute("mostrarBuscador", true);

        return "producto/buscar";
    }

    /**
     * explora productos con filtros
     * similar a buscar pero sin el parámetro q
     * 
     * @param categoria filtro por categoría
     * @param estado    filtro por estado
     * @param precioMin precio mínimo para filtrar
     * @param precioMax precio máximo para filtrar
     * @param session   sesión HTTP del usuario actual
     * @param model     modelo para pasar atributos a la vista
     * @param redirect  mensajes flash de errores, o información adicional
     * @return vista con los productos filtrados
     */
    @GetMapping("/explorar")
    public String explorar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) String formato,
            HttpSession session,
            Model model,
            RedirectAttributes redirect) {

        // ------------------------------------------------------------------
        // comprueba que el usuario está en sesión, sino, redirige al login
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            redirect.addFlashAttribute("sesionCaducada", "Sesión caducada");
            return "redirect:/login";
        }
        // ------------------------------------------------------------------

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null)
            return "redirect:/home";

        // ------------------------------------------------------------------
        // si q es null o está en blanco, se queda como null
        String catFinal = (categoria != null && !categoria.isBlank()) ? categoria : null;
        String fmtFinal = (formato != null && !formato.isBlank()) ? formato : null;
        // ------------------------------------------------------------------

        // reutiliza el mismo método buscar del repositorio
        // el primer parámetro q va como null -> no hay búsqueda textual
        // solo filtra por categoría, estado y rango de precios
        List<ProductoEntity> resultados = productoRepository.buscar(usuarioId, null, catFinal, fmtFinal, precioMin, precioMax);
        
        Map<Long, List<String>> imagenesMap = new HashMap<>();
        for (ProductoEntity p : resultados) {
            List<String> imgs = (p.getImagenes() != null && !p.getImagenes().isBlank())
                    ? Arrays.asList(p.getImagenes().split(","))
                    : Arrays.asList("sin_foto.png");
            imagenesMap.put(p.getId_producto(), imgs);
        }

        // añade atributos para mostrarlos en la vista
        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", resultados);
        model.addAttribute("imagenesMap", imagenesMap);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("nombreCompletoUsuario", session.getAttribute("nombreCompletoUsuario"));
        model.addAttribute("categoria", categoria);
        model.addAttribute("formato", formato);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);

        return "producto/explorar";
    }

    // Notificaciones para el vendedor
    @PostMapping("/producto/{id}/aceptar")
    public String aceptarProducto(@PathVariable("id") @NonNull Long id, HttpSession session,
            HttpServletRequest request) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        ProductoEntity producto = productoRepository.findById(id).orElse(null);
        if (producto != null && producto.getPropietario().getIdUsuario().equals(usuarioId)) {
            producto.setEstado("Vendido");
            productoRepository.save(producto);
        }
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/producto/{id}/rechazar")
    public String rechazarProducto(@PathVariable("id") @NonNull Long id, HttpSession session,
            HttpServletRequest request) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        ProductoEntity producto = productoRepository.findById(id).orElse(null);
        if (producto != null && producto.getPropietario().getIdUsuario().equals(usuarioId)) {
            producto.setEstado("Disponible");
            productoRepository.save(producto);
        }
        return "redirect:" + request.getHeader("Referer");
    }

}