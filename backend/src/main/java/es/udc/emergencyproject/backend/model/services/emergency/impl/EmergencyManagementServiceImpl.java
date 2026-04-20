package es.udc.emergencyproject.backend.model.services.emergency.impl;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentRepository;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrant;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRepository;
import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLog;
import es.udc.emergencyproject.backend.model.entities.logs.GeneralLogEventType;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceRepository;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceStatus;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.EmergencyAlreadyLinkedToPointException;
import es.udc.emergencyproject.backend.model.exceptions.EmergencyAlreadyLinkedToQuadrantsException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantAlreadyLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantNotLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.ResolvedEmergencyException;
import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import es.udc.emergencyproject.backend.model.services.utils.ConstraintValidator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmergencyManagementServiceImpl implements EmergencyManagementService {

  public static final String QUADRANT_NOT_FOUND = "Quadrant not found";
  public static final String EMERGENCY_NOT_FOUND = "Emergency not found";

  private final LogManagementService logManagementService;
  private final EmergencyRepository emergencyRepository;
  private final QuadrantRepository quadrantRepository;
  private final EmergencyQuadrantRepository emergencyQuadrantRepository;
  private final EmergencyTypeRepository emergencyTypeRepository;
  private final AssignmentRepository assignmentRepository;
  private final ResourceRepository resourceRepository;

  // QUADRANT SERVICE
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
    List<EmergencyQuadrant> eqs = emergencyQuadrantRepository.findAll();
    List<Quadrant> result = new ArrayList<>();
    for (EmergencyQuadrant eq : eqs) {
      try {
        if (eq.getEmergency() != null && eq.getEmergency().getEmergencyIndex() != null
            && eq.getEmergency().getEmergencyIndex() == EmergencyIndex.RESUELTO) {
          continue;
        }
      } catch (Exception ignored) {
      }
      if (eq.getQuadrant() != null && !result.contains(eq.getQuadrant())) {
        result.add(eq.getQuadrant());
      }
    }
    return result;
  }

  @Override
  public Optional<Quadrant> findQuadrantByLocation(Point location) {
    return quadrantRepository.findByContainingPoint(location);
  }

  @Override
  public Emergency linkEmergencyToQuadrants(Long emergencyId, List<Integer> quadrantGids)
      throws InstanceNotFoundException {

    var emergency = emergencyRepository.findById(emergencyId)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, emergencyId));

    if (emergency.getEmergencyIndex() == EmergencyIndex.RESUELTO) {
      throw new ResolvedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    if (emergency.getLocation() != null) {
      throw new EmergencyAlreadyLinkedToPointException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    List<Quadrant> linked = new ArrayList<>();
    for (Integer gid : quadrantGids) {
      Quadrant quadrant = quadrantRepository.findById(gid)
          .orElseThrow(() -> new InstanceNotFoundException(QUADRANT_NOT_FOUND, gid));

      Optional<EmergencyQuadrant> existing = emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(emergencyId,
          gid);
      if (existing.isPresent()) {

        throw new QuadrantAlreadyLinkedToEmergencyException(emergencyId, gid);
      } else {
        EmergencyQuadrant eq = new EmergencyQuadrant();
        eq.setEmergency(emergency);
        eq.setQuadrant(quadrant);
        eq.setLinkedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        emergencyQuadrantRepository.save(eq);
        // log general event
        try {
          var gl = new AssignmentLog(null, emergency, quadrant, null,
              GeneralLogEventType.EMERGENCY_LINKED_QUADRANT, eq.getLinkedAt(), "Linked quadrant " + quadrant.getId());
          logManagementService.logGeneral(gl);
        } catch (Exception ignored) {
        }
      }
      linked.add(quadrant);
    }

    return emergencyRepository.save(emergency);
  }

  @Override
  public Emergency linkEmergencyToPoint(Long emergencyId, Point location)
      throws InstanceNotFoundException {
    var emergency = emergencyRepository.findById(emergencyId)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, emergencyId));
    if (emergency.getEmergencyIndex() == EmergencyIndex.RESUELTO) {
      throw new ResolvedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    List<EmergencyQuadrant> quadrants = emergencyQuadrantRepository.findByEmergencyId(emergencyId);
    if (quadrants != null && !quadrants.isEmpty()) {
      throw new EmergencyAlreadyLinkedToQuadrantsException(
          Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    if (emergency.getLocation() != null) {
      throw new EmergencyAlreadyLinkedToPointException(
          Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    emergency.setLocation(location);
    Emergency saved = emergencyRepository.save(emergency);
    try {
      var gl = new AssignmentLog(null, saved, null, null, GeneralLogEventType.EMERGENCY_LINKED_POINT,
          LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "Linked point");
      logManagementService.logGeneral(gl);
    } catch (Exception ignored) {
    }
    return saved;
  }


  // EMERGENCY SERVICES
  @Override
  public List<Emergency> findAllEmergencies() {
    return emergencyRepository.findAllByOrderByResolvedAtDescIdAsc();
  }

  @Override
  public Emergency findEmergencyById(Long id) throws InstanceNotFoundException {

    return emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));
  }

  @Override
  public Emergency createEmergency(String description, Long emergencyTypeId, EmergencyIndex emergencyIndex) {
    Emergency emergency = new Emergency();
    emergency.setDescription(description);
    if (emergencyTypeId == null) {
      throw new IllegalArgumentException("Emergency type id is required");
    }
    var et = emergencyTypeRepository.findById(emergencyTypeId).orElse(null);
    if (et == null) {
      throw new InstanceNotFoundException("EmergencyType", emergencyTypeId);
    }
    emergency.setEmergencyType(et);
    emergency.setCreatedAt((LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
    emergency.setEmergencyIndex(EmergencyIndex.valueOf(emergencyIndex.name()));

    ConstraintValidator.validate(emergency);

    Emergency saved = emergencyRepository.save(emergency);
    try {
      var gl = new AssignmentLog(null, saved, null, null, GeneralLogEventType.EMERGENCY_CREATED,
          LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "Emergency created");
      logManagementService.logGeneral(gl);
    } catch (Exception ignored) {
    }
    return saved;
  }

  @Override
  public Emergency resolveEmergency(Long id)
      throws InstanceNotFoundException, ResolvedEmergencyException, AlreadyDismantledException {

    Emergency emergency = emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));

    if (emergency.getEmergencyIndex() == EmergencyIndex.RESUELTO) {
      throw new ResolvedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    emergency.setEmergencyIndex(EmergencyIndex.RESUELTO);
    emergency.setResolvedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

    List<Assignment> assignments = assignmentRepository.findByEmergencyId(emergency.getId());
    for (Assignment a : assignments) {
      try {
        if (a.getStatus() != null && a.getStatus() != AssignmentStatus.COMPLETED) {
          if (a.getStatus() == AssignmentStatus.ACCEPTED) {
            a.setStatus(AssignmentStatus.COMPLETED);
            a.setCompletedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            assignmentRepository.save(a);
            try {
              logManagementService.registerAssignmentEvent(a, GeneralLogEventType.ASSIGNMENT_COMPLETED,
                  "Assignment auto-completed due to emergency resolved");
            } catch (Exception ignored) {
            }

            if (a.getResource() != null) {
              Long resourceId = a.getResource().getId();
              var resource = resourceRepository.findByIdForUpdate(resourceId).orElse(null);
              if (resource != null) {
                resource.setStatus(ResourceStatus.AVAILABLE);
                resource.setDeployAt(null);
                resourceRepository.save(resource);

                try {
                  logManagementService.registerAssignmentEvent(a, GeneralLogEventType.RESOURCE_RETRACTED,
                      "Resource retracted due to emergency resolved");
                } catch (Exception ignored) {
                }
              }
            }
          } else {
            // For assignments that were not accepted (e.g. PENDING), perform a soft delete
            a.setRemoved(Boolean.TRUE);
            assignmentRepository.save(a);
            try {
              logManagementService.registerAssignmentEvent(a, GeneralLogEventType.ASSIGNMENT_DELETED,
                  "Assignment auto-deleted due to emergency resolved");
            } catch (Exception ignored) {
            }
          }
        }
      } catch (Exception ignored) {
      }
    }

    return emergencyRepository.save(emergency);
  }

  @Override
  public Emergency removeQuadrantByEmergencyId(Long id, Integer quadrantId)
      throws InstanceNotFoundException, ResolvedEmergencyException, AlreadyDismantledException {

    Emergency emergency = emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));

    if (emergency.getEmergencyIndex() == EmergencyIndex.RESUELTO) {
      throw new ResolvedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    Optional<EmergencyQuadrant> eq = emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(id, quadrantId);
    if (eq.isEmpty()) {
      throw new QuadrantNotLinkedToEmergencyException(id, quadrantId);
    }

    //TODO: cuando se soluciona una emergencia hay que liberar los recursos de ese cuadrante

    emergencyQuadrantRepository.delete(eq.orElseThrow());
    try {
      var quadrant = quadrantRepository.findById(quadrantId).orElse(null);
      var gl = new AssignmentLog(null, emergency, quadrant, null, GeneralLogEventType.EMERGENCY_UNLINKED_QUADRANT,
          java.time.LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), "Unlinked quadrant " + quadrantId);
      logManagementService.logGeneral(gl);
    } catch (Exception ignored) {
    }
    return emergencyRepository.save(emergency);
  }


  @Override
  public Emergency updateEmergency(Long id, String description, Long emergencyTypeId, EmergencyIndex emergencyIndex) {

    Emergency emergency = emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));

    if (emergency.getEmergencyIndex() != EmergencyIndex.RESUELTO) {
      emergency.setDescription(description);
      if (emergencyIndex == EmergencyIndex.RESUELTO) {
        emergency.setResolvedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
      }
      emergency.setEmergencyIndex(emergencyIndex);
      if (emergencyTypeId != null) {
        var et = emergencyTypeRepository.findById(emergencyTypeId).orElse(null);
        if (et == null) {
          throw new InstanceNotFoundException("EmergencyType", emergencyTypeId);
        }
        emergency.setEmergencyType(et);
      }
    } else {
      throw new ResolvedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    Emergency saved = emergencyRepository.save(emergency);
    try {
      var gl = new AssignmentLog(null, saved, null, null, GeneralLogEventType.EMERGENCY_STATE_CHANGED,
          LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
          "Emergency state changed to " + emergency.getEmergencyIndex());
      logManagementService.logGeneral(gl);
    } catch (Exception ignored) {
    }
    return saved;
  }

  @Override
  public List<EmergencyType> findAllEmergencyTypes() {
    return emergencyTypeRepository.findAll();
  }


}
