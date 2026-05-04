package riffy.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import riffy.model.ProductoEntity;

@Repository
public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

    @Query("SELECT p FROM ProductoEntity p WHERE p.propietario.idUsuario = :usuarioId")
    List<ProductoEntity> findByPropietarioId(@Param("usuarioId") Long usuarioId);

    List<ProductoEntity> findByCategoriaAndPropietarioIdUsuarioNot(String categoria, Long idUsuario);

    @Query("""
                SELECT p FROM ProductoEntity p
                WHERE p.estado = 'Disponible'
                  AND (:q IS NULL OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', :q, '%'))
                                  OR LOWER(p.artista) LIKE LOWER(CONCAT('%', :q, '%')))
                  AND (:categoria IS NULL OR p.categoria = :categoria)
                  AND (:estado IS NULL OR p.estado = :estado)
                  AND (:precioMin IS NULL OR p.precio >= :precioMin)
                  AND (:precioMax IS NULL OR p.precio <= :precioMax)
            """)
    List<ProductoEntity> buscar(
            @Param("q") String q,
            @Param("categoria") String categoria,
            @Param("estado") String estado,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax);
}