package es.udc.emergencyproject.backend.model.services.assignment;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import java.util.List;

public interface AssignmentService {

  Assignment createAssignment(Long emergencyId, Long emergencyQuadrantId, Long resourceId, String notes)
      throws InstanceNotFoundException;

  Assignment findAssignmentById(Long id) throws InstanceNotFoundException;

  List<Assignment> findByEmergencyQuadrantId(Long emergencyQuadrantId);

  List<Assignment> findByEmergencyId(Long emergencyId);

  List<Assignment> findByResourceId(Long resourceId);

  Assignment updateStatus(Long id, AssignmentStatus status) throws InstanceNotFoundException;

  void deleteAssignment(Long id) throws InstanceNotFoundException;


}
