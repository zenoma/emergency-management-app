package es.udc.fireproject.backend.integration.services;

import es.udc.fireproject.backend.IntegrationTest;
import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.fire.FireIndex;
import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.AlreadyExistException;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.utils.FireOM;
import es.udc.fireproject.backend.utils.OrganizationOM;
import es.udc.fireproject.backend.utils.OrganizationTypeOM;
import es.udc.fireproject.backend.utils.TeamOM;
import es.udc.fireproject.backend.utils.VehicleOM;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FireManagementServiceImplTest extends IntegrationTest {

  private final Integer VALID_QUADRANT_ID = 1;
  private final Integer INVALID_QUADRANT_ID = -1;
  private final Long INVALID_FIRE_ID = -1L;
  @Autowired
  FireManagementService fireManagementService;
  @Autowired
  PersonalManagementService personalManagementService;

  // QUADRANT SERVICES
  @Test
  void givenNoData_whenFindAll_thenReturnNotEmptyList() {
    Assertions.assertNotNull(fireManagementService.findAllQuadrants());
  }

  @Test
  void givenValidData_whenFindByEscala_thenReturnNotEmptyList() {
    List<Quadrant> quadrantsScale50 = fireManagementService.findQuadrantsByEscala("50.0");
    Assertions.assertNotNull(quadrantsScale50);
    List<Quadrant> quadrantsScale25 = fireManagementService.findQuadrantsByEscala("25.0");
    Assertions.assertNotNull(quadrantsScale25);
    Assertions.assertNotEquals(quadrantsScale50.size(), quadrantsScale25.size());
  }

  @Test
  void givenInvalidData_whenFindByEscala_thenReturnNotEmptyList() {
    Assertions.assertNotNull(fireManagementService.findQuadrantsByEscala("Not a valid value"));
  }

  @Test
  void givenValidData_whenFindQuadrantById_thenQuadrantFound() throws InstanceNotFoundException {
    Assertions.assertNotNull(fireManagementService.findQuadrantById(1));
  }

  @Test
  void givenInvalidData_whenFindQuadrantById_thenInstanceNotFoundException() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> fireManagementService.findQuadrantById(INVALID_QUADRANT_ID),
        "InstanceNotFoundException error was expected");
  }

  @Test
  void givenValidData_whenLinkFire_thenFireLinkedSuccessfully() throws InstanceNotFoundException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);

    quadrant = fireManagementService.linkFire(quadrant.getId(), fire.getId());

    Assertions.assertEquals(quadrant.getFire(), fire);
  }


  @Test
  void givenValidData_whenFindQuadrantsWithActiveFire_thenActivesQuadrantsFound() throws InstanceNotFoundException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());
    Fire fire2 = fireManagementService.createFire("Description 2", fire.getType(), fire.getFireIndex());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);
    Quadrant quadrant2 = fireManagementService.findQuadrantById(2);
    Quadrant quadrant3 = fireManagementService.findQuadrantById(3);

    quadrant = fireManagementService.linkFire(quadrant.getId(), fire.getId());
    quadrant2 = fireManagementService.linkFire(quadrant2.getId(), fire.getId());
    quadrant3 = fireManagementService.linkFire(quadrant3.getId(), fire2.getId());

    List<Quadrant> quadrants = new ArrayList<>();
    quadrants.add(quadrant);
    quadrants.add(quadrant2);
    quadrants.add(quadrant3);

    Assertions.assertTrue(fireManagementService.findQuadrantsWithActiveFire().containsAll(quadrants));
  }


  // FIRE SERVICES
  @Test
  void givenValidData_whenCreateFire_thenFireCreated() throws InstanceNotFoundException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

    Assertions.assertEquals(fire, fireManagementService.findFireById(fire.getId()));
  }


  @Test
  void givenValidData_whenFindAll_thenFoundAllFires() {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());
    Fire fire2 = fireManagementService.createFire("description2", "Type1", FireIndex.UNO);
    Fire fire3 = fireManagementService.createFire("description3", "Type1", FireIndex.UNO);

    List<Fire> fires = new ArrayList<>();
    fires.add(fire);
    fires.add(fire);
    fires.add(fire3);

    Assertions.assertTrue(fireManagementService.findAllFires().containsAll(fires));
  }

  @Test
  void givenInvalidData_whenFindFireById_thenFireCreated() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> fireManagementService.findFireById(INVALID_FIRE_ID),
        "InstanceNotFoundException error was expected");
  }

  @Test
  void givenValidData_whenUpdateFire_thenFireUpdated() throws InstanceNotFoundException, ExtinguishedFireException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());
    fire.setFireIndex(FireIndex.TRES);
    fire.setDescription("New Description.");

    Assertions.assertEquals(fire,
        fireManagementService.updateFire(fire.getId(), fire.getDescription(), fire.getType(), fire.getFireIndex()));
  }

  @Test
  void givenValidData_whenExtinguishFire_thenFireExtinguish()
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

    fireManagementService.extinguishFire(fire.getId());

    Assertions.assertEquals(FireIndex.EXTINGUIDO, fireManagementService.findFireById(fire.getId()).getFireIndex());
  }


  @Test
  void givenValidData_whenExtinguishFireWithTeamsAndVehicles_thenFireExtinguish()
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException, AlreadyExistException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

    OrganizationType organizationType = personalManagementService.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementService.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = personalManagementService.createTeam(team.getCode(),
        organization.getId());

    Vehicle vehicle = VehicleOM.withDefaultValues();
    vehicle = personalManagementService.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
        organization.getId());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);

    vehicle = fireManagementService.deployVehicle(vehicle.getId(), quadrant.getId());
    team = fireManagementService.deployTeam(team.getId(), quadrant.getId());

    quadrant = fireManagementService.linkFire(quadrant.getId(), fire.getId());

    fire = fireManagementService.extinguishFire(fire.getId());

    Assertions.assertEquals(FireIndex.EXTINGUIDO, fireManagementService.findFireById(fire.getId()).getFireIndex());
    Assertions.assertNull(personalManagementService.findVehicleById(vehicle.getId()).getQuadrant());
    Assertions.assertNull(personalManagementService.findTeamById(team.getId()).getQuadrant());
  }

  @Test
  void givenValidData_whenExtinguishFire_thenExtinguishedFireException()
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

    fireManagementService.extinguishFire(fire.getId());

    Fire finalFire = fire;
    Assertions.assertThrows(ExtinguishedFireException.class,
        () -> fireManagementService.extinguishFire(finalFire.getId()),
        "ExtinguishedFireException error was expected");

  }

  @Test
  void givenValidData_whenUpdateExtinguishedFire_thenExtinguishedFireException()
      throws ExtinguishedFireException, InstanceNotFoundException, AlreadyDismantledException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());
    fire.setDescription("New Description.");

    fireManagementService.extinguishFire(fire.getId());

    Fire finalFire = fire;

    Assertions.assertThrows(ExtinguishedFireException.class,
        () -> fireManagementService.updateFire(finalFire.getId(), finalFire.getDescription(), finalFire.getType(),
            finalFire.getFireIndex()),
        "ExtinguishedFireException error was expected");
  }


  @Test
  void givenValidData_whenExtinguishQuadrant_thenQuadrantExtinguishedSuccessfully()
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);
    Quadrant quadrant2 = fireManagementService.findQuadrantById(2);

    quadrant = fireManagementService.linkFire(quadrant.getId(), fire.getId());
    quadrant2 = fireManagementService.linkFire(quadrant2.getId(), fire.getId());

    fire = fireManagementService.extinguishQuadrantByFireId(fire.getId(), quadrant.getId());

    Assertions.assertNull(quadrant.getFire());
    Assertions.assertEquals(quadrant2.getFire(), fire);
  }


  @Test
  void givenValidData_whenExtinguishQuadrantWithTeamsAndVehicles_thenQuadrantExtinguishedSuccessfully()
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException, AlreadyExistException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

    OrganizationType organizationType = personalManagementService.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementService.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = personalManagementService.createTeam(team.getCode(),
        organization.getId());

    Vehicle vehicle = VehicleOM.withDefaultValues();
    vehicle = personalManagementService.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
        organization.getId());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);

    vehicle = fireManagementService.deployVehicle(vehicle.getId(), quadrant.getId());
    team = fireManagementService.deployTeam(team.getId(), quadrant.getId());

    quadrant = fireManagementService.linkFire(quadrant.getId(), fire.getId());

    fire = fireManagementService.extinguishQuadrantByFireId(fire.getId(), quadrant.getId());

    Assertions.assertEquals(fire.getFireIndex(), fireManagementService.findFireById(fire.getId()).getFireIndex());
  }

  @Test
  void givenValidData_whenExtinguishQuadrantOfExtinguishedFire_thenExtinguishedFireException()
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException, AlreadyExistException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());

    OrganizationType organizationType = personalManagementService.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementService.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = personalManagementService.createTeam(team.getCode(),
        organization.getId());

    Vehicle vehicle = VehicleOM.withDefaultValues();
    vehicle = personalManagementService.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
        organization.getId());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);

    quadrant = fireManagementService.linkFire(quadrant.getId(), fire.getId());

    vehicle = fireManagementService.deployVehicle(vehicle.getId(), quadrant.getId());
    team = fireManagementService.deployTeam(team.getId(), quadrant.getId());

    fire = fireManagementService.extinguishFire(fire.getId());

    Fire finalFire = fire;
    Quadrant finalQuadrant = quadrant;
    Assertions.assertThrows(ExtinguishedFireException.class,
        () -> fireManagementService.extinguishQuadrantByFireId(finalFire.getId(), finalQuadrant.getId()),
        "ExtinguishedFireException error was expected");
  }


  @Test
  void givenValidData_whenExtinguishQuadrantOfNotBelonginFire_thenExtinguishedFireException()
      throws InstanceNotFoundException, ExtinguishedFireException, AlreadyDismantledException, AlreadyExistException {
    Fire fire = FireOM.withDefaultValues();
    fire = fireManagementService.createFire(fire.getDescription(), fire.getType(), fire.getFireIndex());
    Fire fire2 = fireManagementService.createFire("Description 2", fire.getType(), fire.getFireIndex());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);

    quadrant = fireManagementService.linkFire(quadrant.getId(), fire.getId());

    Fire finalFire = fire2;
    Quadrant finalQuadrant = quadrant;
    Assertions.assertThrows(RuntimeException.class,
        () -> fireManagementService.extinguishQuadrantByFireId(finalFire.getId(), finalQuadrant.getId()),
        "ExtinguishedFireException error was expected");
  }


  // EXTINCTION SERVICES
  @Test
  void givenValidTeam_whenDeployTeam_thenTeamDeployed()
      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
    OrganizationType organizationType = personalManagementService.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementService.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = personalManagementService.createTeam(team.getCode(),
        organization.getId());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);

    fireManagementService.deployTeam(team.getId(), quadrant.getId());

    Assertions.assertNotNull(personalManagementService.findTeamById(team.getId()).getQuadrant());
    Assertions.assertEquals(personalManagementService.findTeamById(team.getId()).getQuadrant(), quadrant);
  }

  @Test
  void givenValidTeam_whenRetractTeam_thenTeamRetracted()
      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
    OrganizationType organizationType = personalManagementService.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementService.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = personalManagementService.createTeam(team.getCode(),
        organization.getId());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);

    fireManagementService.deployTeam(team.getId(), quadrant.getId());

    Assertions.assertNotNull(personalManagementService.findTeamById(team.getId()).getQuadrant());
    Assertions.assertEquals(personalManagementService.findTeamById(team.getId()).getQuadrant(), quadrant);

    fireManagementService.retractTeam(team.getId());
    Assertions.assertNull(personalManagementService.findTeamById(team.getId()).getQuadrant());

  }

  @Test
  void givenValidVehicle_whenDeployVehicle_thenVehicleDeployed()
      throws InstanceNotFoundException, AlreadyDismantledException {
    OrganizationType organizationType = personalManagementService.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementService.createOrganization(organization);

    Vehicle vehicle = VehicleOM.withDefaultValues();
    vehicle = personalManagementService.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
        organization.getId());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);

    fireManagementService.deployVehicle(vehicle.getId(), quadrant.getId());

    Assertions.assertNotNull(personalManagementService.findVehicleById(vehicle.getId()).getQuadrant());
    Assertions.assertEquals(personalManagementService.findVehicleById(vehicle.getId()).getQuadrant(), quadrant);
  }

  @Test
  void givenValidVehicle_whenRetractVehicle_thenVehicleRetracted()
      throws InstanceNotFoundException, AlreadyDismantledException {
    OrganizationType organizationType = personalManagementService.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementService.createOrganization(organization);

    Vehicle vehicle = VehicleOM.withDefaultValues();
    vehicle = personalManagementService.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
        organization.getId());

    Quadrant quadrant = fireManagementService.findQuadrantById(VALID_QUADRANT_ID);

    fireManagementService.deployVehicle(vehicle.getId(), quadrant.getId());

    Assertions.assertNotNull(personalManagementService.findVehicleById(vehicle.getId()).getQuadrant());
    Assertions.assertEquals(personalManagementService.findVehicleById(vehicle.getId()).getQuadrant(), quadrant);

    fireManagementService.retractVehicle(vehicle.getId());
    Assertions.assertNull(personalManagementService.findVehicleById(vehicle.getId()).getQuadrant());
  }

}
