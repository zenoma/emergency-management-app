package es.udc.emergencyproject.backend.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentRepository;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrant;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.resource.Resource;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceRepository;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceStatus;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceType;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.exceptions.AssignmentAlreadyInStatusException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.InvalidAssignmentTransitionException;
import es.udc.emergencyproject.backend.model.exceptions.QuadrantNotLinkedToEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.ResolvedEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.ResourceBusyException;
import es.udc.emergencyproject.backend.model.services.assignment.impl.AssignmentServiceImpl;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import es.udc.emergencyproject.backend.model.services.notifications.AssignmentNotificationService;
import java.time.LocalDateTime;
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
class AssignmentServiceImplUnitTest {

  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

  @Mock
  private AssignmentRepository assignmentRepository;
  @Mock
  private EmergencyQuadrantRepository emergencyQuadrantRepository;
  @Mock
  private ResourceRepository resourceRepository;
  @Mock
  private EmergencyRepository emergencyRepository;
  @Mock
  private LogManagementService logManagementService;
  @Mock
  private AssignmentNotificationService assignmentNotificationService;

  @InjectMocks
  private AssignmentServiceImpl assignmentService;

  private Resource vehicleResource;
  private Resource teamResource;
  private Emergency pointEmergency;
  private Emergency quadrantEmergency;
  private EmergencyQuadrant emergencyQuadrant;
  private Point testLocation;

