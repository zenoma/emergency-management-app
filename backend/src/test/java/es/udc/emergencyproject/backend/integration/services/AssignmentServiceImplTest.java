package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.assignment.AssignmentStatus;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.organization.OrganizationType;
import es.udc.emergencyproject.backend.model.entities.resource.ResourceStatus;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyExistException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.InvalidAssignmentTransitionException;
import es.udc.emergencyproject.backend.model.exceptions.ResourceBusyException;
import es.udc.emergencyproject.backend.model.services.assignment.AssignmentService;
import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import es.udc.emergencyproject.backend.model.services.resources.ResourceManagementFacade;
import es.udc.emergencyproject.backend.utils.OrganizationOM;
import es.udc.emergencyproject.backend.utils.OrganizationTypeOM;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@RequiredArgsConstructor
class AssignmentServiceImplTest extends IntegrationTest {

  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

  private final AssignmentService assignmentService;
  private final EmergencyManagementService emergencyManagementService;
  private final PersonalManagementFacade personalManagementFacade;
  private final ResourceManagementFacade resourceManagementFacade;

  private Organization createDefaultOrganization() {
    OrganizationType type = personalManagementFacade.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization org = OrganizationOM.withDefaultValues();
    org.setOrganizationType(type);
    return personalManagementFacade.createOrganization(org);
  }

  private Emergency createEmergencyWithPoint() {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency e = emergencyManagementService.createEmergency("Point emergency", types.get(0).getId(),
        EmergencyIndex.UNO);
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    return emergencyManagementService.linkEmergencyToPoint(e.getId(), location);
  }

  private Emergency createEmergencyWithQuadrantsAndPoint() {
    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency e = emergencyManagementService.createEmergency("Quadrant emergency", types.get(0).getId(),
        EmergencyIndex.UNO);
    Emergency linked = emergencyManagementService.linkEmergencyToQuadrants(e.getId(), List.of(1, 2));
    Point location = geometryFactory.createPoint(new Coordinate(539500, 4724000));
    linked.setLocation(location);
    return linked;
  }

  @Test
  void givenValidEmergency_whenCreateAssignmentWithVehicle_thenAutoAccepted() throws InstanceNotFoundException {
    Organization org = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("ASSIGN01", "Car", org.getId());
    Emergency emergency = createEmergencyWithPoint();

    Assignment assignment = assignmentService.createAssignment(emergency.getId(), null, vehicle.getId(), "Test notes");
    Assertions.assertNotNull(assignment.getId());
    Assertions.assertEquals(AssignmentStatus.ACCEPTED, assignment.getStatus());
  }

  @Test
  void givenQuadrantEmergency_whenCreateAssignmentWithTeam_thenPending()
      throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createDefaultOrganization();
    Team team = resourceManagementFacade.createTeam("TEAM-ASSIGN", org.getId());
    Emergency emergency = createEmergencyWithQuadrantsAndPoint();

