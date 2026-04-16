package es.udc.emergencyproject.backend.model.services.emergency;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.ResolvedEmergencyException;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;


public interface EmergencyManagementService {

  // QUADRANT SERVICES
  List<Quadrant> findAllQuadrants();

  List<Quadrant> findQuadrantsByEscala(String scale);

  Quadrant findQuadrantById(Integer gid) throws InstanceNotFoundException;

  Emergency linkEmergencyToQuadrants(Long emergencyId,
      List<Integer> quadrantGids) throws InstanceNotFoundException;

  Emergency linkEmergencyToPoint(Long emergencyId, Point location) throws InstanceNotFoundException;

  List<Quadrant> findQuadrantsWithActiveEmergency();

  Optional<Quadrant> findQuadrantByLocation(Point location);


  // EMERGENCY SERVICES
  List<Emergency> findAllEmergencies();

  Emergency findEmergencyById(Long id) throws InstanceNotFoundException;

  Emergency createEmergency(String description, Long emergencyTypeId, EmergencyIndex emergencyIndex);

  Emergency resolveEmergency(Long id)
      throws InstanceNotFoundException, ResolvedEmergencyException, AlreadyDismantledException;

  Emergency removeQuadrantByEmergencyId(Long id, Integer quadrantId)
      throws InstanceNotFoundException, ResolvedEmergencyException, AlreadyDismantledException;

  Emergency updateEmergency(Long id, String description, Long emergencyTypeId, EmergencyIndex emergencyIndex)
      throws InstanceNotFoundException, ResolvedEmergencyException;

  java.util.List<es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType> findAllEmergencyTypes();


}
