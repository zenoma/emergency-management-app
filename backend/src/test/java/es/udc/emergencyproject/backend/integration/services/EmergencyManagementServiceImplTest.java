package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantAlreadyLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantNotLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.ResolvedEmergencyException;
import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@RequiredArgsConstructor
class EmergencyManagementServiceImplTest extends IntegrationTest {

  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

  private final EmergencyManagementService emergencyManagementService;

  @Test
  void givenNoData_whenFindAllQuadrants_thenReturnNotEmptyList() {
    List<Quadrant> quadrants = emergencyManagementService.findAllQuadrants();
    Assertions.assertFalse(quadrants.isEmpty());
  }

  @Test
  void givenValidScale_whenFindQuadrantsByEscala_thenReturnFilteredList() {
    List<Quadrant> scale50 = emergencyManagementService.findQuadrantsByEscala("50.0");
    List<Quadrant> scale25 = emergencyManagementService.findQuadrantsByEscala("25.0");
    Assertions.assertNotEquals(scale50.size(), scale25.size());
  }

  @Test
  void givenValidId_whenFindQuadrantById_thenQuadrantFound() throws InstanceNotFoundException {
    Assertions.assertNotNull(emergencyManagementService.findQuadrantById(1));
  }

  @Test
  void givenInvalidId_whenFindQuadrantById_thenThrows() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> emergencyManagementService.findQuadrantById(-1));
  }

  @Test
  void whenFindAllEmergencyTypes_thenReturnNonEmpty() {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Assertions.assertFalse(types.isEmpty());
  }

  @Test
  void givenValidData_whenCreateAndFindEmergency_thenSuccess() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();

    Emergency created = emergencyManagementService.createEmergency("Test emergency", typeId, EmergencyIndex.UNO);
    Assertions.assertNotNull(created.getId());

    Emergency found = emergencyManagementService.findEmergencyById(created.getId());
    Assertions.assertEquals(created.getId(), found.getId());
    Assertions.assertEquals("Test emergency", found.getDescription());
  }

  @Test
  void givenCreatedEmergencies_whenFindAll_thenContainsAll() {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();

    Emergency e1 = emergencyManagementService.createEmergency("Emergency A", typeId, EmergencyIndex.UNO);
    Emergency e2 = emergencyManagementService.createEmergency("Emergency B", typeId, EmergencyIndex.DOS);

    List<Emergency> all = emergencyManagementService.findAllEmergencies();
    Assertions.assertTrue(all.stream().anyMatch(e -> e.getId().equals(e1.getId())));
    Assertions.assertTrue(all.stream().anyMatch(e -> e.getId().equals(e2.getId())));
  }

  @Test
  void givenCreatedEmergencies_whenFindActive_thenOnlyNonResolved() {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();

    Emergency e1 = emergencyManagementService.createEmergency("Active", typeId, EmergencyIndex.UNO);
    Emergency e2 = emergencyManagementService.createEmergency("To resolve", typeId, EmergencyIndex.UNO);

    List<Emergency> activeBefore = emergencyManagementService.findActiveEmergencies();
    Assertions.assertTrue(activeBefore.stream().anyMatch(e -> e.getId().equals(e1.getId())));

    emergencyManagementService.resolveEmergency(e2.getId());
    List<Emergency> activeAfter = emergencyManagementService.findActiveEmergencies();
    Assertions.assertTrue(activeAfter.stream().anyMatch(e -> e.getId().equals(e1.getId())));
    Assertions.assertTrue(activeAfter.stream().noneMatch(e -> e.getId().equals(e2.getId())));
  }

  @Test
  void givenValidData_whenUpdateEmergency_thenUpdated() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();

    Emergency emergency = emergencyManagementService.createEmergency("Original", typeId, EmergencyIndex.UNO);
    emergencyManagementService.updateEmergency(emergency.getId(), "Updated description", typeId, EmergencyIndex.DOS);

    Emergency updated = emergencyManagementService.findEmergencyById(emergency.getId());
    Assertions.assertEquals("Updated description", updated.getDescription());
    Assertions.assertEquals(EmergencyIndex.DOS, updated.getEmergencyIndex());
  }


  @Test
  void givenAlreadyLinkedQuadrant_whenLinkAgain_thenThrows() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();
    Emergency emergency = emergencyManagementService.createEmergency("Duplicate link", typeId, EmergencyIndex.UNO);

    emergencyManagementService.linkEmergencyToQuadrants(emergency.getId(), List.of(1));
    Assertions.assertThrows(QuadrantAlreadyLinkedToEmergencyException.class,
        () -> emergencyManagementService.linkEmergencyToQuadrants(emergency.getId(), List.of(1)));
  }

  @Test
  void givenValidData_whenLinkEmergencyToPoint_thenLinked() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();
    Emergency emergency = emergencyManagementService.createEmergency("Point linked", typeId, EmergencyIndex.UNO);

    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    Emergency linked = emergencyManagementService.linkEmergencyToPoint(emergency.getId(), location);
    Assertions.assertNotNull(linked.getLocation());
  }

  @Test
  void givenResolvedEmergency_whenLinkQuadrant_thenThrows() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();
    Emergency emergency = emergencyManagementService.createEmergency("Resolve me", typeId, EmergencyIndex.UNO);
    emergencyManagementService.resolveEmergency(emergency.getId());

    Assertions.assertThrows(ResolvedEmergencyException.class,
        () -> emergencyManagementService.linkEmergencyToQuadrants(emergency.getId(), List.of(1)));
  }

  @Test
  void givenResolvedEmergency_whenUpdate_thenThrows() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();
    Emergency emergency = emergencyManagementService.createEmergency("Resolved update", typeId, EmergencyIndex.UNO);
    emergencyManagementService.resolveEmergency(emergency.getId());

    Assertions.assertThrows(ResolvedEmergencyException.class,
        () -> emergencyManagementService.updateEmergency(emergency.getId(), "New", typeId, EmergencyIndex.DOS));
  }

  @Test
  void givenQuadrantLinked_whenRemoveQuadrant_thenRemoved() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();
    Emergency emergency = emergencyManagementService.createEmergency("Remove quadrant", typeId, EmergencyIndex.UNO);
    emergencyManagementService.linkEmergencyToQuadrants(emergency.getId(), List.of(1));

    Emergency afterRemove = emergencyManagementService.removeQuadrantByEmergencyId(emergency.getId(), 1);
    Assertions.assertTrue(afterRemove.getQuadrantGids() == null || afterRemove.getQuadrantGids().isEmpty());
  }

  @Test
  void givenNoQuadrantLinked_whenRemoveQuadrant_thenThrows() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();
    Emergency emergency = emergencyManagementService.createEmergency("No link", typeId, EmergencyIndex.UNO);

    Assertions.assertThrows(QuadrantNotLinkedToEmergencyException.class,
        () -> emergencyManagementService.removeQuadrantByEmergencyId(emergency.getId(), 1));
  }

  @Test
  void givenInvalidEmergencyId_whenFindById_thenThrows() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> emergencyManagementService.findEmergencyById(-1L));
  }

  @Test
  void givenResolvedEmergency_whenRemoveQuadrant_thenThrows() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();
    Emergency emergency = emergencyManagementService.createEmergency("Resolved quad", typeId, EmergencyIndex.UNO);
    emergencyManagementService.linkEmergencyToQuadrants(emergency.getId(), List.of(1));
    emergencyManagementService.resolveEmergency(emergency.getId());

    Assertions.assertThrows(ResolvedEmergencyException.class,
        () -> emergencyManagementService.removeQuadrantByEmergencyId(emergency.getId(), 1));
  }

  @Test
  void givenQuadrantLinkedEmergency_whenLinkToPoint_thenThrows() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Long typeId = types.get(0).getId();
    Emergency emergency = emergencyManagementService.createEmergency("Quad first", typeId, EmergencyIndex.UNO);
    emergencyManagementService.linkEmergencyToQuadrants(emergency.getId(), List.of(1));

    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    Assertions.assertThrows(Exception.class,
        () -> emergencyManagementService.linkEmergencyToPoint(emergency.getId(), location));
  }
}
