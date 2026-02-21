package es.udc.fireproject.backend.model.services.firemanagement;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.fire.FireIndex;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;

public interface FireManagementService {

  // CUADRANT SERVICES
  List<Quadrant> findAllQuadrants();

  List<Quadrant> findQuadrantsByEscala(String scale);

  Quadrant findQuadrantById(Integer gid) throws InstanceNotFoundException;

  Quadrant linkFire(Integer gid, Long id) throws InstanceNotFoundException;

  List<Quadrant> findQuadrantsWithActiveFire();

  Optional<Quadrant> findQuadrantByLocation(Point location);


  // FIRE SERVICES
  List<Fire> findAllFires();

  Fire findFireById(Long id) throws InstanceNotFoundException;

  Fire createFire(String description, String type, FireIndex fireIndex);

  Fire extinguishFire(Long id)
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException, AlreadyDismantledException;

  Fire extinguishQuadrantByFireId(Long id, Integer quadrantId)
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException, AlreadyDismantledException;

  Fire updateFire(Long id, String description, String type, FireIndex fireIndex)
      throws InstanceNotFoundException, ExtinguishedFireException;

  // EXTINCTION SERVICES

  Team deployTeam(Long teamId, Integer gid) throws InstanceNotFoundException, AlreadyDismantledException;

  Team retractTeam(Long teamId) throws InstanceNotFoundException, AlreadyDismantledException;

  Vehicle deployVehicle(Long vehicleId, Integer gid) throws InstanceNotFoundException, AlreadyDismantledException;

  Vehicle retractVehicle(Long vehicleId) throws InstanceNotFoundException, AlreadyDismantledException;


}
