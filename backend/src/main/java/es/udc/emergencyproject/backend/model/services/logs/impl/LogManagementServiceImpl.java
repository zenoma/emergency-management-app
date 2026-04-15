package es.udc.emergencyproject.backend.model.services.logs.impl;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.logs.EmergencyQuadrantLog;
import es.udc.emergencyproject.backend.model.entities.logs.EmergencyQuadrantLogRepository;
import es.udc.emergencyproject.backend.model.entities.logs.GlobalStatistics;
import es.udc.emergencyproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.emergencyproject.backend.model.entities.logs.TeamQuadrantLogRepository;
import es.udc.emergencyproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.emergencyproject.backend.model.entities.logs.VehicleQuadrantLogRepository;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.ResolvedEmergencyException;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LogManagementServiceImpl implements LogManagementService {

  private final EmergencyQuadrantLogRepository emergencyQuadrantLogRepository;
  private final TeamQuadrantLogRepository teamQuadrantLogRepository;
  private final VehicleQuadrantLogRepository vehicleQuadrantLogRepository;
  private final QuadrantRepository quadrantRepository;

  @Override
  public EmergencyQuadrantLog logEmergency(Emergency emergency,
      es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrant emergencyQuadrant)
      throws InstanceNotFoundException {
    return emergencyQuadrantLogRepository.save(
        new EmergencyQuadrantLog(emergency, emergencyQuadrant.getQuadrant(), emergencyQuadrant.getLinkedAt(),
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
  }


  @Override
  public TeamQuadrantLog logTeam(Team team, Quadrant quadrant) throws InstanceNotFoundException {
    return teamQuadrantLogRepository.save(
        new TeamQuadrantLog(team, quadrant, team.getDeployAt(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
  }


  @Override
  public VehicleQuadrantLog logVehicle(Vehicle vehicle, Quadrant quadrant) throws InstanceNotFoundException {
    return vehicleQuadrantLogRepository.save(
        new VehicleQuadrantLog(vehicle, quadrant, vehicle.getDeployAt(),
            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
  }

  @Override
  public List<EmergencyQuadrantLog> findAllEmergencyQuadrantLogs() {
    return emergencyQuadrantLogRepository.findAll();
  }

  @Override
  public List<TeamQuadrantLog> findAllTeamQuadrantLogs() {
    return teamQuadrantLogRepository.findAll();
  }

  @Override
  public List<VehicleQuadrantLog> findAllVehicleQuadrantLogs() {
    return vehicleQuadrantLogRepository.findAll();
  }

  @Override
  public List<EmergencyQuadrantLog> findEmergenciesByEmergencyIdAndLinkedAt(Emergency emergency,
      LocalDateTime startDate,
      LocalDateTime endDate)
      throws InstanceNotFoundException {
    return emergencyQuadrantLogRepository.findByEmergencyIdAndLinkedAtBetweenOrderByLinkedAt(emergency.getId(),
        startDate,
        endDate);
  }

  @Override
  public List<TeamQuadrantLog> findTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Quadrant quadrant,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException {

    return teamQuadrantLogRepository.findByQuadrantIdAndDeployAtBetweenOrderByDeployAt(quadrant.getId(), startDate,
        endDate);
  }

  @Override
  public List<VehicleQuadrantLog> findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Quadrant quadrant,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException {

    return vehicleQuadrantLogRepository.findByQuadrantIdAndDeployAtBetweenOrderByDeployAt(quadrant.getId(), startDate,
        endDate);
  }

  @Override
  public GlobalStatistics getGlobalStatisticsByEmergencyId(Emergency emergency)
      throws InstanceNotFoundException, ResolvedEmergencyException {

    if (emergency.getEmergencyIndex() != EmergencyIndex.RESUELTO) {
      throw new ResolvedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());

    }

    List<Integer> quadrantsGidList = emergencyQuadrantLogRepository.findQuadrantIdsByEmergencyId(emergency.getId());
    Set<Integer> uniqueQuadrants = new HashSet<>(quadrantsGidList);
    List<Integer> uniqueQuadrantsList = new ArrayList<>(uniqueQuadrants);
    Integer affectedQuadrants = uniqueQuadrantsList.size();

    // Batch queries to avoid N+1: fetch all team and vehicle ids for the affected quadrants in one query each
    List<Long> teamsMobilized = teamQuadrantLogRepository.findTeamsIdsByQuadrantsGids(uniqueQuadrantsList);
    List<Long> vehiclesMobilized = vehicleQuadrantLogRepository.findVehiclesIdsByQuadrantsGids(uniqueQuadrantsList);

    Double maxBurnedHectares =
        uniqueQuadrantsList.isEmpty() ? 0 : quadrantRepository.findHectaresByQuadrantIds(uniqueQuadrantsList);

    return new GlobalStatistics(teamsMobilized.size(), vehiclesMobilized.size(), maxBurnedHectares, affectedQuadrants);
  }

}
