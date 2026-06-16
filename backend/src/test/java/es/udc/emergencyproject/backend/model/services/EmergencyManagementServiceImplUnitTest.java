package es.udc.emergencyproject.backend.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrant;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyTypeRepository;
import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLog;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceRepository;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.EmergencyAlreadyLinkedToPointException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantAlreadyLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantNotLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.ResolvedEmergencyException;
import es.udc.emergencyproject.backend.model.services.emergency.impl.EmergencyManagementServiceImpl;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import es.udc.emergencyproject.backend.model.services.notifications.AssignmentNotificationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmergencyManagementServiceImplUnitTest {

  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
  private static final Point TEST_POINT = geometryFactory.createPoint(new Coordinate(539500, 4724000));

  @Mock
  private LogManagementService logManagementService;
  @Mock
  private EmergencyRepository emergencyRepository;
  @Mock
  private QuadrantRepository quadrantRepository;
  @Mock
  private EmergencyQuadrantRepository emergencyQuadrantRepository;
  @Mock
  private EmergencyTypeRepository emergencyTypeRepository;
  @Mock
  private AssignmentRepository assignmentRepository;
  @Mock
  private ResourceRepository resourceRepository;
  @Mock
  private AssignmentNotificationService assignmentNotificationService;

  @InjectMocks
  private EmergencyManagementServiceImpl emergencyService;

  private EmergencyType emergencyType;
  private Emergency emergency;
  private Emergency resolvedEmergency;
  private Quadrant quadrant;
  private EmergencyQuadrant emergencyQuadrant;

  @BeforeEach
  void setUp() {
    emergencyType = new EmergencyType();
    emergencyType.setId(1L);
    emergencyType.setName("Fire");

    emergency = new Emergency();
    emergency.setId(1L);
    emergency.setDescription("Test emergency");
    emergency.setEmergencyType(emergencyType);
    emergency.setEmergencyIndex(EmergencyIndex.UNO);

    resolvedEmergency = new Emergency();
    resolvedEmergency.setId(2L);
    resolvedEmergency.setDescription("Resolved");
    resolvedEmergency.setEmergencyIndex(EmergencyIndex.RESUELTO);

    quadrant = new Quadrant();
    quadrant.setId(1);
    quadrant.setNombre("Quadrant 1");

    emergencyQuadrant = new EmergencyQuadrant();
    emergencyQuadrant.setId(10L);
    emergencyQuadrant.setEmergency(emergency);
    emergencyQuadrant.setQuadrant(quadrant);

    lenient().when(emergencyTypeRepository.findAll()).thenReturn(List.of(emergencyType));
    lenient().when(emergencyTypeRepository.findById(anyLong())).thenReturn(Optional.of(emergencyType));
    lenient().when(emergencyRepository.findById(1L)).thenReturn(Optional.of(emergency));
    lenient().when(emergencyRepository.findById(2L)).thenReturn(Optional.of(resolvedEmergency));
    lenient().when(emergencyRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    lenient().when(quadrantRepository.findById(anyInt())).thenReturn(Optional.of(quadrant));
    lenient().when(quadrantRepository.findAll()).thenReturn(List.of(quadrant));
    lenient().when(emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(anyLong(), anyInt()))
        .thenReturn(Optional.empty());
    lenient().when(emergencyQuadrantRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    lenient().doNothing().when(logManagementService).logGeneral(any(AssignmentLog.class));
  }

  @Test
  void givenEmergencyType_whenCreateEmergency_thenSuccess() {
    lenient().when(emergencyRepository.save(any())).thenAnswer(i -> {
      Emergency e = i.getArgument(0);
      e.setId(10L);
      return e;
    });

    Emergency created = emergencyService.createEmergency("New emergency", 1L, EmergencyIndex.UNO);

    assertNotNull(created);
    assertEquals("New emergency", created.getDescription());
    verify(logManagementService).logGeneral(any(AssignmentLog.class));
  }

  @Test
  void givenNullEmergencyTypeId_whenCreateEmergency_thenThrows() {
    assertThrows(IllegalArgumentException.class,
        () -> emergencyService.createEmergency("No type", null, EmergencyIndex.UNO));
  }

  @Test
  void givenNonExistentEmergencyType_whenCreateEmergency_thenThrows() {
    when(emergencyTypeRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> emergencyService.createEmergency("Bad type", 999L, EmergencyIndex.UNO));
  }

  @Test
  void whenFindAllQuadrants_thenReturnAll() {
    List<Quadrant> result = emergencyService.findAllQuadrants();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  void givenValidScale_whenFindQuadrantsByEscala_thenReturnFiltered() {
    when(quadrantRepository.findByEscala("50.0")).thenReturn(List.of(quadrant));

    List<Quadrant> result = emergencyService.findQuadrantsByEscala("50.0");
    assertEquals(1, result.size());
  }

  @Test
  void givenValidGid_whenFindQuadrantById_thenFound() throws InstanceNotFoundException {
    Quadrant found = emergencyService.findQuadrantById(1);
    assertNotNull(found);
  }

  @Test
  void givenInvalidGid_whenFindQuadrantById_thenThrows() {
    when(quadrantRepository.findById(anyInt())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> emergencyService.findQuadrantById(999));
  }

  @Test
  void whenFindQuadrantsWithActiveEmergency_thenExcludesResolved() {
    EmergencyQuadrant eqResolved = new EmergencyQuadrant();
    eqResolved.setId(20L);
    eqResolved.setEmergency(resolvedEmergency);
    eqResolved.setQuadrant(quadrant);

    when(emergencyQuadrantRepository.findAll()).thenReturn(List.of(emergencyQuadrant, eqResolved));

    List<Quadrant> result = emergencyService.findQuadrantsWithActiveEmergency();
    assertEquals(1, result.size());
  }

  @Test
  void givenNoActiveEmergencies_whenFindQuadrantsWithActiveEmergency_thenEmpty() {
    EmergencyQuadrant eqResolved = new EmergencyQuadrant();
    eqResolved.setId(20L);
    eqResolved.setEmergency(resolvedEmergency);
    eqResolved.setQuadrant(quadrant);

    when(emergencyQuadrantRepository.findAll()).thenReturn(List.of(eqResolved));

    List<Quadrant> result = emergencyService.findQuadrantsWithActiveEmergency();
    assertTrue(result.isEmpty());
  }

  @Test
  void givenLocation_whenFindQuadrantByLocation_thenFound() {
    when(quadrantRepository.findByContainingPoint(TEST_POINT)).thenReturn(Optional.of(quadrant));

    Optional<Quadrant> result = emergencyService.findQuadrantByLocation(TEST_POINT);
    assertTrue(result.isPresent());
  }

  @Test
  void givenLocation_whenFindQuadrantByLocation_thenNotFound() {
    when(quadrantRepository.findByContainingPoint(TEST_POINT)).thenReturn(Optional.empty());

    Optional<Quadrant> result = emergencyService.findQuadrantByLocation(TEST_POINT);
    assertTrue(result.isEmpty());
  }

  @Test
  void givenValidData_whenLinkEmergencyToQuadrants_thenLinked() throws InstanceNotFoundException {
    lenient().when(emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(anyLong(), anyInt()))
        .thenReturn(Optional.empty());

    Emergency result = emergencyService.linkEmergencyToQuadrants(1L, List.of(1));

    assertNotNull(result);
    verify(emergencyQuadrantRepository).save(any(EmergencyQuadrant.class));
  }

  @Test
  void givenResolvedEmergency_whenLinkEmergencyToQuadrants_thenThrows() {
    assertThrows(ResolvedEmergencyException.class,
        () -> emergencyService.linkEmergencyToQuadrants(2L, List.of(1)));
  }

  @Test
  void givenEmergencyWithLocation_whenLinkEmergencyToQuadrants_thenThrows() {
    emergency.setLocation(TEST_POINT);

    assertThrows(EmergencyAlreadyLinkedToPointException.class,
        () -> emergencyService.linkEmergencyToQuadrants(1L, List.of(1)));
  }

  @Test
  void givenAlreadyLinkedQuadrant_whenLinkAgain_thenThrows() throws InstanceNotFoundException {
    when(emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(1L, 1))
        .thenReturn(Optional.of(emergencyQuadrant));

    assertThrows(QuadrantAlreadyLinkedToEmergencyException.class,
        () -> emergencyService.linkEmergencyToQuadrants(1L, List.of(1)));
  }

  @Test
  void givenValidEmergency_whenLinkEmergencyToPoint_thenLinked() throws InstanceNotFoundException {
    Emergency result = emergencyService.linkEmergencyToPoint(1L, TEST_POINT);

    assertNotNull(result.getLocation());
    verify(logManagementService).logGeneral(any(AssignmentLog.class));
  }

  @Test
  void givenResolvedEmergency_whenLinkEmergencyToPoint_thenThrows() {
    assertThrows(ResolvedEmergencyException.class,
        () -> emergencyService.linkEmergencyToPoint(2L, TEST_POINT));
  }


  @Test
  void givenEmergencyWithPoint_whenLinkEmergencyToPoint_thenThrows() {
    emergency.setLocation(TEST_POINT);

    assertThrows(EmergencyAlreadyLinkedToPointException.class,
        () -> emergencyService.linkEmergencyToPoint(1L, TEST_POINT));
  }

  @Test
  void whenFindAllEmergencies_thenReturnAll() {
    when(emergencyRepository.findAllByOrderByResolvedAtDescIdAsc()).thenReturn(List.of(emergency, resolvedEmergency));

    List<Emergency> result = emergencyService.findAllEmergencies();
    assertEquals(2, result.size());
  }

  @Test
  void whenFindActiveEmergencies_thenExcludesResolved() {
    when(emergencyRepository.findAllByOrderByResolvedAtDescIdAsc())
        .thenReturn(List.of(emergency, resolvedEmergency));

    List<Emergency> result = emergencyService.findActiveEmergencies();
    assertEquals(1, result.size());
    assertEquals(EmergencyIndex.UNO, result.get(0).getEmergencyIndex());
  }

  @Test
  void givenValidId_whenFindEmergencyById_thenFound() throws InstanceNotFoundException {
    Emergency found = emergencyService.findEmergencyById(1L);
    assertNotNull(found);
  }

  @Test
  void givenInvalidId_whenFindEmergencyById_thenThrows() {
    when(emergencyRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> emergencyService.findEmergencyById(999L));
  }

  @Test
  void givenActiveEmergency_whenResolve_thenResolved()
      throws InstanceNotFoundException, ResolvedEmergencyException, AlreadyDismantledException {
    when(assignmentRepository.findByEmergencyId(anyLong())).thenReturn(new ArrayList<>());
    lenient().when(emergencyRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Emergency result = emergencyService.resolveEmergency(1L);

    assertEquals(EmergencyIndex.RESUELTO, result.getEmergencyIndex());
    assertNotNull(result.getResolvedAt());
  }

  @Test
  void givenResolvedEmergency_whenResolveAgain_thenThrows() {
    assertThrows(ResolvedEmergencyException.class,
        () -> emergencyService.resolveEmergency(2L));
  }

  @Test
  void givenActiveEmergency_whenUpdateEmergency_thenUpdated() throws InstanceNotFoundException {
    emergencyService.updateEmergency(1L, "Updated description", 1L, EmergencyIndex.DOS);

    assertEquals("Updated description", emergency.getDescription());
    assertEquals(EmergencyIndex.DOS, emergency.getEmergencyIndex());
  }

  @Test
  void givenResolvedEmergency_whenUpdateEmergency_thenThrows() {
    assertThrows(ResolvedEmergencyException.class,
        () -> emergencyService.updateEmergency(2L, "New", 1L, EmergencyIndex.UNO));
  }

  @Test
  void givenLinkedQuadrant_whenRemoveQuadrant_thenRemoved()
      throws InstanceNotFoundException, ResolvedEmergencyException, AlreadyDismantledException {
    when(emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(1L, 1))
        .thenReturn(Optional.of(emergencyQuadrant));

    Emergency result = emergencyService.removeQuadrantByEmergencyId(1L, 1);
    assertNotNull(result);
  }

  @Test
  void givenNotLinkedQuadrant_whenRemoveQuadrant_thenThrows() {
    when(emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(1L, 1))
        .thenReturn(Optional.empty());

    assertThrows(QuadrantNotLinkedToEmergencyException.class,
        () -> emergencyService.removeQuadrantByEmergencyId(1L, 1));
  }

  @Test
  void givenResolvedEmergency_whenRemoveQuadrant_thenThrows() {
    assertThrows(ResolvedEmergencyException.class,
        () -> emergencyService.removeQuadrantByEmergencyId(2L, 1));
  }

  @Test
  void whenFindAllEmergencyTypes_thenReturnAll() {
    List<EmergencyType> types = emergencyService.findAllEmergencyTypes();
    assertEquals(1, types.size());
  }

  @Test
  void givenLocation_whenFindQuadrantByLocation_thenQuadrantReturned() {
    when(quadrantRepository.findByContainingPoint(TEST_POINT)).thenReturn(Optional.of(quadrant));

    Optional<Quadrant> result = emergencyService.findQuadrantByLocation(TEST_POINT);
    assertTrue(result.isPresent());
    assertEquals(1, result.get().getId());
  }

  @Test
  void givenNonExistentEmergency_whenLinkToQuadrants_thenThrows() {
    when(emergencyRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> emergencyService.linkEmergencyToQuadrants(999L, List.of(1)));
  }

  @Test
  void givenNonExistentQuadrant_whenLinkToQuadrants_thenThrows() {
    when(quadrantRepository.findById(anyInt())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> emergencyService.linkEmergencyToQuadrants(1L, List.of(999)));
  }

  @Test
  void givenNonExistentEmergency_whenLinkToPoint_thenThrows() {
    when(emergencyRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> emergencyService.linkEmergencyToPoint(999L, TEST_POINT));
  }

  @Test
  void givenNonExistentEmergency_whenResolve_thenThrows() {
    when(emergencyRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> emergencyService.resolveEmergency(999L));
  }
}