  @BeforeEach
  void setUp() {
    testLocation = geometryFactory.createPoint(new Coordinate(539500, 4724000));

    vehicleResource = new Vehicle("VEH-01", "Car", null);
    vehicleResource.setId(1L);
    vehicleResource.setStatus(ResourceStatus.AVAILABLE);

    teamResource = new Team("TEAM-X", null);
    teamResource.setId(2L);
    teamResource.setStatus(ResourceStatus.AVAILABLE);

    pointEmergency = new Emergency();
    pointEmergency.setId(10L);
    pointEmergency.setEmergencyIndex(EmergencyIndex.UNO);
    pointEmergency.setLocation(testLocation);

    quadrantEmergency = new Emergency();
    quadrantEmergency.setId(20L);
    quadrantEmergency.setEmergencyIndex(EmergencyIndex.UNO);

    EmergencyQuadrant eq = new EmergencyQuadrant();
    eq.setId(100L);
    eq.setEmergency(quadrantEmergency);
    quadrantEmergency.setEmergencyQuadrants(List.of(eq));

    emergencyQuadrant = eq;

    lenient().when(resourceRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(vehicleResource));
    lenient().when(emergencyRepository.findById(anyLong())).thenReturn(Optional.of(pointEmergency));
    lenient().when(emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(anyLong(), any()))
        .thenReturn(Optional.of(emergencyQuadrant));
    lenient().when(assignmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    lenient().when(resourceRepository.save(any())).thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void givenVehicleResource_whenCreateAssignmentForPointEmergency_thenAutoAccepted()
      throws InstanceNotFoundException {
    Assignment result = assignmentService.createAssignment(10L, null, 1L, "notes");

    assertNotNull(result);
    assertEquals(AssignmentStatus.ACCEPTED, result.getStatus());
    assertEquals(ResourceStatus.BUSY, vehicleResource.getStatus());
    assertNotNull(vehicleResource.getDeployAt());
    verify(assignmentNotificationService).notifyTeamAssignmentCreated(any());
  }

  @Test
  void givenTeamResource_whenCreateAssignmentForQuadrantEmergency_thenPending()
      throws InstanceNotFoundException {
    when(resourceRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(teamResource));
    when(emergencyRepository.findById(anyLong())).thenReturn(Optional.of(quadrantEmergency));

    Assignment result = assignmentService.createAssignment(20L, 1, 2L, null);

    assertNotNull(result);
    assertEquals(AssignmentStatus.PENDING, result.getStatus());
    verify(assignmentNotificationService).notifyTeamAssignmentCreated(any());
  }

  @Test
  void givenResolvedEmergency_whenCreateAssignment_thenThrows() {
    pointEmergency.setEmergencyIndex(EmergencyIndex.RESUELTO);

    assertThrows(ResolvedEmergencyException.class,
        () -> assignmentService.createAssignment(10L, null, 1L, "notes"));
  }

  @Test
  void givenBusyResource_whenCreateAssignment_thenThrows() {
    vehicleResource.setStatus(ResourceStatus.BUSY);

    assertThrows(ResourceBusyException.class,
        () -> assignmentService.createAssignment(10L, null, 1L, "notes"));
  }

  @Test
  void givenEmergencyWithNoPointAndNoQuadrants_whenCreateAssignment_thenThrows() {
    Emergency empty = new Emergency();
    empty.setId(30L);
    empty.setEmergencyIndex(EmergencyIndex.UNO);
    when(emergencyRepository.findById(30L)).thenReturn(Optional.of(empty));

    assertThrows(IllegalArgumentException.class,
        () -> assignmentService.createAssignment(30L, null, 1L, "notes"));
  }

  @Test
  void givenQuadrantEmergency_whenCreateAssignmentWithoutQuadrantId_thenThrows() {
    when(emergencyRepository.findById(anyLong())).thenReturn(Optional.of(quadrantEmergency));

    assertThrows(IllegalArgumentException.class,
        () -> assignmentService.createAssignment(20L, null, 1L, "notes"));
  }

  @Test
  void givenInvalidQuadrant_whenCreateAssignment_thenThrows() {
    when(emergencyRepository.findById(anyLong())).thenReturn(Optional.of(quadrantEmergency));
    when(emergencyQuadrantRepository.findByEmergencyIdAndQuadrantId(anyLong(), any()))
        .thenReturn(Optional.empty());

    assertThrows(QuadrantNotLinkedToEmergencyException.class,
        () -> assignmentService.createAssignment(20L, 999, 1L, "notes"));
  }

  @Test
  void givenExistingAssignment_whenFindById_thenReturned() throws InstanceNotFoundException {
    Assignment a = new Assignment();
    a.setId(1L);
    when(assignmentRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(a));

    Assignment found = assignmentService.findAssignmentById(1L);
    assertEquals(1L, found.getId());
  }

  @Test
  void givenNonExistentId_whenFindById_thenThrows() {
    when(assignmentRepository.findByIdWithRelations(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> assignmentService.findAssignmentById(999L));
  }

  @Test
  void givenFilters_whenFindByFiltersOnlyQuadrant_thenReturned() {
    when(assignmentRepository.findByEmergencyQuadrantQuadrantId(1)).thenReturn(List.of(new Assignment()));

    List<Assignment> result = assignmentService.findByFilters(1, null, null);
    assertEquals(1, result.size());
  }

  @Test
  void givenFilters_whenFindByFiltersOnlyEmergency_thenReturned() {
    when(assignmentRepository.findByEmergencyId(10L)).thenReturn(List.of(new Assignment()));

    List<Assignment> result = assignmentService.findByFilters(null, 10L, null);
    assertEquals(1, result.size());
  }

  @Test
  void givenFilters_whenFindByFiltersOnlyResource_thenReturned() {
    when(assignmentRepository.findByResourceId(1L)).thenReturn(List.of(new Assignment()));

    List<Assignment> result = assignmentService.findByFilters(null, null, 1L);
    assertEquals(1, result.size());
  }

  @Test
  void givenNoFilters_whenFindByFilters_thenAllReturned() {
    when(assignmentRepository.findByFilters(null, null, null)).thenReturn(List.of(new Assignment(), new Assignment()));

    List<Assignment> result = assignmentService.findByFilters(null, null, null);
    assertEquals(2, result.size());
  }

  @Test
  void givenAllFilters_whenFindByFilters_thenCombined() {
    when(assignmentRepository.findByFilters(1, 10L, 1L)).thenReturn(List.of(new Assignment()));

    List<Assignment> result = assignmentService.findByFilters(1, 10L, 1L);
    assertEquals(1, result.size());
  }

  @Test
  void givenEmergencyId_whenFindByEmergencyId_thenReturned() {
    when(assignmentRepository.findByEmergencyId(10L)).thenReturn(List.of(new Assignment()));

    List<Assignment> result = assignmentService.findByEmergencyId(10L);
    assertEquals(1, result.size());
  }

  @Test
  void givenResourceId_whenFindByResourceId_thenReturned() {
    when(assignmentRepository.findByResourceId(1L)).thenReturn(List.of(new Assignment()));

    List<Assignment> result = assignmentService.findByResourceId(1L);
    assertEquals(1, result.size());
  }

  @Test
  void givenQuadrantId_whenFindByEmergencyQuadrantQuadrantId_thenReturned() {
    when(assignmentRepository.findByEmergencyQuadrantQuadrantId(1)).thenReturn(List.of(new Assignment()));

    List<Assignment> result = assignmentService.findByEmergencyQuadrantQuadrantId(1);
    assertEquals(1, result.size());
  }

  @Test
  void givenQuadrantGid_whenFindByQuadrantGid_thenReturned() {
    when(assignmentRepository.findByQuadrantGid(1)).thenReturn(List.of(new Assignment()));

    List<Assignment> result = assignmentService.findByQuadrantGid(1);
    assertEquals(1, result.size());
  }

  @Test
  void givenPendingAssignment_whenUpdateStatusToAccepted_thenSuccess()
      throws InstanceNotFoundException {
    Assignment a = new Assignment();
    a.setId(1L);
    a.setStatus(AssignmentStatus.PENDING);
    a.setResource(vehicleResource);
    a.setEmergency(pointEmergency);
    when(assignmentRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(a));

    Assignment result = assignmentService.updateStatus(1L, AssignmentStatus.ACCEPTED);

    assertEquals(AssignmentStatus.ACCEPTED, result.getStatus());
    assertNotNull(result.getAcceptedAt());
    verify(assignmentNotificationService).notifyTeamAssignmentStatusChanged(any(), any());
  }

  @Test
  void givenAcceptedAssignment_whenUpdateStatusToCompleted_thenSuccess()
      throws InstanceNotFoundException {
    Assignment a = new Assignment();
    a.setId(1L);
    a.setStatus(AssignmentStatus.ACCEPTED);
    a.setResource(vehicleResource);
    a.setEmergency(pointEmergency);
    when(assignmentRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(a));

    Assignment result = assignmentService.updateStatus(1L, AssignmentStatus.COMPLETED);

    assertEquals(AssignmentStatus.COMPLETED, result.getStatus());
    assertNotNull(result.getCompletedAt());
    assertEquals(ResourceStatus.AVAILABLE, vehicleResource.getStatus());
  }

  @Test
  void givenAcceptedAssignment_whenUpdateStatusToReleased_thenSuccess()
      throws InstanceNotFoundException {
    Assignment a = new Assignment();
    a.setId(1L);
    a.setStatus(AssignmentStatus.ACCEPTED);
    a.setResource(vehicleResource);
    a.setEmergency(pointEmergency);
    when(assignmentRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(a));

    Assignment result = assignmentService.updateStatus(1L, AssignmentStatus.RELEASED);

    assertEquals(AssignmentStatus.RELEASED, result.getStatus());
    assertNotNull(result.getCompletedAt());
    assertEquals(ResourceStatus.AVAILABLE, vehicleResource.getStatus());
  }

  @Test
  void givenPendingAssignment_whenUpdateStatusToCompleted_thenThrows()
      throws InstanceNotFoundException {
    Assignment a = new Assignment();
    a.setId(1L);
    a.setStatus(AssignmentStatus.PENDING);
    a.setResource(vehicleResource);
    when(assignmentRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(a));

    assertThrows(InvalidAssignmentTransitionException.class,
        () -> assignmentService.updateStatus(1L, AssignmentStatus.COMPLETED));
  }

  @Test
  void givenAssignment_whenUpdateStatusToSameStatus_thenThrows()
      throws InstanceNotFoundException {
    Assignment a = new Assignment();
    a.setId(1L);
    a.setStatus(AssignmentStatus.PENDING);
    a.setResource(vehicleResource);
    when(assignmentRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(a));

    assertThrows(AssignmentAlreadyInStatusException.class,
        () -> assignmentService.updateStatus(1L, AssignmentStatus.PENDING));
  }

  @Test
  void givenPendingAssignment_whenDelete_thenRemoved() throws InstanceNotFoundException {
    Assignment a = new Assignment();
    a.setId(1L);
    a.setStatus(AssignmentStatus.PENDING);
    a.setResource(vehicleResource);
    a.setEmergency(pointEmergency);
    when(assignmentRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(a));

    assignmentService.deleteAssignment(1L);

    assertEquals(Boolean.TRUE, a.getRemoved());
  }

  @Test
  void givenAcceptedAssignment_whenDelete_thenThrows() throws InstanceNotFoundException {
    Assignment a = new Assignment();
    a.setId(1L);
    a.setStatus(AssignmentStatus.ACCEPTED);
    a.setResource(vehicleResource);
    when(assignmentRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(a));

    assertThrows(InvalidAssignmentTransitionException.class,
        () -> assignmentService.deleteAssignment(1L));
  }

  @Test
  void givenNonExistentAssignment_whenDelete_thenThrows() {
    when(assignmentRepository.findByIdWithRelations(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> assignmentService.deleteAssignment(999L));
  }

  @Test
  void givenVehicleAssignment_whenResourceBecomesBusyBeforeAutoAccept_thenRollback()
      throws InstanceNotFoundException {
    vehicleResource.setStatus(ResourceStatus.BUSY);
    when(resourceRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(vehicleResource));

    assertThrows(ResourceBusyException.class,
        () -> assignmentService.createAssignment(10L, null, 1L, "notes"));
  }

  @Test
  void givenEmptyQuadrantList_whenCreateAssignment_thenPointBasedCheck()
      throws InstanceNotFoundException {
    Emergency emptyQuadList = new Emergency();
    emptyQuadList.setId(40L);
    emptyQuadList.setEmergencyIndex(EmergencyIndex.UNO);
    emptyQuadList.setEmergencyQuadrants(new ArrayList<>());
    emptyQuadList.setLocation(testLocation);
    when(emergencyRepository.findById(40L)).thenReturn(Optional.of(emptyQuadList));

    Assignment result = assignmentService.createAssignment(40L, null, 1L, "point-only");

    assertNotNull(result);
    assertEquals(AssignmentStatus.ACCEPTED, result.getStatus());
  }
}
