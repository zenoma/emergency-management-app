package es.udc.emergencyproject.backend.model.services.assignment.impl;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentRepository;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceRepository;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.assignment.AssignmentService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

  private final AssignmentRepository assignmentRepository;
  private final EmergencyQuadrantRepository emergencyQuadrantRepository;
  private final ResourceRepository resourceRepository;
  private final EmergencyRepository emergencyRepository;

  // TODO: Hay que guardar registro de todos las operaciones de asignación

  @Override
  public Assignment createAssignment(Long emergencyId, Long emergencyQuadrantId, Long resourceId, String notes)
      throws InstanceNotFoundException {

    var resource = resourceRepository.findById(resourceId)
        .orElseThrow(() -> new InstanceNotFoundException("Resource not found", resourceId));

    // Resolve emergency
    var emergency = emergencyRepository.findById(emergencyId)
        .orElseThrow(() -> new InstanceNotFoundException("Emergency not found", emergencyId));

    Assignment a = new Assignment();
    a.setResource(resource);
    a.setNotes(notes);
    a.setAssignedAt(LocalDateTime.now());
    a.setStatus(AssignmentStatus.PENDING);

    if (emergencyQuadrantId != null) {
      var eq = emergencyQuadrantRepository.findById(emergencyQuadrantId)
          .orElseThrow(() -> new InstanceNotFoundException("EmergencyQuadrant not found", emergencyQuadrantId));
      if (!eq.getEmergency().getId().equals(emergency.getId())) {
        throw new InstanceNotFoundException("EmergencyQuadrant does not belong to emergency", emergencyQuadrantId);
      }
      a.setEmergencyQuadrant(eq);
    } else {
      if (emergency.getLocation() == null) {
        throw new InstanceNotFoundException("Emergency is not point-based and no quadrant specified", emergencyId);
      }
      a.setEmergency(emergency);
    }

    return assignmentRepository.save(a);
  }

  @Override
  public Assignment findAssignmentById(Long id) throws InstanceNotFoundException {
    return assignmentRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException("Assignment not found", id));
  }

  @Override
  public List<Assignment> findByEmergencyQuadrantId(Long emergencyQuadrantId) {
    return assignmentRepository.findByEmergencyQuadrantId(emergencyQuadrantId);
  }

  @Override
  public List<Assignment> findByResourceId(Long resourceId) {
    return assignmentRepository.findByResourceId(resourceId);
  }

  @Override
  public List<Assignment> findByEmergencyId(Long emergencyId) {
    return assignmentRepository.findByEmergencyId(emergencyId);
  }

  @Override
  public Assignment updateStatus(Long id, AssignmentStatus status) throws InstanceNotFoundException {
    Assignment a = findAssignmentById(id);
    a.setStatus(status);
    //TODO: cuando cambia un assignment de estado tienen que cambiar los recursos de estado
    return assignmentRepository.save(a);
  }

  @Override
  public void deleteAssignment(Long id) throws InstanceNotFoundException {
    Assignment a = findAssignmentById(id);
    assignmentRepository.delete(a);
  }
}
