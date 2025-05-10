package es.udc.fireproject.backend.model.services.logsmanagement;

import es.udc.fireproject.backend.model.entities.logs.FireQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.GlobalStatistics;
import es.udc.fireproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public interface LogManagementService {

  FireQuadrantLog logFire(Long fireId, Integer quadrantId) throws InstanceNotFoundException;

  TeamQuadrantLog logTeam(Long teamId, Integer quadrantId) throws InstanceNotFoundException;

  VehicleQuadrantLog logVehicle(Long vehicleId, Integer quadrantId) throws InstanceNotFoundException;

  List<FireQuadrantLog> findAllFireQuadrantLogs();

  List<TeamQuadrantLog> findAllTeamQuadrantLogs();

  List<VehicleQuadrantLog> findAllVehicleQuadrantLogs();

  List<FireQuadrantLog> findFiresByFireIdAndLinkedAt(Long fireId, LocalDateTime startDate, LocalDateTime endDate)
      throws InstanceNotFoundException;

  List<TeamQuadrantLog> findTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Integer quadrantId,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException;

  List<VehicleQuadrantLog> findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Integer quadrantId,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException;

  GlobalStatistics getGlobalStatisticsByFireId(Long fireId) throws InstanceNotFoundException, ExtinguishedFireException;
}
