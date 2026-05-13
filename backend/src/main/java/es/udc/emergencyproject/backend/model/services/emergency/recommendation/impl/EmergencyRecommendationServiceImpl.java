package es.udc.emergencyproject.backend.model.services.emergency.recommendation.impl;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRuleRepository;
import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceType;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.team.TeamRepository;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.VehicleRepository;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.AssignmentRecommendation;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.EmergencyRecommendationService;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.RecommendationRuleEngine;
import es.udc.emergencyproject.backend.rest.mappers.TeamMapper;
import es.udc.emergencyproject.backend.rest.mappers.VehicleMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    var rules = emergencyTypeRuleRepository.findByEmergencyTypeIdOrderByPriorityAsc(
        emergency.getEmergencyType().getId());
    var evaluation = ruleEngine.evaluate(emergency.getEmergencyType().getName(), rules);
    if (evaluation.getTeams() <= 0 && evaluation.getVehicles() <= 0) {
      return List.of();
    }

    List<AssignmentRecommendation> result = new ArrayList<>();
    Point location = emergency.getLocation();
    double maxDistanceMeters =
        evaluation.getMaxDistanceKm() != null ? evaluation.getMaxDistanceKm() * 1000d : Double.MAX_VALUE;
    String preferredOrganizationType = evaluation.getPreferredOrganizationType();

    if (evaluation.getTeams() > 0) {
      List<Team> teams = teamRepository.findAvailableClosestToLocation(location,
          PageRequest.of(0, evaluation.getTeams()));
      List<Team> filteredTeams = new ArrayList<>();
      for (Team team : teams) {
        if (!matchesOrganizationPreference(team.getOrganization(), preferredOrganizationType)) {
          continue;
        }
        double distance = team.getOrganization() != null && team.getOrganization().getLocation() != null
            ? team.getOrganization().getLocation().distance(location)
            : 0d;
        if (distance > maxDistanceMeters) {
          continue;
        }
        filteredTeams.add(team);
        result.add(new AssignmentRecommendation(
            team.getId(),
            ResourceType.TEAM,
            team.getOrganization() != null ? team.getOrganization().getId() : null,
            team.getOrganization() != null ? team.getOrganization().getName() : null,
            distance,
            evaluation.getReason(),
            TeamMapper.toTeamDtoWithoutQuadrantInfo(team),
            null));
      }
      if (filteredTeams.isEmpty()) {
        for (Team team : teams) {
          double distance = team.getOrganization() != null && team.getOrganization().getLocation() != null
              ? team.getOrganization().getLocation().distance(location)
              : 0d;
          if (distance > maxDistanceMeters) {
            continue;
          }
          result.add(new AssignmentRecommendation(
              team.getId(),
              ResourceType.TEAM,
              team.getOrganization() != null ? team.getOrganization().getId() : null,
              team.getOrganization() != null ? team.getOrganization().getName() : null,
              distance,
              evaluation.getReason(),
              TeamMapper.toTeamDtoWithoutQuadrantInfo(team),
              null));
        }
      }
    }

    if (evaluation.getVehicles() > 0) {
      List<Vehicle> vehicles = vehicleRepository.findAvailableClosestToLocation(location,
          PageRequest.of(0, evaluation.getVehicles()));
      List<Vehicle> filteredVehicles = new ArrayList<>();
      for (Vehicle vehicle : vehicles) {
        if (!matchesOrganizationPreference(vehicle.getOrganization(), preferredOrganizationType)) {
          continue;
        }
        double distance = vehicle.getOrganization() != null && vehicle.getOrganization().getLocation() != null
            ? vehicle.getOrganization().getLocation().distance(location)
            : 0d;
        if (distance > maxDistanceMeters) {
          continue;
        }
        filteredVehicles.add(vehicle);
        result.add(new AssignmentRecommendation(
            vehicle.getId(),
            ResourceType.VEHICLE,
            vehicle.getOrganization() != null ? vehicle.getOrganization().getId() : null,
            vehicle.getOrganization() != null ? vehicle.getOrganization().getName() : null,
            distance,
            evaluation.getReason(),
            null,
            VehicleMapper.toVehicleDtoWithoutQuadrantInfo(vehicle)));
      }
      if (filteredVehicles.isEmpty()) {
        for (Vehicle vehicle : vehicles) {
          double distance = vehicle.getOrganization() != null && vehicle.getOrganization().getLocation() != null
              ? vehicle.getOrganization().getLocation().distance(location)
              : 0d;
          if (distance > maxDistanceMeters) {
            continue;
          }
          result.add(new AssignmentRecommendation(
              vehicle.getId(),
              ResourceType.VEHICLE,
              vehicle.getOrganization() != null ? vehicle.getOrganization().getId() : null,
              vehicle.getOrganization() != null ? vehicle.getOrganization().getName() : null,
              distance,
              evaluation.getReason(),
              null,
              VehicleMapper.toVehicleDtoWithoutQuadrantInfo(vehicle)));
        }
      }
    }

    return result;
  }

  private boolean matchesOrganizationPreference(Organization organization, String preferredOrganizationType) {
    if (preferredOrganizationType == null || preferredOrganizationType.isBlank()) {
      return true;
    }
    if (organization == null || organization.getOrganizationType() == null
        || organization.getOrganizationType().getName() == null) {
      return false;
    }
    return organization.getOrganizationType().getName().toLowerCase(Locale.ROOT)
        .contains(preferredOrganizationType.toLowerCase(Locale.ROOT));
  }
}
