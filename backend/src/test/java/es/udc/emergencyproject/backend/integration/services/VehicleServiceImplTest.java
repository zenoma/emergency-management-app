package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import org.springframework.beans.factory.annotation.Autowired;

public class VehicleServiceImplTest extends IntegrationTest {

  private final Long INVALID_VEHICLE_ID = -1L;

  @Autowired
  private PersonalManagementFacade personalManagementFacade;
//
//  @Test
//  void givenInvalidId_whenFindVehicleById_thenReturnInstanceNotFoundException() {
//
//    Assertions.assertThrows(InstanceNotFoundException.class, () ->
//            personalManagementFacade.findVehicleById(INVALID_VEHICLE_ID)
//        , "InstanceNotFoundException error was expected");
//  }
//
//  @Test
//  void givenValidData_whenCreateVehicle_thenCreateVehicle() throws InstanceNotFoundException {
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
//    Assertions.assertEquals(vehicle, personalManagementFacade.findVehicleById(vehicle.getId()));
//  }
//
//  @Test
//  void givenValidData_whenFindByOrganizationID_thenVehiclesFound() throws InstanceNotFoundException {
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
//    ArrayList<Vehicle> vehicles = new ArrayList<>();
//    vehicles.add(vehicle);
//
//    Assertions.assertEquals(vehicles,
//        personalManagementFacade.findVehiclesByOrganizationId(vehicle.getOrganization().getId()));
//  }
//
//
//  @Test
//  void givenInvalidData_whenCallCreate_thenReturnConstraintViolationException() {
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Long id = organization.getId();
//    Assertions.assertThrows(ConstraintViolationException.class, () ->
//            personalManagementFacade.createVehicle("", "", id)
//        , "ConstraintViolationException error was expected");
//  }
//
//  @Test
//  void givenInvalidOrganizationId_whenCallCreate_thenReturnInstanceNotFoundException() {
//
//    Assertions.assertThrows(InstanceNotFoundException.class, () ->
//            personalManagementFacade.createVehicle("", "", 1L)
//        , "InstanceNotFoundException error was expected");
//  }
//
//
//  @Test
//  void givenValidId_whenDismantle_thenDismantleSuccessfully()
//      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
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
//    personalManagementFacade.dismantleVehicleById(vehicle.getId());
//
//    Assertions.assertNotNull(personalManagementFacade.findVehicleById(vehicle.getId()).getDismantleAt(),
//        "Expected result must be not empty");
//  }
//
//
//  @Test
//  void givenVehiclePlate_whenUpdate_thenConstraintViolationException()
//      throws InstanceNotFoundException, AlreadyExistException {
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
//    vehicle.setVehiclePlate("");
//
//    Long id = vehicle.getId();
//    String vehiclePlate = vehicle.getVehiclePlate();
//    String type = vehicle.getType();
//    Assertions.assertThrows(ConstraintViolationException.class, () ->
//            personalManagementFacade.updateVehicle(id, vehiclePlate, type),
//        "ConstraintViolationException error was expected");
//  }
//
//
//  @Test
//  void givenInvalidId_whenUpdate_thenInstanceNotFoundException() {
//
//    Assertions.assertThrows(InstanceNotFoundException.class, () -> personalManagementFacade.updateVehicle(-1L, "", ""),
//        "InstanceNotFoundException error was expected");
//  }
//
//  @Test
//  void givenValidCode_whenUpdate_thenUpdateSuccessfully()
//      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Vehicle vehicle = VehicleOM.withDefaultValues();
//    vehicle = personalManagementFacade.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//    vehicle.setVehiclePlate("New Name");
//
//    Vehicle updateVehicle = personalManagementFacade.updateVehicle(vehicle.getId(), vehicle.getVehiclePlate(),
//        vehicle.getType());
//    Assertions.assertEquals(vehicle, updateVehicle);
//  }
//
//
//  @Test
//  void givenVehicles_whenFindVehiclesByOrganizationId_thenVehiclesFound()
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
//    personalManagementFacade.dismantleVehicleById(vehicle.getId());
//
//    Vehicle vehicle2 = VehicleOM.withDefaultValues();
//    vehicle2.setVehiclePlate("11111abc");
//    vehicle2 = personalManagementFacade.createVehicle(vehicle2.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//
//    ArrayList<Vehicle> vehicles = new ArrayList<>();
//    vehicles.add(vehicle2);
//    vehicles.add(vehicle);
//
//    Assertions.assertEquals(vehicles,
//        personalManagementFacade.findVehiclesByOrganizationId(vehicle.getOrganization().getId()));
//  }
//
//  @Test
//  void givenVehicles_whenFindActivesByOrganizationID_thenVehiclesFound()
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
//    personalManagementFacade.dismantleVehicleById(vehicle.getId());
//
//    Vehicle vehicle2 = VehicleOM.withDefaultValues();
//    vehicle2.setVehiclePlate("11111abc");
//    vehicle2 = personalManagementFacade.createVehicle(vehicle2.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//
//    ArrayList<Vehicle> vehicles = new ArrayList<>();
//    vehicles.add(vehicle2);
//
//    Assertions.assertEquals(vehicles,
//        personalManagementFacade.findActiveVehiclesByOrganizationId(vehicle.getOrganization().getId()));
//  }
//
//  @Test
//  void givenVehicles_whenFindAllVehicles_thenAllVehiclesFound()
//      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
//    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
//        OrganizationTypeOM.withDefaultValues().getName());
//    Organization organization = OrganizationOM.withDefaultValues();
//    organization.setOrganizationType(organizationType);
//    organization = personalManagementFacade.createOrganization(organization);
//
//    Vehicle vehicle = VehicleOM.withDefaultValues();
//    vehicle = personalManagementFacade.createVehicle(vehicle.getVehiclePlate(), vehicle.getType(),
//        organization.getId());
//    personalManagementFacade.dismantleVehicleById(vehicle.getId());
//
//    Vehicle vehicle2 = VehicleOM.withDefaultValues();
//    vehicle2.setVehiclePlate("22222ABC");
//    vehicle2 = personalManagementFacade.createVehicle(vehicle2.getVehiclePlate(), vehicle2.getType(),
//        organization.getId());
//
//    Organization organization2 = OrganizationOM.withOrganizationTypeAndRandomNames("Organization 2");
//    organization2.setOrganizationType(organizationType);
//    organization2 = personalManagementFacade.createOrganization(organization2);
//
//    Vehicle vehicle3 = VehicleOM.withDefaultValues();
//    vehicle3.setVehiclePlate("44444ABC");
//    vehicle3 = personalManagementFacade.createVehicle(vehicle3.getVehiclePlate(), vehicle3.getType(),
//        organization2.getId());
//    personalManagementFacade.dismantleVehicleById(vehicle3.getId());
//
//    ArrayList<Vehicle> vehicles = new ArrayList<>();
//
//    vehicles.add(vehicle2);
//    vehicles.add(vehicle);
//    vehicles.add(vehicle3);
//
//    Assertions.assertEquals(vehicles, personalManagementFacade.findAllVehicles());
//  }
//
//
//  @Test
//  void givenVehicle_whenFindAllActiveVehicles_thenActiveVehiclesFound()
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
//    personalManagementFacade.dismantleVehicleById(vehicle.getId());
//
//    Vehicle vehicle2 = VehicleOM.withDefaultValues();
//    vehicle2.setVehiclePlate("11111ABC");
//    vehicle2 = personalManagementFacade.createVehicle(vehicle2.getVehiclePlate(), vehicle2.getType(),
//        organization.getId());
//
//    Organization organization2 = OrganizationOM.withOrganizationTypeAndRandomNames("Organization 2");
//    organization2.setOrganizationType(organizationType);
//    organization2 = personalManagementFacade.createOrganization(organization2);
//
//    Vehicle vehicle3 = VehicleOM.withDefaultValues();
//    vehicle3.setVehiclePlate("22222ABC");
//    vehicle3 = personalManagementFacade.createVehicle(vehicle3.getVehiclePlate(), vehicle3.getType(),
//        organization.getId());
//    personalManagementFacade.dismantleVehicleById(vehicle3.getId());
//
//    Vehicle vehicle4 = VehicleOM.withDefaultValues();
//    vehicle4.setVehiclePlate("333333ABC");
//    vehicle4 = personalManagementFacade.createVehicle(vehicle4.getVehiclePlate(), vehicle4.getType(),
//        organization.getId());
//
//    ArrayList<Vehicle> vehicles = new ArrayList<>();
//
//    vehicles.add(vehicle2);
//    vehicles.add(vehicle4);
//
//    Assertions.assertEquals(vehicles, personalManagementFacade.findAllActiveVehicles());
//  }


}
