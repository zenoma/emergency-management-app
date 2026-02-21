package es.udc.fireproject.backend.model.services.logsmanagement;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.logs.FireQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.GlobalStatistics;
import es.udc.fireproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public interface LogManagementService {

  FireQuadrantLog logFire(Fire fire,
      Quadrant quadrant) throws InstanceNotFoundException;

  TeamQuadrantLog logTeam(Team team,
      Quadrant quadrant) throws InstanceNotFoundException;

  VehicleQuadrantLog logVehicle(Vehicle vehicle,
      Quadrant quadrant) throws InstanceNotFoundException;

  List<FireQuadrantLog> findAllFireQuadrantLogs();

  List<TeamQuadrantLog> findAllTeamQuadrantLogs();

  List<VehicleQuadrantLog> findAllVehicleQuadrantLogs();

  List<FireQuadrantLog> findFiresByFireIdAndLinkedAt(Fire fire, LocalDateTime startDate, LocalDateTime endDate)
      throws InstanceNotFoundException;

  List<TeamQuadrantLog> findTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Quadrant quadrant,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException;

  List<VehicleQuadrantLog> findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Quadrant quadrant,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException;

  GlobalStatistics getGlobalStatisticsByFireId(Fire fire) throws InstanceNotFoundException, ExtinguishedFireException;
}
