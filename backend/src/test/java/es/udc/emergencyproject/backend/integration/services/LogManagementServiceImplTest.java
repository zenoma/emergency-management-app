package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.services.emergency.impl.EmergencyManagementServiceImpl;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class LogManagementServiceImplTest extends IntegrationTest {

  private static final Integer VALID_QUADRANT_ID = 1;

  private final LogManagementService logManagementService;
  private final EmergencyManagementServiceImpl emergencyManagementService;
  private final PersonalManagementFacade personalManagementService;

//  @Test
//  void givenNoData_whenFindAllEmergencyQuadrantLogs_thenReturnNotEmptyList() {
//    Assertions.assertNotNull(logManagementService.findAllEmergencyQuadrantLogs());
//  }
//
//  @Test
//  void givenNoData_whenFindAllTeamQuadrantLogs_thenReturnNotEmptyList() {
//    Assertions.assertNotNull(logManagementService.findAllTeamQuadrantLogs());
//  }
//
//  @Test
//  void givenNoData_whenFindAllVehicleQuadrantLogs_thenReturnNotEmptyList() {
//    Assertions.assertNotNull(logManagementService.findAllVehicleQuadrantLogs());
//  }
//
//  @Test
//  void givenValidData_whenFindEmergencysLogByEmergencyIdAndLinkedAt_thenReturnValidLogs()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//    Quadrant quadrant2 = emergencyManagementService.findQuadrantById(2);
//    Quadrant quadrant3 = emergencyManagementService.findQuadrantById(3);
//
//    LocalDateTime starDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
//    quadrant = emergencyManagementService.linkEmergency(quadrant.getId(), emergency.getId());
//    quadrant2 = emergencyManagementService.linkEmergency(quadrant2.getId(), emergency.getId());
//    quadrant3 = emergencyManagementService.linkEmergency(quadrant3.getId(), emergency.getId());
//
//    LocalDateTime endDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
//
//    emergency = emergencyManagementService.extinguishQuadrantByEmergencyId(emergency.getId(), quadrant.getId());
//    emergency = emergencyManagementService.extinguishQuadrantByEmergencyId(emergency.getId(), quadrant2.getId());
//
//    logManagementService.findEmergenciesByEmergencyIdAndLinkedAt(emergency, starDate, endDate);
//
//    Assertions.assertNotNull(
//        logManagementService.findEmergenciesByEmergencyIdAndLinkedAt(emergency, starDate, endDate));
//  }
//
//  @Test
//  void givenValidData_whenFindTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt_thenReturnValidLogs()
//      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException, AlreadyExistException {
//    Emergency emergency = EmergencyOM.withDefaultValues();
//    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
//        emergency.getEmergencyIndex());
//
//    OrganizationType organizationType = personalManagementService.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementService.createOrganization(organization);
//
//    Quadrant quadrant = emergencyManagementService.findQuadrantById(VALID_QUADRANT_ID);
//    Quadrant quadrant2 = emergencyManagementService.findQuadrantById(2);
//    Quadrant quadrant3 = emergencyManagementService.findQuadrantById(3);
//
//    Team team = TeamOM.withDefaultValues();
//    team = personalManagementService.createTeam(team.getCode(),
//        organization.getId());
//
//    Vehicle vehicle = VehicleOM.withDefaultValues();
//    vehicle = personalManagementService.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//
//    LocalDateTime starDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
//    quadrant = emergencyManagementService.linkEmergency(quadrant.getId(), emergency.getId());
//    quadrant2 = emergencyManagementService.linkEmergency(quadrant2.getId(), emergency.getId());
//    quadrant3 = emergencyManagementService.linkEmergency(quadrant3.getId(), emergency.getId());
//
//    vehicle = emergencyManagementService.deployVehicle(vehicle.getId(), quadrant.getId());
//    team = emergencyManagementService.deployTeam(team.getId(), quadrant.getId());
//
//    vehicle = emergencyManagementService.deployVehicle(vehicle.getId(), quadrant2.getId());
//    team = emergencyManagementService.deployTeam(team.getId(), quadrant2.getId());
//
//    vehicle = emergencyManagementService.deployVehicle(vehicle.getId(), quadrant3.getId());
//    team = emergencyManagementService.deployTeam(team.getId(), quadrant3.getId());
//    LocalDateTime endDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
//
//    Assertions.assertEquals(1,
//        logManagementService.findTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt(quadrant, starDate, endDate)
//            .size());
//    Assertions.assertEquals(1,
//        logManagementService.findTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt(quadrant2, starDate,
//            endDate).size());
//
//    Assertions.assertEquals(1,
//        logManagementService.findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(quadrant, starDate,
//            endDate).size());
//    Assertions.assertEquals(1,
//        logManagementService.findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(quadrant2, starDate,
//            endDate).size());
//
//  }

}
