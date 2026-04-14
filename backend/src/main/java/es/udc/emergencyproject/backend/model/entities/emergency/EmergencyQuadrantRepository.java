package es.udc.emergencyproject.backend.model.entities.emergency;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyQuadrantRepository extends JpaRepository<EmergencyQuadrant, Long> {

  List<EmergencyQuadrant> findByEmergencyId(Long emergencyId);

  List<EmergencyQuadrant> findByQuadrantId(Integer quadrantId);

  EmergencyQuadrant findByEmergencyIdAndQuadrantId(Long emergencyId, Integer quadrantId);

}