    Assignment assignment = assignmentService.createAssignment(emergency.getId(), 1, team.getId(), null);
    Assertions.assertNotNull(assignment.getId());
    Assertions.assertEquals(AssignmentStatus.PENDING, assignment.getStatus());
  }

  @Test
  void givenAssignment_whenFindById_thenFound() throws InstanceNotFoundException {
    Organization org = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("ASSIGN02", "Car", org.getId());
    Emergency emergency = createEmergencyWithPoint();

    Assignment created = assignmentService.createAssignment(emergency.getId(), null, vehicle.getId(), null);
    Assignment found = assignmentService.findAssignmentById(created.getId());
    Assertions.assertEquals(created.getId(), found.getId());
    Assertions.assertNotNull(found.getResource());
  }

  @Test
  void givenInvalidQuadrant_whenCreateAssignment_thenThrows() throws InstanceNotFoundException {
    Organization org = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("ASSIGN03", "Car", org.getId());

    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency e = emergencyManagementService.createEmergency("Quadrant emergency", types.get(0).getId(),
        EmergencyIndex.UNO);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> assignmentService.createAssignment(e.getId(), 999, vehicle.getId(), null));
  }

  @Test
  void givenPendingTeamAssignment_whenUpdateToAccepted_thenDeployed()
      throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createDefaultOrganization();
    Team team = resourceManagementFacade.createTeam("TEAM-ACC", org.getId());
    Emergency emergency = createEmergencyWithQuadrantsAndPoint();

    Assignment assignment = assignmentService.createAssignment(emergency.getId(), 1, team.getId(), null);
    Assertions.assertEquals(AssignmentStatus.PENDING, assignment.getStatus());

    Assignment accepted = assignmentService.updateStatus(assignment.getId(), AssignmentStatus.ACCEPTED);
    Assertions.assertEquals(AssignmentStatus.ACCEPTED, accepted.getStatus());

    Team deployed = resourceManagementFacade.findTeamById(team.getId());
    Assertions.assertEquals(ResourceStatus.BUSY, deployed.getStatus());
    Assertions.assertNotNull(deployed.getDeployAt());
  }

  @Test
  void givenAcceptedAssignment_whenUpdateToCompleted_thenResourceAvailable()
      throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createDefaultOrganization();
    Team team = resourceManagementFacade.createTeam("TEAM-COMP", org.getId());
    Emergency emergency = createEmergencyWithQuadrantsAndPoint();

    Assignment assignment = assignmentService.createAssignment(emergency.getId(), 1, team.getId(), null);
    assignmentService.updateStatus(assignment.getId(), AssignmentStatus.ACCEPTED);
    assignmentService.updateStatus(assignment.getId(), AssignmentStatus.COMPLETED);

    Team completed = resourceManagementFacade.findTeamById(team.getId());
    Assertions.assertEquals(ResourceStatus.AVAILABLE, completed.getStatus());
    Assertions.assertNull(completed.getDeployAt());
  }

  @Test
  void givenAcceptedAssignment_whenUpdateToReleased_thenResourceAvailable()
      throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createDefaultOrganization();
    Team team = resourceManagementFacade.createTeam("TEAM-REL", org.getId());
    Emergency emergency = createEmergencyWithQuadrantsAndPoint();

    Assignment assignment = assignmentService.createAssignment(emergency.getId(), 1, team.getId(), null);
    assignmentService.updateStatus(assignment.getId(), AssignmentStatus.ACCEPTED);
    assignmentService.updateStatus(assignment.getId(), AssignmentStatus.RELEASED);

    Team released = resourceManagementFacade.findTeamById(team.getId());
    Assertions.assertEquals(ResourceStatus.AVAILABLE, released.getStatus());
    Assertions.assertNull(released.getDeployAt());
  }

  @Test
  void givenInvalidTransition_whenUpdateStatus_thenThrows() throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createDefaultOrganization();
    Team team = resourceManagementFacade.createTeam("TEAM-INV", org.getId());
    Emergency emergency = createEmergencyWithQuadrantsAndPoint();

    Assignment assignment = assignmentService.createAssignment(emergency.getId(), 1, team.getId(), null);
    Assertions.assertThrows(InvalidAssignmentTransitionException.class,
        () -> assignmentService.updateStatus(assignment.getId(), AssignmentStatus.COMPLETED));
  }


  @Test
  void givenAcceptedAssignment_whenDelete_thenThrows() throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createDefaultOrganization();
    Team team = resourceManagementFacade.createTeam("TEAM-DEL2", org.getId());
    Emergency emergency = createEmergencyWithQuadrantsAndPoint();

    Assignment assignment = assignmentService.createAssignment(emergency.getId(), 1, team.getId(), null);
    assignmentService.updateStatus(assignment.getId(), AssignmentStatus.ACCEPTED);

    Assertions.assertThrows(InvalidAssignmentTransitionException.class,
        () -> assignmentService.deleteAssignment(assignment.getId()));
  }

  @Test
  void givenAssignments_whenFindByEmergencyId_thenFound() throws InstanceNotFoundException {
    Organization org = createDefaultOrganization();
    Vehicle v1 = resourceManagementFacade.createVehicle("FBYE01", "Car", org.getId());
    Vehicle v2 = resourceManagementFacade.createVehicle("FBYE02", "Van", org.getId());
    Emergency emergency = createEmergencyWithPoint();

    assignmentService.createAssignment(emergency.getId(), null, v1.getId(), null);
    assignmentService.createAssignment(emergency.getId(), null, v2.getId(), null);

    List<Assignment> found = assignmentService.findByEmergencyId(emergency.getId());
    Assertions.assertEquals(2, found.size());
  }

  @Test
  void givenAssignments_whenFindByResourceId_thenFound() throws InstanceNotFoundException {
    Organization org = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("FBYR01", "Car", org.getId());
    Emergency emergency = createEmergencyWithPoint();

    assignmentService.createAssignment(emergency.getId(), null, vehicle.getId(), null);

    List<Assignment> found = assignmentService.findByResourceId(vehicle.getId());
    Assertions.assertEquals(1, found.size());
  }

  @Test
  void givenQuadrantAssignment_whenFindByQuadrantGid_thenFound()
      throws InstanceNotFoundException, AlreadyExistException {
    Organization org = createDefaultOrganization();
    Team team = resourceManagementFacade.createTeam("TEAM-QGID", org.getId());
    Emergency emergency = createEmergencyWithQuadrantsAndPoint();

    assignmentService.createAssignment(emergency.getId(), 1, team.getId(), null);

    List<Assignment> found = assignmentService.findByQuadrantGid(1);
    Assertions.assertTrue(found.isEmpty());
  }

  @Test
  void givenBusyResource_whenCreateAssignment_thenThrows() throws InstanceNotFoundException {
    Organization org = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("BUSY01", "Car", org.getId());
    Emergency emergency1 = createEmergencyWithPoint();

    List<EmergencyType> types = emergencyManagementService.findAllEmergencyTypes();
    Emergency emergency2 = emergencyManagementService.createEmergency("Second", types.get(0).getId(),
        EmergencyIndex.UNO);
    Point loc = geometryFactory.createPoint(new Coordinate(539600, 4724100));
    emergency2 = emergencyManagementService.linkEmergencyToPoint(emergency2.getId(), loc);

    assignmentService.createAssignment(emergency1.getId(), null, vehicle.getId(), null);

    Emergency finalEmergency = emergency2;
    Assertions.assertThrows(ResourceBusyException.class,
        () -> assignmentService.createAssignment(finalEmergency.getId(), null, vehicle.getId(), null));
  }
}
