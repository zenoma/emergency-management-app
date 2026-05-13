package es.udc.emergencyproject.backend.model.services.emergency.recommendation;

import es.udc.emergencyproject.backend.model.entities.resource.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssignmentRecommendation {

  private final Long resourceId;
  private final ResourceType resourceType;
  private final Long organizationId;
  private final String organizationName;
  private final double distanceMeters;
  private final String reason;
}
