package es.udc.emergencyproject.backend.model.services.emergency.recommendation;

import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRule;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import java.util.List;

public interface RecommendationRuleManagementService {

  List<EmergencyTypeRule> findRulesByEmergencyTypeId(Long emergencyTypeId);

  EmergencyTypeRule updateRule(Long id, Integer priority, String ruleJson)
      throws InstanceNotFoundException;
}
