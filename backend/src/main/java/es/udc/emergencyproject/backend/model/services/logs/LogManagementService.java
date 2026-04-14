package es.udc.emergencyproject.backend.model.services.logs;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.logs.EmergencyQuadrantLog;
import es.udc.emergencyproject.backend.model.entities.logs.GlobalStatistics;
import es.udc.emergencyproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.emergencyproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.exceptions.ExtinguishedEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public interface LogManagementService {

  EmergencyQuadrantLog logEmergency(Emergency emergency,
      es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrant emergencyQuadrant) throws InstanceNotFoundException;

  TeamQuadrantLog logTeam(Team team,
      Quadrant quadrant) throws InstanceNotFoundException;

  VehicleQuadrantLog logVehicle(Vehicle vehicle,
      Quadrant quadrant) throws InstanceNotFoundException;

  List<EmergencyQuadrantLog> findAllEmergencyQuadrantLogs();

  List<TeamQuadrantLog> findAllTeamQuadrantLogs();

  List<VehicleQuadrantLog> findAllVehicleQuadrantLogs();

  List<EmergencyQuadrantLog> findEmergenciesByEmergencyIdAndLinkedAt(Emergency emergency, LocalDateTime startDate,
      LocalDateTime endDate)
      throws InstanceNotFoundException;

  List<TeamQuadrantLog> findTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Quadrant quadrant,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException;

  List<VehicleQuadrantLog> findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Quadrant quadrant,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException;

  GlobalStatistics getGlobalStatisticsByEmergencyId(Emergency emergency)
      throws InstanceNotFoundException, ExtinguishedEmergencyException;
}
