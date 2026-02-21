package es.udc.fireproject.backend.model.services.logsmanagement;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.fire.FireIndex;
import es.udc.fireproject.backend.model.entities.fire.FireRepository;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LogManagementServiceImpl implements LogManagementService {

  @Autowired
  FireQuadrantLogRepository fireQuadrantLogRepository;
  @Autowired
  TeamQuadrantLogRepository teamQuadrantLogRepository;
  @Autowired
  VehicleQuadrantLogRepository vehicleQuadrantLogRepository;
  @Autowired
  private QuadrantRepository quadrantRepository;
  @Autowired
  private FireRepository fireRepository;

  @Override
  public FireQuadrantLog logFire(Fire fire, Quadrant quadrant) throws InstanceNotFoundException {
    return fireQuadrantLogRepository.save(
        new FireQuadrantLog(fire, quadrant, quadrant.getLinkedAt(),
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
  public List<FireQuadrantLog> findFiresByFireIdAndLinkedAt(Fire fire, LocalDateTime startDate, LocalDateTime endDate)
      throws InstanceNotFoundException {

    return fireQuadrantLogRepository.findByFireIdAndLinkedAtBetweenOrderByLinkedAt(fire.getId(), startDate, endDate);
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
  public GlobalStatistics getGlobalStatisticsByFireId(Fire fire)
      throws InstanceNotFoundException, ExtinguishedFireException {

    if (fire.getFireIndex() != FireIndex.EXTINGUIDO) {
      throw new ExtinguishedFireException(Fire.class.getSimpleName(), fire.getId().toString());

    }

    List<Integer> quadrantsGidList = fireQuadrantLogRepository.findQuadrantIdsByFireId(fire.getId());
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
