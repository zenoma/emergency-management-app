package es.udc.emergencyproject.backend.model.services.emergency.impl;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrant;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
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
  private final es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRepository emergencyTypeRepository;

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
    List<EmergencyQuadrant> eqs = emergencyQuadrantRepository.findAll();
    List<Quadrant> result = new ArrayList<>();
    for (EmergencyQuadrant eq : eqs) {
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

      EmergencyQuadrant existing = emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(emergencyId, gid);
      if (existing != null) {
        throw new QuadrantAlreadyLinkedToEmergencyException(emergencyId, gid);
      } else {
        EmergencyQuadrant eq = new EmergencyQuadrant();
        eq.setEmergency(emergency);
        eq.setQuadrant(quadrant);
        eq.setLinkedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        emergencyQuadrantRepository.save(eq);
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
    return emergencyRepository.save(emergency);
  }


  // EMERGENCY SERVICES
  @Override
  public List<Emergency> findAllEmergencies() {
    return emergencyRepository.findAllByOrderByResolvedAtDescIdAsc();
  }

  @Override
  public Emergency findEmergencyById(Long id) throws InstanceNotFoundException {

    Emergency emergency = emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));
    // emergency.emergencyType is a @ManyToOne relation and will be available (fetch LAZY by default)
    return emergency;
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

    return emergencyRepository.save(emergency);
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

    List<EmergencyQuadrant> quadrants = emergencyQuadrantRepository.findByEmergencyId(id);

    //TODO: cuando se soluciona una emergencia hay que limpiar todos sus cuadrantes y liberar todos sus recursos

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

    EmergencyQuadrant eq = emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(id, quadrantId);
    if (eq == null) {
      throw new QuadrantNotLinkedToEmergencyException(id, quadrantId);
    }

    logManagementService.logEmergency(emergency, eq);
    //TODO: cuando se soluciona una emergencia hay que liberar los recursos de ese cuadrante

    emergencyQuadrantRepository.delete(eq);
    return emergencyRepository.save(emergency);
  }


  @Override
  public Emergency updateEmergency(Long id, String description, Long emergencyTypeId, EmergencyIndex emergencyIndex) {

    Emergency emergency = emergencyRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(EMERGENCY_NOT_FOUND, id));

    if (emergencyIndex == EmergencyIndex.RESUELTO) {
      throw new ResolvedEmergencyException(Emergency.class.getSimpleName(), emergency.getId().toString());
    }

    if (emergency.getEmergencyIndex() != EmergencyIndex.RESUELTO) {
      emergency.setDescription(description);
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

    return emergencyRepository.save(emergency);
  }

  @Override
  public List<EmergencyType> findAllEmergencyTypes() {
    return emergencyTypeRepository.findAll();
  }


}
