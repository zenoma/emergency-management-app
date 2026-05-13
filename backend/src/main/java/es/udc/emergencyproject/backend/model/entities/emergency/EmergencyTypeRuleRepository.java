package es.udc.emergencyproject.backend.model.entities.emergency;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyTypeRuleRepository extends JpaRepository<EmergencyTypeRule, Long> {

  List<EmergencyTypeRule> findByEmergencyTypeIdOrderByPriorityAsc(Long emergencyTypeId);
}
