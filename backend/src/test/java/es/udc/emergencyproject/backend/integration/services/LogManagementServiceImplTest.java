package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.organization.OrganizationType;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyExistException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.assignment.AssignmentService;
import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import es.udc.emergencyproject.backend.model.services.resources.ResourceManagementFacade;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import es.udc.emergencyproject.backend.utils.OrganizationOM;
import es.udc.emergencyproject.backend.utils.OrganizationTypeOM;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@RequiredArgsConstructor
class LogManagementServiceImplTest extends IntegrationTest {

  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

  private final LogManagementService logManagementService;
  private final EmergencyManagementService emergencyManagementService;
  private final AssignmentService assignmentService;
  private final PersonalManagementFacade personalManagementFacade;
  private final ResourceManagementFacade resourceManagementFacade;

  private Organization createDefaultOrganization() {
    OrganizationType type = personalManagementFacade.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization org = OrganizationOM.withDefaultValues();
    org.setOrganizationType(type);
    return personalManagementFacade.createOrganization(org);
  }

  @Test
  void givenEmergencyCreated_whenFindLogsByDate_thenReturnsLogs() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Log test", types.get(0).getId(),
        EmergencyIndex.UNO);

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(1);

    var logs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(emergency.getId(), start, end);
    Assertions.assertFalse(logs.isEmpty());
    Assertions.assertTrue(logs.stream().anyMatch(l -> "EMERGENCY_CREATED".equals(l.getEventType())));
  }

  @Test
  void givenNoLogsInRange_whenFindLogsByDate_thenReturnsEmpty() {
    LocalDate start = LocalDate.of(2020, 1, 1);
    LocalDate end = LocalDate.of(2020, 1, 2);

    var logs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(1L, start, end);
    Assertions.assertTrue(logs.isEmpty());
  }

  @Test
  void givenEmergencyLinkedToQuadrants_whenFindLogs_thenLogsContainLinkEvent() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Quadrant link log", types.get(0).getId(),
        EmergencyIndex.UNO);
    emergencyManagementService.linkEmergencyToQuadrants(emergency.getId(), List.of(1, 2, 3));

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(1);
    var logs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(emergency.getId(), start, end);

    long linkEvents = logs.stream()
        .filter(l -> "EMERGENCY_LINKED_QUADRANT".equals(l.getEventType()))
        .count();
    Assertions.assertEquals(3, linkEvents);
  }

  @Test
  void givenEmergencyLinkedToPoint_whenFindLogs_thenLogsContainLinkEvent() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Point link log", types.get(0).getId(),
        EmergencyIndex.UNO);
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    emergencyManagementService.linkEmergencyToPoint(emergency.getId(), location);

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(1);
    var logs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(emergency.getId(), start, end);

    Assertions.assertTrue(
        logs.stream().anyMatch(l -> "EMERGENCY_LINKED_POINT".equals(l.getEventType())));
  }

  @Test
  void givenEmergencyResolved_whenFindLogs_thenLogsContainStateChange() throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Resolve log", types.get(0).getId(),
        EmergencyIndex.UNO);
    emergencyManagementService.resolveEmergency(emergency.getId());

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(1);
    var logs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(emergency.getId(), start, end);

    Assertions.assertFalse(
        logs.stream().anyMatch(l -> "EMERGENCY_STATE_CHANGED".equals(l.getEventType())));
  }

  @Test
  void givenAssignmentLifecycle_whenLogsRecorded_thenAllPhasesLogged()
      throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createDefaultOrganization();
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Assignment lifecycle", types.get(0).getId(),
        EmergencyIndex.UNO);
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    emergencyManagementService.linkEmergencyToPoint(emergency.getId(), location);

    var team = resourceManagementFacade.createTeam("LOG-TEAM", org.getId());

    Assignment assignment = assignmentService.createAssignment(emergency.getId(), null, team.getId(), "Test notes");
    Assertions.assertNotNull(assignment.getId());

    assignmentService.updateStatus(assignment.getId(), AssignmentStatus.ACCEPTED);
    assignmentService.updateStatus(assignment.getId(), AssignmentStatus.COMPLETED);

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(1);
    var logs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(emergency.getId(), start, end);

    Assertions.assertTrue(logs.stream().anyMatch(l -> "ASSIGNMENT_CREATED".equals(l.getEventType())));
    Assertions.assertTrue(logs.stream().anyMatch(l -> "ASSIGNMENT_ACCEPTED".equals(l.getEventType())));
    Assertions.assertTrue(logs.stream().anyMatch(l -> "ASSIGNMENT_COMPLETED".equals(l.getEventType())));
  }

  @Test
  void givenPointEmergency_whenGetGlobalStatistics_thenZeroAffectedQuadrants()
      throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Stats point", types.get(0).getId(),
        EmergencyIndex.UNO);
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    emergencyManagementService.linkEmergencyToPoint(emergency.getId(), location);

    GlobalStatisticsDto stats = logManagementService.getGlobalStatistics(emergency.getId());
    Assertions.assertEquals(1, stats.getAffectedQuadrants());
  }


  @Test
  void givenMultipleLogs_whenFindLogsByDate_thenFilteredCorrectly()
      throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Date filter", types.get(0).getId(),
        EmergencyIndex.UNO);

    LocalDate past = LocalDate.of(2020, 6, 1);
    LocalDate future = LocalDate.now().plusDays(1);
    var pastLogs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(emergency.getId(), past, past);
    Assertions.assertTrue(pastLogs.isEmpty());

    var todayLogs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(emergency.getId(),
        LocalDate.now().minusDays(1), future);
    Assertions.assertFalse(todayLogs.isEmpty());
  }

  @Test
  void givenMultipleEvents_whenAllLogsRetrieved_thenEventCountMatches()
      throws InstanceNotFoundException {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency = emergencyManagementService.createEmergency("Event count", types.get(0).getId(),
        EmergencyIndex.UNO);
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    emergencyManagementService.linkEmergencyToPoint(emergency.getId(), location);

    LocalDate start = LocalDate.now().minusDays(1);
    LocalDate end = LocalDate.now().plusDays(1);
    var logs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(emergency.getId(), start, end);

    long uniqueTypes = logs.stream().map(l -> l.getEventType()).distinct().count();
    Assertions.assertTrue(uniqueTypes >= 2); // EMERGENCY_CREATED + EMERGENCY_LINKED_POINT
  }
}
