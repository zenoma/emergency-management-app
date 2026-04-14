package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class EmergencyManagementServiceImplTest extends IntegrationTest {

  private final Integer VALID_QUADRANT_ID = 1;
  private final Integer INVALID_QUADRANT_ID = -1;
  private final Long INVALID_emergency_ID = -1L;
  private final EmergencyManagementService emergencyManagementService;
  private final PersonalManagementFacade personalManagementFacade;

//  // QUADRANT SERVICES
//  @Test
//  void givenNoData_whenFindAll_thenReturnNotEmptyList() {
//    Assertions.assertNotNull(emergencyManagementService.findAllQuadrants());
//  }
//
//  @Test
//  void givenValidData_whenFindByEscala_thenReturnNotEmptyList() {
//    List<Quadrant> quadrantsScale50 = emergencyManagementService.findQuadrantsByEscala("50.0");
//    Assertions.assertNotNull(quadrantsScale50);
//    List<Quadrant> quadrantsScale25 = emergencyManagementService.findQuadrantsByEscala("25.0");
//    Assertions.assertNotNull(quadrantsScale25);
//    Assertions.assertNotEquals(quadrantsScale50.size(), quadrantsScale25.size());
//  }
//
//  @Test
//  void givenInvalidData_whenFindByEscala_thenReturnNotEmptyList() {
//    Assertions.assertNotNull(emergencyManagementService.findQuadrantsByEscala("Not a valid value"));
//  }
//
//  @Test
//  void givenValidData_whenFindQuadrantById_thenQuadrantFound() throws InstanceNotFoundException {
//    Assertions.assertNotNull(emergencyManagementService.findQuadrantById(1));
//  }
//
//  @Test
//  void givenInvalidData_whenFindQuadrantById_thenInstanceNotFoundException() {
//    Assertions.assertThrows(InstanceNotFoundException.class,
//        () -> emergencyManagementService.findQuadrantById(INVALID_QUADRANT_ID),
//        "InstanceNotFoundException error was expected");
//  }
//
//  @Test
//  void givenValidData_whenLinkEmergency_thenEmergencyLinkedSuccessfully() throws InstanceNotFoundException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//
//    quadrant = emergencyManagementService.linkEmergency(quadrant.getId(), emergency.getId());
//
//    Assertions.assertEquals(quadrant.getEmergency(), emergency);
//  }
//
//
//  @Test
//  void givenValidData_whenFindQuadrantsWithActiveEmergency_thenActivesQuadrantsFound()
//      throws InstanceNotFoundException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//    Emergency emergency2 = emergencyManagementService.createEmergency("Description 2", emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//    Quadrant quadrant2 = emergencyManagementService.findQuadrantById(2);
//    Quadrant quadrant3 = emergencyManagementService.findQuadrantById(3);
//
//    quadrant = emergencyManagementService.linkEmergency(quadrant.getId(), emergency.getId());
//    quadrant2 = emergencyManagementService.linkEmergency(quadrant2.getId(), emergency.getId());
//    quadrant3 = emergencyManagementService.linkEmergency(quadrant3.getId(), emergency2.getId());
//
//    List<Quadrant> quadrants = new ArrayList<>();
//    quadrants.add(quadrant);
//    quadrants.add(quadrant2);
//    quadrants.add(quadrant3);
//
//    Assertions.assertTrue(emergencyManagementService.findQuadrantsWithActiveEmergency().containsAll(quadrants));
//  }
//
//
//  // EMERGENCY SERVICES
//  @Test
//  void givenValidData_whenCreateEmergency_thenEmergencyCreated() throws InstanceNotFoundException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    Assertions.assertEquals(emergency, emergencyManagementService.findEmergencyById(emergency.getId()));
//  }
//
//
//  @Test
//  void givenValidData_whenFindAll_thenFoundAllEmergencies() {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//    Emergency emergency2 = emergencyManagementService.createEmergency("description2", "Type1", EmergencyIndex.UNO);
//    Emergency emergency3 = emergencyManagementService.createEmergency("description3", "Type1", EmergencyIndex.UNO);
//
//    List<Emergency> emergencies = new ArrayList<>();
//    emergencies.add(emergency);
//    emergencies.add(emergency);
//    emergencies.add(emergency3);
//
//    Assertions.assertTrue(emergencyManagementService.findAllEmergencies().containsAll(emergencies));
//  }
//
//  @Test
//  void givenInvalidData_whenFindEmergencyById_thenEmergencyCreated() {
//    Assertions.assertThrows(InstanceNotFoundException.class,
//        () -> emergencyManagementService.findEmergencyById(INVALID_emergency_ID),
//        "InstanceNotFoundException error was expected");
//  }
//
//  @Test
//  void givenValidData_whenUpdateEmergency_thenEmergencyUpdated()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//    emergency.setEmergencyIndex(EmergencyIndex.TRES);
//    emergency.setDescription("New Description.");
//
//    Assertions.assertEquals(emergency,
//        emergencyManagementService.updateEmergency(emergency.getId(), emergency.getDescription(), emergency.getType(),
//            emergency.getEmergencyIndex()));
//  }
//
//  @Test
//  void givenValidData_whenExtinguishEmergency_thenEmergencyExtinguish()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    emergencyManagementService.extinguishEmergency(emergency.getId());
//
//    Assertions.assertEquals(EmergencyIndex.EXTINGUIDO,
//        emergencyManagementService.findEmergencyById(emergency.getId()).getEmergencyIndex());
//  }
//
//
//  @Test
//  void givenValidData_whenExtinguishEmergencyWithTeamsAndVehicles_thenEmergencyExtinguish()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException, AlreadyExistException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Team team = TeamOM.withDefaultValues();
//    team = personalManagementFacade.createTeam(team.getCode(),
//        organization.getId());
//
//    Vehicle vehicle = VehicleOM.withDefaultValues();
//    vehicle = personalManagementFacade.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//
//    vehicle = emergencyManagementService.deployVehicle(vehicle.getId(), quadrant.getId());
//    team = emergencyManagementService.deployTeam(team.getId(), quadrant.getId());
//
//    quadrant = emergencyManagementService.linkEmergency(quadrant.getId(), emergency.getId());
//
//    emergency = emergencyManagementService.extinguishEmergency(emergency.getId());
//
//    Assertions.assertEquals(EmergencyIndex.EXTINGUIDO,
//        emergencyManagementService.findEmergencyById(emergency.getId()).getEmergencyIndex());
//    Assertions.assertNull(personalManagementFacade.findVehicleById(vehicle.getId()).getQuadrant());
//    Assertions.assertNull(personalManagementFacade.findTeamById(team.getId()).getQuadrant());
//  }
//
//  @Test
//  void givenValidData_whenExtinguishEmergency_thenExtinguishedEmergencyException()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    emergencyManagementService.extinguishEmergency(emergency.getId());
//
//    Emergency finalEmergency = emergency;
//    Assertions.assertThrows(ExtinguishedEmergencyException.class,
//        () -> emergencyManagementService.extinguishEmergency(finalEmergency.getId()),
//        "ExtinguishedEmergencyException error was expected");
//
//  }
//
//  @Test
//  void givenValidData_whenUpdateExtinguishedEmergency_thenExtinguishedEmergencyException()
//      throws ExtinguishedEmergencyException, InstanceNotFoundException, AlreadyDismantledException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//    emergency.setDescription("New Description.");
//
//    emergencyManagementService.extinguishEmergency(emergency.getId());
//
//    Emergency finalEmergency = emergency;
//
//    Assertions.assertThrows(ExtinguishedEmergencyException.class,
//        () -> emergencyManagementService.updateEmergency(finalEmergency.getId(), finalEmergency.getDescription(),
//            finalEmergency.getType(),
//            finalEmergency.getEmergencyIndex()),
//        "ExtinguishedEmergencyException error was expected");
//  }
//
//
//  @Test
//  void givenValidData_whenExtinguishQuadrant_thenQuadrantExtinguishedSuccessfully()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//    Quadrant quadrant2 = emergencyManagementService.findQuadrantById(2);
//
//    quadrant = emergencyManagementService.linkEmergency(quadrant.getId(), emergency.getId());
//    quadrant2 = emergencyManagementService.linkEmergency(quadrant2.getId(), emergency.getId());
//
//    emergency = emergencyManagementService.extinguishQuadrantByEmergencyId(emergency.getId(), quadrant.getId());
//
//    Assertions.assertNull(quadrant.getEmergency());
//    Assertions.assertEquals(quadrant2.getEmergency(), emergency);
//  }
//
//
//  @Test
//  void givenValidData_whenExtinguishQuadrantWithTeamsAndVehicles_thenQuadrantExtinguishedSuccessfully()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException, AlreadyExistException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Team team = TeamOM.withDefaultValues();
//    team = personalManagementFacade.createTeam(team.getCode(),
//        organization.getId());
//
//    Vehicle vehicle = VehicleOM.withDefaultValues();
//    vehicle = personalManagementFacade.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//
//    vehicle = emergencyManagementService.deployVehicle(vehicle.getId(), quadrant.getId());
//    team = emergencyManagementService.deployTeam(team.getId(), quadrant.getId());
//
//    quadrant = emergencyManagementService.linkEmergency(quadrant.getId(), emergency.getId());
//
//    emergency = emergencyManagementService.extinguishQuadrantByEmergencyId(emergency.getId(), quadrant.getId());
//
//    Assertions.assertEquals(emergency.getEmergencyIndex(),
//        emergencyManagementService.findEmergencyById(emergency.getId()).getEmergencyIndex());
//  }
//
//  @Test
//  void givenValidData_whenExtinguishQuadrantOfExtinguishedEmergency_thenExtinguishedEmergencyException()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException, AlreadyExistException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Team team = TeamOM.withDefaultValues();
//    team = personalManagementFacade.createTeam(team.getCode(),
//        organization.getId());
//
//    Vehicle vehicle = VehicleOM.withDefaultValues();
//    vehicle = personalManagementFacade.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//
//    quadrant = emergencyManagementService.linkEmergency(quadrant.getId(), emergency.getId());
//
//    vehicle = emergencyManagementService.deployVehicle(vehicle.getId(), quadrant.getId());
//    team = emergencyManagementService.deployTeam(team.getId(), quadrant.getId());
//
//    emergency = emergencyManagementService.extinguishEmergency(emergency.getId());
//
//    Emergency finalEmergency = emergency;
//    Quadrant finalQuadrant = quadrant;
//    Assertions.assertThrows(ExtinguishedEmergencyException.class,
//        () -> emergencyManagementService.extinguishQuadrantByEmergencyId(finalEmergency.getId(), finalQuadrant.getId()),
//        "ExtinguishedEmergencyException error was expected");
//  }
//
//
//  @Test
//  void givenValidData_whenExtinguishQuadrantOfNotBelonginEmergency_thenExtinguishedEmergencyException()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException, AlreadyExistException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//    Emergency emergency2 = emergencyManagementService.createEmergency("Description 2", emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//
//    quadrant = emergencyManagementService.linkEmergency(quadrant.getId(), emergency.getId());
//
//    Emergency finalEmergency = emergency2;
//    Quadrant finalQuadrant = quadrant;
//    Assertions.assertThrows(RuntimeException.class,
//        () -> emergencyManagementService.extinguishQuadrantByEmergencyId(finalEmergency.getId(), finalQuadrant.getId()),
//        "ExtinguishedEmergencyException error was expected");
//  }
//
//
//  // EXTINCTION SERVICES
//  @Test
//  void givenValidTeam_whenDeployTeam_thenTeamDeployed()
//      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Team team = TeamOM.withDefaultValues();
//    team = personalManagementFacade.createTeam(team.getCode(),
//        organization.getId());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//
//    emergencyManagementService.deployTeam(team.getId(), quadrant.getId());
//
//    Assertions.assertNotNull(personalManagementFacade.findTeamById(team.getId()).getQuadrant());
//    Assertions.assertEquals(personalManagementFacade.findTeamById(team.getId()).getQuadrant(), quadrant);
//  }
//
//  @Test
//  void givenValidTeam_whenRetractTeam_thenTeamRetracted()
//      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Team team = TeamOM.withDefaultValues();
//    team = personalManagementFacade.createTeam(team.getCode(),
//        organization.getId());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//
//    emergencyManagementService.deployTeam(team.getId(), quadrant.getId());
//
//    Assertions.assertNotNull(personalManagementFacade.findTeamById(team.getId()).getQuadrant());
//    Assertions.assertEquals(personalManagementFacade.findTeamById(team.getId()).getQuadrant(), quadrant);
//
//    emergencyManagementService.retractTeam(team.getId());
//    Assertions.assertNull(personalManagementFacade.findTeamById(team.getId()).getQuadrant());
//
//  }
//
//  @Test
//  void givenValidVehicle_whenDeployVehicle_thenVehicleDeployed()
//      throws InstanceNotFoundException, AlreadyDismantledException {
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Vehicle vehicle = VehicleOM.withDefaultValues();
//    vehicle = personalManagementFacade.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//
//    emergencyManagementService.deployVehicle(vehicle.getId(), quadrant.getId());
//
//    Assertions.assertNotNull(personalManagementFacade.findVehicleById(vehicle.getId()).getQuadrant());
//    Assertions.assertEquals(personalManagementFacade.findVehicleById(vehicle.getId()).getQuadrant(), quadrant);
//  }
//
//  @Test
//  void givenValidVehicle_whenRetractVehicle_thenVehicleRetracted()
//      throws InstanceNotFoundException, AlreadyDismantledException {
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Vehicle vehicle = VehicleOM.withDefaultValues();
//    vehicle = personalManagementFacade.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//
//    emergencyManagementService.deployVehicle(vehicle.getId(), quadrant.getId());
//
//    Assertions.assertNotNull(personalManagementFacade.findVehicleById(vehicle.getId()).getQuadrant());
//    Assertions.assertEquals(personalManagementFacade.findVehicleById(vehicle.getId()).getQuadrant(), quadrant);
//
//    emergencyManagementService.retractVehicle(vehicle.getId());
//    Assertions.assertNull(personalManagementFacade.findVehicleById(vehicle.getId()).getQuadrant());
//  }

}
