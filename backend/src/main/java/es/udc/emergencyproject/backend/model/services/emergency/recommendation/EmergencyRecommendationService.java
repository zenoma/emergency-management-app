package es.udc.emergencyproject.backend.model.services.emergency.recommendation;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import java.util.List;

public interface EmergencyRecommendationService {

  List<AssignmentRecommendation> recommendForEmergency(Long emergencyId) throws InstanceNotFoundException;

  List<AssignmentRecommendation> recommendForEmergency(Long emergencyId, Integer quadrantId)
      throws InstanceNotFoundException;

  List<AssignmentRecommendation> recommendForEmergency(Emergency emergency);
}
