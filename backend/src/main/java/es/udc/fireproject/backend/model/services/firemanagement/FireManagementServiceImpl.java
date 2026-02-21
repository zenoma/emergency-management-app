package es.udc.fireproject.backend.model.services.firemanagement;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.fire.FireIndex;
import es.udc.fireproject.backend.model.entities.fire.FireRepository;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.team.TeamRepository;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.entities.vehicle.VehicleRepository;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.QuadrantNotLinkedToFireException;
import es.udc.fireproject.backend.model.services.logsmanagement.LogManagementService;
import es.udc.fireproject.backend.model.services.utils.ConstraintValidator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FireManagementServiceImpl implements FireManagementService {

  public static final String QUADRANT_NOT_FOUND = "Quadrant not found";
  public static final String FIRE_NOT_FOUND = "Fire not found";
  public static final String TEAM_NOT_FOUND = "Team not found";
  public static final String VEHICLE_NOT_FOUND = "Vehicle not found";


  private final QuadrantRepository quadrantRepository;
  private final FireRepository fireRepository;
  private final TeamRepository teamRepository;
  private final VehicleRepository vehicleRepository;
  private final LogManagementService logManagementService;

  // QUADRANT SERVICES
  @Override
  public List<Quadrant> findAllQuadrants() {
    return quadrantRepository.findAll();
  }

  @Override
  public List<Quadrant> findQuadrantsByEscala(String scale) {
    return quadrantRepository.findByEscala(scale);
  }

  @Override
  public Quadrant findQuadrantById(Integer gid) throws InstanceNotFoundException {
    return quadrantRepository.findById(gid).orElseThrow(() -> new InstanceNotFoundException(QUADRANT_NOT_FOUND, gid));

  }

  @Override
  public List<Quadrant> findQuadrantsWithActiveFire() {
    return quadrantRepository.findByFireIdNotNull();
  }

  @Override
  public Optional<Quadrant> findQuadrantByLocation(org.locationtech.jts.geom.Point location) {
    return quadrantRepository.findByContainingPoint(location);
  }

  @Override
  public Quadrant linkFire(Integer gid, Long id) throws InstanceNotFoundException {

    Quadrant quadrant = quadrantRepository.findById(gid)
        .orElseThrow(() -> new InstanceNotFoundException(QUADRANT_NOT_FOUND, gid));

    Fire fire = fireRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(FIRE_NOT_FOUND, id));

    if (quadrant.getFire() == null) {
      quadrant.setFire(fire);
      quadrant.setLinkedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    return quadrantRepository.save(quadrant);


  }


  // FIRE SERVICES
  @Override
  public List<Fire> findAllFires() {
    return fireRepository.findAllByOrderByExtinguishedAtDescIdAsc();
  }

  @Override
  public Fire findFireById(Long id) throws InstanceNotFoundException {

    return fireRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(FIRE_NOT_FOUND, id));
  }

  @Override
  public Fire createFire(String description, String type, FireIndex fireIndex) {
    Fire fire = new Fire(description, type, fireIndex);
    fire.setCreatedAt((LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
    fire.setFireIndex(FireIndex.valueOf(fireIndex.name()));

    ConstraintValidator.validate(fire);

    return fireRepository.save(fire);
  }

  @Override
  public Fire extinguishFire(Long id)
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException, AlreadyDismantledException {

    Fire fire = fireRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(FIRE_NOT_FOUND, id));

    if (fire.getFireIndex() == FireIndex.EXTINGUIDO) {
      throw new ExtinguishedFireException(Fire.class.getSimpleName(), fire.getId().toString());
    }

    fire.setFireIndex(FireIndex.EXTINGUIDO);
    fire.setExtinguishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

    List<Quadrant> quadrants = quadrantRepository.findByFireId(id);

    for (Quadrant quadrant : quadrants
    ) {
      logManagementService.logFire(fire, quadrant);
      quadrant.setFire(null);
      quadrant.setLinkedAt(null);
      for (Team team : quadrant.getTeamList()) {
        retractTeam(team.getId());
      }
      for (Vehicle vehicle : quadrant.getVehicleList()) {
        retractVehicle(vehicle.getId());
      }
      quadrantRepository.save(quadrant);
    }

    return fireRepository.save(fire);
  }

  @Override
  public Fire extinguishQuadrantByFireId(Long id, Integer quadrantId)
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException {

    Fire fire = fireRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(FIRE_NOT_FOUND, id));

    if (fire.getFireIndex() == FireIndex.EXTINGUIDO) {
      throw new ExtinguishedFireException(Fire.class.getSimpleName(), fire.getId().toString());
    }

    Quadrant quadrant = quadrantRepository.findById(quadrantId)
        .orElseThrow(() -> new InstanceNotFoundException(QUADRANT_NOT_FOUND, quadrantId));

    if (quadrant.getFire() == null || !Objects.equals(quadrant.getFire().getId(), fire.getId())) {
      throw new QuadrantNotLinkedToFireException(id, quadrantId);
    }

    logManagementService.logFire(fire, quadrant);
    quadrant.setFire(null);
    quadrant.setLinkedAt(null);
    for (Team team : quadrant.getTeamList()) {
      retractTeam(team.getId());
    }
    for (Vehicle vehicle : quadrant.getVehicleList()) {
      retractVehicle(vehicle.getId());
    }
    quadrantRepository.save(quadrant);

    return fireRepository.save(fire);
  }


  @Override
  public Fire updateFire(Long id, String description, String type, FireIndex fireIndex)
      throws InstanceNotFoundException, ExtinguishedFireException {

    Fire fire = fireRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(FIRE_NOT_FOUND, id));

    if (fireIndex == FireIndex.EXTINGUIDO) {
      throw new ExtinguishedFireException(Fire.class.getSimpleName(), fire.getId().toString());
    }

    if (fire.getFireIndex() != FireIndex.EXTINGUIDO) {
      fire.setDescription(description);
      fire.setType(type);
      fire.setFireIndex(fireIndex);
    } else {
      throw new ExtinguishedFireException(Fire.class.getSimpleName(), fire.getId().toString());
    }

    return fireRepository.save(fire);
  }


  // EXTINCTION SERVICES
  @Override
  public Team deployTeam(Long teamId, Integer gid) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId));
    if (team.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }

    Quadrant quadrant = quadrantRepository.findById(gid)
        .orElseThrow(() -> new InstanceNotFoundException(QUADRANT_NOT_FOUND, gid));

    if (team.getQuadrant() != null && !Objects.equals(team.getQuadrant().getId(), gid)) {
      retractTeam(teamId);
    }

    team.setDeployAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    team.setQuadrant(quadrant);

    return teamRepository.save(team);

  }

  @Override
  public Team retractTeam(Long teamId) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId));
    if (team.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }

    if (team.getQuadrant() != null) {
      logManagementService.logTeam(team, team.getQuadrant());
      team.setDeployAt(null);
      team.setQuadrant(null);
    }

    return teamRepository.save(team);

  }

  @Override
  public Vehicle deployVehicle(Long vehicleId, Integer gid)
      throws InstanceNotFoundException, AlreadyDismantledException {
    Vehicle vehicle = vehicleRepository.findById(vehicleId)
        .orElseThrow(() -> new InstanceNotFoundException(VEHICLE_NOT_FOUND, vehicleId));
    if (vehicle.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Vehicle.class.getSimpleName(), vehicle.getVehiclePlate());
    }

    Quadrant quadrant = quadrantRepository.findById(gid)
        .orElseThrow(() -> new InstanceNotFoundException(QUADRANT_NOT_FOUND, gid));

    if (vehicle.getQuadrant() != null && !Objects.equals(vehicle.getQuadrant().getId(), gid)) {
      retractVehicle(vehicleId);
    }

    vehicle.setDeployAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    vehicle.setQuadrant(quadrant);

    return vehicleRepository.save(vehicle);

  }

  @Override
  public Vehicle retractVehicle(Long vehicleId) throws InstanceNotFoundException, AlreadyDismantledException {
    Vehicle vehicle = vehicleRepository.findById(vehicleId)
        .orElseThrow(() -> new InstanceNotFoundException(VEHICLE_NOT_FOUND, vehicleId));
    if (vehicle.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Vehicle.class.getSimpleName(), vehicle.getVehiclePlate());
    }

    if (vehicle.getQuadrant() != null) {
      logManagementService.logVehicle(vehicle, vehicle.getQuadrant());
      vehicle.setDeployAt(null);
      vehicle.setQuadrant(null);
    }

    return vehicleRepository.save(vehicle);

  }


}
