package riffy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import riffy.model.ConversacionEntity;
import riffy.model.ProductoEntity;
import riffy.model.UsuarioEntity;

@Repository
public interface ConversacionRepository extends JpaRepository<ConversacionEntity, Long> {

    Optional<ConversacionEntity> findByProductoAndCompradorAndVendedor(
            ProductoEntity producto, UsuarioEntity comprador, UsuarioEntity vendedor);

    List<ConversacionEntity> findByCompradorOrVendedor(UsuarioEntity comprador, UsuarioEntity vendedor);

    List<ConversacionEntity> findByComprador(UsuarioEntity comprador);

    List<ConversacionEntity> findByVendedor(UsuarioEntity vendedor);
}