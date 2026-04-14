package es.udc.emergencyproject.backend.model.services.emergencymanagement;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.ExtinguishedEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;


public interface EmergencyManagementService {

  // QUADRANT SERVICES
  List<Quadrant> findAllQuadrants();

  List<Quadrant> findQuadrantsByEscala(String scale);

  Quadrant findQuadrantById(Integer gid) throws InstanceNotFoundException;

  Quadrant linkEmergency(Integer gid, Long id) throws InstanceNotFoundException;

  List<Quadrant> findQuadrantsWithActiveEmergency();

  Optional<Quadrant> findQuadrantByLocation(Point location);


  // EMERGENCY SERVICES
  List<Emergency> findAllEmergencies();

  Emergency findEmergencyById(Long id) throws InstanceNotFoundException;

  Emergency createEmergency(String description, String type, EmergencyIndex emergencyIndex);

  Emergency extinguishEmergency(Long id)
      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException;

  Emergency extinguishQuadrantByEmergencyId(Long id, Integer quadrantId)
      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException;

  Emergency updateEmergency(Long id, String description, String type, EmergencyIndex fireIndex)
      throws InstanceNotFoundException, ExtinguishedEmergencyException;

  // EXTINCTION SERVICES

  Team deployTeam(Long teamId, Integer gid) throws InstanceNotFoundException, AlreadyDismantledException;

  Team retractTeam(Long teamId) throws InstanceNotFoundException, AlreadyDismantledException;

  Vehicle deployVehicle(Long vehicleId, Integer gid) throws InstanceNotFoundException, AlreadyDismantledException;

  Vehicle retractVehicle(Long vehicleId) throws InstanceNotFoundException, AlreadyDismantledException;


}
