package es.udc.emergencyproject.backend.model.entities.assignment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

  List<Assignment> findByEmergencyQuadrantId(Long emergencyQuadrantId);

  List<Assignment> findByResourceId(Long resourceId);

  List<Assignment> findByEmergencyId(Long emergencyId);

  List<Assignment> findByEmergencyQuadrantQuadrantId(Integer quadrantId);

}
