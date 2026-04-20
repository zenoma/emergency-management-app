package es.udc.emergencyproject.backend.model.services.assignment.impl;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentRepository;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.logs.GeneralLogEventType;
import es.udc.emergencyproject.backend.model.entities.resource.Resource;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceRepository;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceStatus;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceType;
import es.udc.emergencyproject.backend.model.exceptions.AssignmentAlreadyInStatusException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.InvalidAssignmentTransitionException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantNotLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.ResolvedEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.ResourceBusyException;
import es.udc.emergencyproject.backend.model.services.assignment.AssignmentService;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
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
  private final LogManagementService logManagementService;


  @Override
  public Assignment createAssignment(Long emergencyId, Integer quadrantId, Long resourceId, String notes)
      throws InstanceNotFoundException {

    var resource = resourceRepository.findByIdForUpdate(resourceId)
        .orElseThrow(() -> new InstanceNotFoundException("Resource not found", resourceId));

    if (resource.getStatus() != null && resource.getStatus() == ResourceStatus.BUSY) {
      throw new ResourceBusyException(Resource.class.getSimpleName(), resourceId.toString());
    }

    var emergency = emergencyRepository.findById(emergencyId)
        .orElseThrow(() -> new InstanceNotFoundException("Emergency not found", emergencyId));

    if (emergency.getEmergencyIndex() == EmergencyIndex.RESUELTO) {
      throw new ResolvedEmergencyException(
          "Emergency",
          emergencyId.toString());
    }

    Assignment a = new Assignment();
    a.setResource(resource);
    a.setNotes(notes);
    a.setAssignedAt(LocalDateTime.now());
    a.setStatus(AssignmentStatus.PENDING);

    boolean hasQuadrants = emergency.getEmergencyQuadrants() != null && !emergency.getEmergencyQuadrants().isEmpty();
    boolean hasPoint = emergency.getLocation() != null;

    if (!hasPoint && !hasQuadrants) {
      throw new IllegalArgumentException("Emergency has no linked point or quadrants");
    }

    if (hasQuadrants) {
      if (quadrantId == null) {
        throw new IllegalArgumentException("quadrantId is required when emergency has quadrants");
      }

      var eq = emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(emergencyId, quadrantId)
          .orElseThrow(() -> new QuadrantNotLinkedToEmergencyException(emergencyId, quadrantId));

      a.setEmergencyQuadrant(eq);
    }
    a.setEmergency(emergency);

    Assignment saved = assignmentRepository.save(a);

    try {
      logManagementService.registerAssignmentEvent(saved, GeneralLogEventType.ASSIGNMENT_CREATED,
          "Assignment created");
    } catch (Exception ignored) {
    }

    try {
      if (resource.getResourceType() == ResourceType.VEHICLE) {
        saved.setStatus(AssignmentStatus.ACCEPTED);
        saved.setAcceptedAt(LocalDateTime.now());
        saved = assignmentRepository.save(saved);

        if (resource.getStatus() != null && resource.getStatus() == ResourceStatus.BUSY) {
          throw new ResourceBusyException(Resource.class.getSimpleName(), resource.getId().toString());
        }
        resource.setStatus(ResourceStatus.BUSY);
        resource.setDeployAt(LocalDateTime.now());
        resourceRepository.save(resource);

        try {
          logManagementService.registerAssignmentEvent(saved, GeneralLogEventType.ASSIGNMENT_ACCEPTED,
              "Assignment auto-accepted for vehicle");
        } catch (Exception ignored) {
        }
      }
    } catch (ResourceBusyException rbe) {
      saved.setRemoved(Boolean.TRUE);
      assignmentRepository.save(saved);
      throw rbe;
    }

    return saved;
  }


  @Override
  public Assignment findAssignmentById(Long id) throws InstanceNotFoundException {
    return assignmentRepository.findByIdWithRelations(id)
        .orElseThrow(() -> new InstanceNotFoundException("Assignment not found", id));
  }


  @Override
  public List<Assignment> findByFilters(Integer quadrantId, Long emergencyId, Long resourceId) {
    boolean hasQuadrant = quadrantId != null;
    boolean hasEmergency = emergencyId != null;
    boolean hasResource = resourceId != null;

    if (hasResource && !hasEmergency && !hasQuadrant) {
      return assignmentRepository.findByResourceId(resourceId);
    }

    if (hasEmergency && !hasResource && !hasQuadrant) {
      return assignmentRepository.findByEmergencyId(emergencyId);
    }

    if (hasQuadrant && !hasEmergency && !hasResource) {
      return assignmentRepository.findByEmergencyQuadrantQuadrantId(quadrantId);
    }

    if (!hasQuadrant && !hasEmergency && !hasResource) {
      return assignmentRepository.findByFilters(null, null, null);
    }

    return assignmentRepository.findByFilters(quadrantId, emergencyId, resourceId);
  }

  @Override
  public Assignment updateStatus(Long id, AssignmentStatus status) throws InstanceNotFoundException {
    Assignment a = findAssignmentById(id);

    AssignmentStatus previous = a.getStatus();

    if (previous == status) {
      throw new AssignmentAlreadyInStatusException(status.name());
    }

    boolean valid = (previous == AssignmentStatus.PENDING && status == AssignmentStatus.ACCEPTED)
        || (previous == AssignmentStatus.ACCEPTED && status == AssignmentStatus.COMPLETED);

    if (!valid) {
      throw new InvalidAssignmentTransitionException(previous.name(),
          status.name());
    }

    a.setStatus(status);
    if (status == AssignmentStatus.ACCEPTED) {
      a.setAcceptedAt(LocalDateTime.now());
    }
    if (status == AssignmentStatus.COMPLETED) {
      a.setCompletedAt(LocalDateTime.now());
    }

    Assignment saved = assignmentRepository.save(a);

    Long resourceId = a.getResource().getId();
    var resource = resourceRepository.findByIdForUpdate(resourceId)
        .orElseThrow(() -> new InstanceNotFoundException("Resource not found", resourceId));

    if (status == AssignmentStatus.ACCEPTED) {
      if (resource.getStatus() != null && resource.getStatus() == ResourceStatus.BUSY) {
        throw new ResourceBusyException(Resource.class.getSimpleName(), resourceId.toString());
      }

      resource.setStatus(ResourceStatus.BUSY);
      resource.setDeployAt(LocalDateTime.now());
      resourceRepository.save(resource);

      try {
        logManagementService.registerAssignmentEvent(a, GeneralLogEventType.ASSIGNMENT_ACCEPTED,
            "Assignment accepted");
      } catch (Exception ignored) {
      }
    }

    if (status == AssignmentStatus.COMPLETED) {
      resource.setStatus(ResourceStatus.AVAILABLE);
      resource.setDeployAt(null);
      resourceRepository.save(resource);

      try {
        logManagementService.registerAssignmentEvent(a, GeneralLogEventType.ASSIGNMENT_COMPLETED,
            "Assignment completed");
      } catch (Exception ignored) {
      }
    }

    return saved;
  }

  @Override
  public List<Assignment> findByEmergencyQuadrantQuadrantId(Integer quadrantId) {
    return assignmentRepository.findByEmergencyQuadrantQuadrantId(quadrantId);
  }

  @Override
  public List<Assignment> findByEmergencyId(Long emergencyId) {
    return assignmentRepository.findByEmergencyId(emergencyId);
  }

  @Override
  public List<Assignment> findByResourceId(Long resourceId) {
    return assignmentRepository.findByResourceId(resourceId);
  }

  @Override
  public List<Assignment> findByQuadrantGid(Integer quadrantGid) {
    return assignmentRepository.findByQuadrantGid(quadrantGid);
  }

  @Override
  public void deleteAssignment(Long id) throws InstanceNotFoundException {
    Assignment a = findAssignmentById(id);
    if (a.getStatus() != AssignmentStatus.PENDING) {
      throw new InvalidAssignmentTransitionException(a.getStatus() == null ? "null" : a.getStatus().name(),
          AssignmentStatus.PENDING.name());
    }

    a.setRemoved(Boolean.TRUE);
    assignmentRepository.save(a);

    try {
      logManagementService.registerAssignmentEvent(a, GeneralLogEventType.ASSIGNMENT_DELETED,
          "Assignment deleted");
    } catch (Exception ignored) {
    }
  }
}
