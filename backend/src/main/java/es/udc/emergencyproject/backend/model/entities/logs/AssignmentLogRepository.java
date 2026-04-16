package es.udc.emergencyproject.backend.model.entities.logs;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentLogRepository extends JpaRepository<AssignmentLog, Long> {

  List<AssignmentLog> findByEmergencyId(Long emergencyId);
}
