package es.udc.fireproject.backend.model.services.logsmanagement;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.fire.FireIndex;
import es.udc.fireproject.backend.model.entities.logs.FireQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.FireQuadrantLogRepository;
import es.udc.fireproject.backend.model.entities.logs.GlobalStatistics;
import es.udc.fireproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.TeamQuadrantLogRepository;
import es.udc.fireproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.VehicleQuadrantLogRepository;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementServiceImpl;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogManagementServiceImpl implements LogManagementService {

  @Autowired
  FireQuadrantLogRepository fireQuadrantLogRepository;
  @Autowired
  TeamQuadrantLogRepository teamQuadrantLogRepository;
  @Autowired
  VehicleQuadrantLogRepository vehicleQuadrantLogRepository;

  @Autowired
  FireManagementServiceImpl fireManagementService;
  @Autowired
  PersonalManagementServiceImpl personalManagementService;

  @Autowired
  private QuadrantRepository quadrantRepository;


  @Override
  public FireQuadrantLog logFire(Long fireId, Integer quadrantId) throws InstanceNotFoundException {

    Quadrant quadrant = fireManagementService.findQuadrantById(quadrantId);

    Fire fire = fireManagementService.findFireById(fireId);

    return fireQuadrantLogRepository.save(
        new FireQuadrantLog(fire, quadrant, quadrant.getLinkedAt(), LocalDateTime.now()));

  }


  @Override
  public TeamQuadrantLog logTeam(Long teamId, Integer quadrantId) throws InstanceNotFoundException {
    Quadrant quadrant = fireManagementService.findQuadrantById(quadrantId);
    Team team = personalManagementService.findTeamById(teamId);

    return teamQuadrantLogRepository.save(new TeamQuadrantLog(team, quadrant, team.getDeployAt(), LocalDateTime.now()));

  }


  @Override
  public VehicleQuadrantLog logVehicle(Long vehicleId, Integer quadrantId) throws InstanceNotFoundException {
    Quadrant quadrant = fireManagementService.findQuadrantById(quadrantId);
    Vehicle vehicle = personalManagementService.findVehicleById(vehicleId);

    return vehicleQuadrantLogRepository.save(
        new VehicleQuadrantLog(vehicle, quadrant, vehicle.getDeployAt(), LocalDateTime.now()));
  }

  @Override
  public List<FireQuadrantLog> findAllFireQuadrantLogs() {
    return fireQuadrantLogRepository.findAll();
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
  public List<FireQuadrantLog> findFiresByFireIdAndLinkedAt(Long fireId, LocalDateTime startDate, LocalDateTime endDate)
      throws InstanceNotFoundException {
    Fire fire = fireManagementService.findFireById(fireId);

    return fireQuadrantLogRepository.findByFireIdAndLinkedAtBetweenOrderByLinkedAt(fireId, startDate, endDate);
  }

  @Override
  public List<TeamQuadrantLog> findTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Integer quadrantId,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException {
    Quadrant quadrant = fireManagementService.findQuadrantById(quadrantId);

    return teamQuadrantLogRepository.findByQuadrantIdAndDeployAtBetweenOrderByDeployAt(quadrantId, startDate, endDate);
  }

  @Override
  public List<VehicleQuadrantLog> findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Integer quadrantId,
      LocalDateTime startDate, LocalDateTime endDate) throws InstanceNotFoundException {
    Quadrant quadrant = fireManagementService.findQuadrantById(quadrantId);

    return vehicleQuadrantLogRepository.findByQuadrantIdAndDeployAtBetweenOrderByDeployAt(quadrantId, startDate,
        endDate);
  }

  @Override
  public GlobalStatistics getGlobalStatisticsByFireId(Long fireId)
      throws InstanceNotFoundException, ExtinguishedFireException {
    Fire fire = fireManagementService.findFireById(fireId);

    if (fire.getFireIndex() != FireIndex.EXTINGUIDO) {
      throw new ExtinguishedFireException(Fire.class.getSimpleName(), fire.getId().toString());

    }

    List<Integer> quadrantsGidList = fireQuadrantLogRepository.findQuadrantIdsByFireId(fireId);
    Set<Integer> uniqueQuadrants = new HashSet<>(quadrantsGidList);
    List<Integer> uniqueQuadrantsList = new ArrayList<>(uniqueQuadrants);
    Integer affectedQuadrants = uniqueQuadrantsList.size();

    List<Long> teamsMobilized = new ArrayList<>();

    for (Integer quadrantId : quadrantsGidList) {
      List<Long> teamsIdList = teamQuadrantLogRepository.findTeamsIdsByQuadrantsGid(quadrantId);
      teamsMobilized.addAll(teamsIdList);
    }

    List<Long> vehiclesMobilized = new ArrayList<>();

    for (Integer quadrantId : quadrantsGidList) {
      List<Long> vehiclesIdList = vehicleQuadrantLogRepository.findVehiclesIdsByQuadrantsGid(quadrantId);
      vehiclesMobilized.addAll(vehiclesIdList);
    }

    Double maxBurnedHectares =
        uniqueQuadrantsList.isEmpty() ? 0 : quadrantRepository.findHectaresByQuadrantIds(uniqueQuadrantsList);

    return new GlobalStatistics(teamsMobilized.size(), vehiclesMobilized.size(), maxBurnedHectares, affectedQuadrants);
  }

}
