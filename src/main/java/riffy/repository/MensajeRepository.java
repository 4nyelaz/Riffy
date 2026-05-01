package riffy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import riffy.model.ConversacionEntity;
import riffy.model.MensajeEntity;

@Repository
public interface MensajeRepository extends JpaRepository<MensajeEntity, Long> {
    List<MensajeEntity> findByConversacionOrderByFechaEnvioAsc(ConversacionEntity conversacion);
}