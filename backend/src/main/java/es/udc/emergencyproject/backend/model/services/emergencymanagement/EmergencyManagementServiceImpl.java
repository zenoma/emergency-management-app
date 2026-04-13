package es.udc.emergencyproject.backend.model.services.emergencymanagement;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.team.TeamRepository;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.VehicleRepository;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.ExtinguishedEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantNotLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.services.logsmanagement.LogManagementService;
import es.udc.emergencyproject.backend.model.services.utils.ConstraintValidator;
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
public class EmergencyManagementServiceImpl implements EmergencyManagementService {

  public static final String QUADRANT_NOT_FOUND = "Quadrant not found";
  public static final String EMERGENCY_NOT_FOUND = "Emergency not found";
  private static final String TEAM_NOT_FOUND = "Team not found";
  private static final String VEHICLE_NOT_FOUND = "Vehicle not found";

  private final LogManagementService logManagementService;

  private final EmergencyRepository emergencyRepository;
  private final VehicleRepository vehicleRepository;
  private final TeamRepository teamRepository;
  private final QuadrantRepository quadrantRepository;

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
  public List<Quadrant> findQuadrantsWithActiveEmergency() {
    return quadrantRepository.findByEmergencyIdNotNull();
  }

  @Override
  public Optional<Quadrant> findQuadrantByLocation(org.locationtech.jts.geom.Point location) {
    return quadrantRepository.findByContainingPoint(location);
  }

  @Override
  public Quadrant linkEmergency(Integer gid, Long id) throws InstanceNotFoundException {

    Quadrant quadrant = quadrantRepository.findById(gid)
        .orElseThrow(() -> new InstanceNotFoundException(QUADRANT_NOT_FOUND, gid));

    Emergency emergency = emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));

    if (quadrant.getEmergency() == null) {
      quadrant.setEmergency(emergency);
      quadrant.setLinkedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    return quadrantRepository.save(quadrant);


  }


  // EMERGENCY SERVICES
  @Override
  public List<Emergency> findAllEmergencies() {
    return emergencyRepository.findAllByOrderByExtinguishedAtDescIdAsc();
  }

  @Override
  public Emergency findEmergencyById(Long id) throws InstanceNotFoundException {

    return emergencyRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));
  }

  @Override
  public Emergency createEmergency(String description, String type, EmergencyIndex emergencyIndex) {
    Emergency emergency = new Emergency();
    emergency.setDescription(description);
    emergency.setType(type);
    emergency.setCreatedAt((LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
    emergency.setEmergencyIndex(EmergencyIndex.valueOf(emergencyIndex.name()));

    ConstraintValidator.validate(emergency);

    return emergencyRepository.save(emergency);
  }

  @Override
  public Emergency extinguishEmergency(Long id)
      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException {

    Emergency emergency = emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));

    if (emergency.getEmergencyIndex() == EmergencyIndex.EXTINGUIDO) {
      throw new ExtinguishedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    emergency.setEmergencyIndex(EmergencyIndex.EXTINGUIDO);
    emergency.setExtinguishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

    List<Quadrant> quadrants = quadrantRepository.findByEmergencyId(id);

    for (Quadrant quadrant : quadrants
    ) {
      logManagementService.logEmergency(emergency, quadrant);
      quadrant.setEmergency(null);
      quadrant.setLinkedAt(null);
      for (Team team : quadrant.getTeamList()) {
        retractTeam(team.getId());
      }
      for (Vehicle vehicle : quadrant.getVehicleList()) {
        retractVehicle(vehicle.getId());
      }
      quadrantRepository.save(quadrant);
    }

    return emergencyRepository.save(emergency);
  }

  @Override
  public Emergency extinguishQuadrantByEmergencyId(Long id, Integer quadrantId)
      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException {

    Emergency emergency = emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));

    if (emergency.getEmergencyIndex() == EmergencyIndex.EXTINGUIDO) {
      throw new ExtinguishedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    Quadrant quadrant = quadrantRepository.findById(quadrantId)
        .orElseThrow(() -> new InstanceNotFoundException(QUADRANT_NOT_FOUND, quadrantId));

    if (quadrant.getEmergency() == null || !Objects.equals(quadrant.getEmergency().getId(), emergency.getId())) {
      throw new QuadrantNotLinkedToEmergencyException(id, quadrantId);
    }

    logManagementService.logEmergency(emergency, quadrant);
    quadrant.setEmergency(null);
    quadrant.setLinkedAt(null);
    for (Team team : quadrant.getTeamList()) {
      retractTeam(team.getId());
    }
    for (Vehicle vehicle : quadrant.getVehicleList()) {
      retractVehicle(vehicle.getId());
    }
    quadrantRepository.save(quadrant);

    return emergencyRepository.save(emergency);
  }


  @Override
  public Emergency updateEmergency(Long id, String description, String type, EmergencyIndex emergencyIndex)
      throws InstanceNotFoundException, ExtinguishedEmergencyException {

    Emergency emergency = emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));

    if (emergencyIndex == EmergencyIndex.EXTINGUIDO) {
      throw new ExtinguishedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    if (emergency.getEmergencyIndex() != EmergencyIndex.EXTINGUIDO) {
      emergency.setDescription(description);
      emergency.setType(type);
      emergency.setEmergencyIndex(emergencyIndex);
    } else {
      throw new ExtinguishedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    return emergencyRepository.save(emergency);
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
