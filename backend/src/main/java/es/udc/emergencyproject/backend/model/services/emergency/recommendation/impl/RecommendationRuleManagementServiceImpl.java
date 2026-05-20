package es.udc.emergencyproject.backend.model.services.emergency.recommendation.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRule;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRuleRepository;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.InvalidRecommendationRuleException;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.RecommendationRuleManagementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RecommendationRuleManagementServiceImpl implements RecommendationRuleManagementService {

  private static final String RULE_NOT_FOUND = "EmergencyTypeRule not found";

  private final EmergencyTypeRuleRepository emergencyTypeRuleRepository;
  private final ObjectMapper objectMapper;

  @Override
  public List<EmergencyTypeRule> findRulesByEmergencyTypeId(Long emergencyTypeId) {
    return emergencyTypeRuleRepository.findByEmergencyTypeIdOrderByPriorityAsc(emergencyTypeId);
  }

  @Override
  public EmergencyTypeRule updateRule(Long id, Integer priority, String ruleJson) throws InstanceNotFoundException {
    EmergencyTypeRule rule = emergencyTypeRuleRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(RULE_NOT_FOUND, id));

    try {
      objectMapper.readTree(ruleJson);
    } catch (Exception e) {
      throw new InvalidRecommendationRuleException(EmergencyTypeRule.class.getSimpleName(), String.valueOf(id));
    }

    if (priority != null) {
      rule.setPriority(priority);
    }
    rule.setRuleJson(ruleJson);
    return emergencyTypeRuleRepository.save(rule);
  }
}
