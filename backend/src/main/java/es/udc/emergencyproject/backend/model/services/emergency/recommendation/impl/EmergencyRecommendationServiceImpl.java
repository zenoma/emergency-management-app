package es.udc.emergencyproject.backend.model.services.emergency.recommendation.impl;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRuleRepository;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceType;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.team.TeamRepository;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.VehicleRepository;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.AssignmentRecommendation;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.EmergencyRecommendationService;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.RecommendationRuleEngine;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmergencyRecommendationServiceImpl implements EmergencyRecommendationService {

  private final EmergencyRepository emergencyRepository;
  private final EmergencyTypeRuleRepository emergencyTypeRuleRepository;
  private final TeamRepository teamRepository;
  private final VehicleRepository vehicleRepository;
  private final RecommendationRuleEngine ruleEngine;

  @Override
  public List<AssignmentRecommendation> recommendForEmergency(Long emergencyId) throws InstanceNotFoundException {
    Emergency emergency = emergencyRepository.findById(emergencyId)
        .orElseThrow(() -> new InstanceNotFoundException("Emergency not found", emergencyId));
    return recommendForEmergency(emergency);
  }

  @Override
  public List<AssignmentRecommendation> recommendForEmergency(Emergency emergency) {
    if (emergency == null || emergency.getLocation() == null || emergency.getEmergencyType() == null) {
      return List.of();
    }

    var rules = emergencyTypeRuleRepository.findByEmergencyTypeIdOrderByPriorityAsc(emergency.getEmergencyType().getId());
    var evaluation = ruleEngine.evaluate(emergency.getEmergencyType().getName(), rules);
    if (evaluation.getTeams() <= 0 && evaluation.getVehicles() <= 0) {
      return List.of();
    }

    List<AssignmentRecommendation> result = new ArrayList<>();
    Point location = emergency.getLocation();

    if (evaluation.getTeams() > 0) {
      List<Team> teams = teamRepository.findAvailableClosestToLocation(location, PageRequest.of(0, evaluation.getTeams()));
      for (Team team : teams) {
        result.add(new AssignmentRecommendation(
            team.getId(),
            ResourceType.TEAM,
            team.getOrganization() != null ? team.getOrganization().getId() : null,
            team.getOrganization() != null ? team.getOrganization().getName() : null,
            team.getOrganization() != null && team.getOrganization().getLocation() != null
                ? team.getOrganization().getLocation().distance(location)
                : 0d,
            evaluation.getReason()));
      }
    }

    if (evaluation.getVehicles() > 0) {
      List<Vehicle> vehicles = vehicleRepository.findAvailableClosestToLocation(location, PageRequest.of(0, evaluation.getVehicles()));
      for (Vehicle vehicle : vehicles) {
        result.add(new AssignmentRecommendation(
            vehicle.getId(),
            ResourceType.VEHICLE,
            vehicle.getOrganization() != null ? vehicle.getOrganization().getId() : null,
            vehicle.getOrganization() != null ? vehicle.getOrganization().getName() : null,
            vehicle.getOrganization() != null && vehicle.getOrganization().getLocation() != null
                ? vehicle.getOrganization().getLocation().distance(location)
                : 0d,
            evaluation.getReason()));
      }
    }

    return result;
  }
}
