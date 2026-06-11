package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.organization.OrganizationType;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import es.udc.emergencyproject.backend.model.services.resources.ResourceManagementFacade;
import es.udc.emergencyproject.backend.utils.OrganizationOM;
import es.udc.emergencyproject.backend.utils.OrganizationTypeOM;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class VehicleServiceImplTest extends IntegrationTest {

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
  void givenValidData_whenCreateVehicle_thenCreated() throws InstanceNotFoundException {
    Organization organization = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("12345ABC", "Car", organization.getId());
    Assertions.assertNotNull(vehicle.getId());

    Vehicle found = resourceManagementFacade.findVehicleById(vehicle.getId());
    Assertions.assertEquals(vehicle.getVehiclePlate(), found.getVehiclePlate());
  }

  @Test
  void givenInvalidId_whenFindVehicleById_thenThrows() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> resourceManagementFacade.findVehicleById(-1L));
  }

  @Test
  void givenValidData_whenUpdateVehicle_thenUpdated() throws InstanceNotFoundException {
    Organization organization = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("12345ABC", "Car", organization.getId());
    resourceManagementFacade.updateVehicle(vehicle.getId(), "NEWPLATE", "Truck");
    Vehicle updated = resourceManagementFacade.findVehicleById(vehicle.getId());
    Assertions.assertEquals("NEWPLATE", updated.getVehiclePlate());
    Assertions.assertEquals("Truck", updated.getType());
  }

  @Test
  void givenInvalidData_whenUpdateVehicle_thenConstraintViolation() throws InstanceNotFoundException {
    Organization organization = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("12345ABC", "Car", organization.getId());
    Assertions.assertThrows(ConstraintViolationException.class,
        () -> resourceManagementFacade.updateVehicle(vehicle.getId(), "", ""));
  }

  @Test
  void givenValidId_whenDismantleVehicle_thenDismantled() throws InstanceNotFoundException {
    Organization organization = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("12345ABC", "Car", organization.getId());
    resourceManagementFacade.dismantleVehicleById(vehicle.getId());
    Assertions.assertNotNull(resourceManagementFacade.findVehicleById(vehicle.getId()).getDismantleAt());
  }

  @Test
  void givenAlreadyDismantled_whenDismantleAgain_thenThrows() throws InstanceNotFoundException {
    Organization organization = createDefaultOrganization();
    Vehicle vehicle = resourceManagementFacade.createVehicle("12345ABC", "Car", organization.getId());
    resourceManagementFacade.dismantleVehicleById(vehicle.getId());
    Assertions.assertThrows(AlreadyDismantledException.class,
        () -> resourceManagementFacade.dismantleVehicleById(vehicle.getId()));
  }

  @Test
  void givenVehiclesInOrganization_whenFindByOrganization_thenFound() throws InstanceNotFoundException {
    Organization organization = createDefaultOrganization();
    resourceManagementFacade.createVehicle("11111AA", "Car", organization.getId());
    resourceManagementFacade.createVehicle("22222BB", "Van", organization.getId());

    List<Vehicle> vehicles = resourceManagementFacade.findVehiclesByOrganizationId(organization.getId());
    Assertions.assertEquals(2, vehicles.size());
  }

  @Test
  void givenVehicles_whenFindActiveByOrganization_thenOnlyActive() throws InstanceNotFoundException {
    Organization organization = createDefaultOrganization();
    resourceManagementFacade.createVehicle("11111AA", "Car", organization.getId());
    Vehicle toDismantle = resourceManagementFacade.createVehicle("22222BB", "Van", organization.getId());
    resourceManagementFacade.dismantleVehicleById(toDismantle.getId());

    List<Vehicle> active = resourceManagementFacade.findActiveVehiclesByOrganizationId(organization.getId());
    Assertions.assertEquals(1, active.size());
  }

  @Test
  void givenVehicles_whenFindAllVehicles_thenAllFound() throws InstanceNotFoundException {
    Organization organization = createDefaultOrganization();
    Vehicle v1 = resourceManagementFacade.createVehicle("11111AA", "Car", organization.getId());
    Vehicle v2 = resourceManagementFacade.createVehicle("22222BB", "Van", organization.getId());

    List<Vehicle> all = resourceManagementFacade.findAllVehicles();
    Assertions.assertTrue(all.stream().anyMatch(v -> v.getId().equals(v1.getId())));
    Assertions.assertTrue(all.stream().anyMatch(v -> v.getId().equals(v2.getId())));
  }

  @Test
  void givenVehicles_whenFindAllActive_thenOnlyActive() throws InstanceNotFoundException {
    Organization organization = createDefaultOrganization();
    resourceManagementFacade.createVehicle("11111AA", "Car", organization.getId());
    Vehicle toDismantle = resourceManagementFacade.createVehicle("22222BB", "Van", organization.getId());
    resourceManagementFacade.dismantleVehicleById(toDismantle.getId());

    List<Vehicle> active = resourceManagementFacade.findAllActiveVehicles();
    Assertions.assertTrue(active.stream().anyMatch(v -> v.getVehiclePlate().equals("11111AA")));
    Assertions.assertTrue(active.stream().noneMatch(v -> v.getVehiclePlate().equals("22222BB")));
  }

  @Test
  void givenInvalidOrganization_whenCreateVehicle_thenThrows() {
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> resourceManagementFacade.createVehicle("12345ABC", "Car", 9999L));
  }
}
