package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.services.emergency.recommendation.RecommendationRuleManagementService;
import es.udc.emergencyproject.backend.rest.dtos.RecommendationRuleDto;
import es.udc.emergencyproject.backend.rest.dtos.RecommendationRuleUpdateRequestDto;
import es.udc.emergencyproject.backend.rest.mappers.RecommendationRuleMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendation-rules")
@RequiredArgsConstructor
public class RecommendationRulesController {

  private final RecommendationRuleManagementService recommendationRuleManagementService;

  @GetMapping
  public ResponseEntity<List<RecommendationRuleDto>> getRecommendationRules(@RequestParam Long emergencyTypeId) {
    return ResponseEntity.ok(RecommendationRuleMapper.toDtoList(
        recommendationRuleManagementService.findRulesByEmergencyTypeId(emergencyTypeId)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<RecommendationRuleDto> putRecommendationRule(@PathVariable Long id,
      @Valid @RequestBody RecommendationRuleUpdateRequestDto requestDto) {
    return ResponseEntity.ok(RecommendationRuleMapper.toDto(
        recommendationRuleManagementService.updateRule(id, requestDto.getPriority(), requestDto.getRuleJson())));
  }
}
