package riffy.repository;

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
}