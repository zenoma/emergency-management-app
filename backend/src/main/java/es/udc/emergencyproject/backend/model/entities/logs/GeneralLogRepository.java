package es.udc.emergencyproject.backend.model.entities.logs;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralLogRepository extends JpaRepository<GeneralLog, Long> {

  List<GeneralLog> findByEmergencyId(Long emergencyId);
}
