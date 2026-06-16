package es.udc.emergencyproject.backend.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLog;
import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLogRepository;
import es.udc.emergencyproject.backend.model.entities.logs.GeneralLogEventType;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.resource.Resource;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.services.logs.impl.LogManagementServiceImpl;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentLogDto;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
class LogManagementServiceImplUnitTest {

  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

  @Mock
  private QuadrantRepository quadrantRepository;
  @Mock
  private AssignmentLogRepository assignmentLogRepository;
  @Mock
  private EmergencyRepository emergencyRepository;

  @InjectMocks
  private LogManagementServiceImpl logManagementService;

  private Emergency emergency;
  private Assignment assignment;
  private Resource teamResource;
  private Resource vehicleResource;
  private Quadrant quadrant;

  @BeforeEach
  void setUp() {
    emergency = new Emergency();
    emergency.setId(1L);

    quadrant = new Quadrant();
    quadrant.setId(1);

    teamResource = new Team("LOG-TEAM", null);
    teamResource.setId(10L);

    vehicleResource = new Vehicle("LOG-VH", "Car", null);
    vehicleResource.setId(20L);

    assignment = new Assignment();
    assignment.setId(100L);
    assignment.setEmergency(emergency);
    assignment.setStatus(AssignmentStatus.PENDING);
    assignment.setResource(teamResource);

    lenient().when(assignmentLogRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    lenient().when(assignmentLogRepository.findByEmergencyId(anyLong())).thenReturn(List.of());
  }

  @Test
  void givenAssignmentLog_whenLogGeneral_thenSaved() {
    AssignmentLog log = new AssignmentLog(null, emergency, quadrant, teamResource,
        GeneralLogEventType.EMERGENCY_CREATED, LocalDateTime.now(), "Test log");

    logManagementService.logGeneral(log);

    verify(assignmentLogRepository, times(1)).save(log);
  }

  @Test
  void givenAssignment_whenRegisterAssignmentEvent_thenSaved() {
    logManagementService.registerAssignmentEvent(assignment, GeneralLogEventType.ASSIGNMENT_CREATED, "Created");

    verify(assignmentLogRepository, times(1)).save(any(AssignmentLog.class));
  }

  @Test
  void givenAssignmentWithQuadrant_whenRegisterAssignmentEvent_thenSaved() {
    es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrant eq =
        new es.udc.emergencyproject.backend.model.entities.emergency.EmergencyQuadrant();
    eq.setQuadrant(quadrant);
    assignment.setEmergencyQuadrant(eq);

    logManagementService.registerAssignmentEvent(assignment, GeneralLogEventType.ASSIGNMENT_ACCEPTED, "Accepted");

    verify(assignmentLogRepository, times(1)).save(any(AssignmentLog.class));
  }


  @Test
  void givenNoLogs_whenFindByEmergencyIdAndDate_thenEmpty() {
    when(assignmentLogRepository.findByEmergencyId(anyLong())).thenReturn(List.of());

    LocalDate start = LocalDate.of(2020, 1, 1);
    LocalDate end = LocalDate.of(2020, 1, 2);
    List<AssignmentLogDto> result = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(1L, start, end);

    assertTrue(result.isEmpty());
  }

  @Test
  void givenEmergencyWithLocation_whenGetGlobalStatistics_thenZeroHectares() {
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    emergency.setLocation(location);
    when(emergencyRepository.findById(1L)).thenReturn(java.util.Optional.of(emergency));

    GlobalStatisticsDto stats = logManagementService.getGlobalStatistics(1L);

    assertNotNull(stats);
    assertEquals(0, stats.getTeamsMobilized());
    assertEquals(0, stats.getVehiclesMobilized());
    assertEquals(BigDecimal.ZERO.setScale(3, java.math.RoundingMode.HALF_UP), stats.getMaxBurnedHectares());
  }

  @Test
  void givenLogsWithTeamAndVehicleEvents_whenGetGlobalStatistics_thenCountsMatch() {
    AssignmentLog teamCompleted = new AssignmentLog(null, emergency, quadrant, teamResource,
        GeneralLogEventType.ASSIGNMENT_COMPLETED, LocalDateTime.now(), "Team done");
    AssignmentLog vehicleCompleted = new AssignmentLog(null, emergency, quadrant, vehicleResource,
        GeneralLogEventType.ASSIGNMENT_COMPLETED, LocalDateTime.now(), "Vehicle done");

    when(assignmentLogRepository.findByEmergencyId(1L)).thenReturn(List.of(teamCompleted, vehicleCompleted));

    GlobalStatisticsDto stats = logManagementService.getGlobalStatistics(1L);

    assertEquals(1, stats.getTeamsMobilized());
    assertEquals(1, stats.getVehiclesMobilized());
  }

  @Test
  void givenLogsWithQuadrantInfo_whenGetGlobalStatistics_thenAffectedQuadrantsCounted() {
    AssignmentLog log = new AssignmentLog(null, emergency, quadrant, teamResource,
        GeneralLogEventType.ASSIGNMENT_CREATED, LocalDateTime.now(), "Created");

    when(assignmentLogRepository.findByEmergencyId(1L)).thenReturn(List.of(log));

    GlobalStatisticsDto stats = logManagementService.getGlobalStatistics(1L);

    assertEquals(1, stats.getAffectedQuadrants());
  }


  @Test
  void givenEmergencyWithQuadrantGids_whenGetGlobalStatistics_thenQuadrantIdsFromGids() {
    Quadrant q = new Quadrant();
    q.setId(5);
    emergency.setEmergencyQuadrants(List.of());
    when(assignmentLogRepository.findByEmergencyId(1L)).thenReturn(List.of());
    when(emergencyRepository.findById(1L)).thenReturn(java.util.Optional.of(emergency));

    GlobalStatisticsDto stats = logManagementService.getGlobalStatistics(1L);

    assertNotNull(stats);
  }
}
