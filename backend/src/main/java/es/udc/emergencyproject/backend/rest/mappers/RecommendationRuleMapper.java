package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRule;
import es.udc.emergencyproject.backend.rest.dtos.RecommendationRuleDto;
import java.util.ArrayList;
import java.util.List;

public class RecommendationRuleMapper {

  private RecommendationRuleMapper() {
  }

  public static RecommendationRuleDto toDto(EmergencyTypeRule rule) {
    RecommendationRuleDto dto = new RecommendationRuleDto();
    dto.setId(rule.getId());
    dto.setEmergencyTypeId(rule.getEmergencyType() != null ? rule.getEmergencyType().getId() : null);
    dto.setEmergencyTypeName(rule.getEmergencyType() != null ? rule.getEmergencyType().getName() : null);
    dto.setPriority(rule.getPriority());
    dto.setRuleJson(rule.getRuleJson());
    return dto;
  }

  public static List<RecommendationRuleDto> toDtoList(List<EmergencyTypeRule> rules) {
    List<RecommendationRuleDto> result = new ArrayList<>();
    for (EmergencyTypeRule rule : rules) {
      result.add(toDto(rule));
    }
    return result;
  }
}
