package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.services.emergency.recommendation.AssignmentRecommendation;
import es.udc.emergencyproject.backend.rest.dtos.RecommendedAssignmentDto;
import es.udc.emergencyproject.backend.rest.dtos.RecommendedAssignmentDto.ResourceTypeEnum;
import java.util.List;

public class RecommendationMapper {

  private RecommendationMapper() {
  }

  public static RecommendedAssignmentDto toDto(AssignmentRecommendation recommendation) {
    RecommendedAssignmentDto dto = new RecommendedAssignmentDto();
    dto.setResourceId(recommendation.getResourceId());
    dto.setResourceType(
        recommendation.getResourceType() != null ? ResourceTypeEnum.valueOf(recommendation.getResourceType().name())
            : null);
    dto.setOrganizationId(recommendation.getOrganizationId());
    dto.setOrganizationName(recommendation.getOrganizationName());
    dto.setDistanceMeters(recommendation.getDistanceMeters());
    dto.setReason(recommendation.getReason());
    return dto;
  }

  public static List<RecommendedAssignmentDto> toDtoList(List<AssignmentRecommendation> recommendations) {
    return recommendations.stream().map(RecommendationMapper::toDto).toList();
  }
}
