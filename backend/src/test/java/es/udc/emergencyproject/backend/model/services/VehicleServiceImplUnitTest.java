package es.udc.emergencyproject.backend.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.entities.resource.vehicle.VehicleRepository;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.personal.OrganizationService;
import es.udc.emergencyproject.backend.model.services.resources.impl.VehicleServiceImpl;
import es.udc.emergencyproject.backend.utils.OrganizationOM;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VehicleServiceImplUnitTest {

  @Mock
  private VehicleRepository vehicleRepository;
  @Mock
  private OrganizationService organizationService;

  @InjectMocks
  private VehicleServiceImpl vehicleService;

  private Organization organization;
  private Vehicle vehicle;

  @BeforeEach
  void setUp() {
    organization = OrganizationOM.withDefaultValues();
    organization.setId(1L);

    vehicle = new Vehicle("12345ABC", "Car", organization);
    vehicle.setId(1L);

    lenient().when(organizationService.findOrganizationById(1L)).thenReturn(organization);
    lenient().when(organizationService.findOrganizationById(anyLong())).thenReturn(organization);
    lenient().when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
    lenient().when(vehicleRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    lenient().when(vehicleRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void givenValidData_whenCreateVehicle_thenSuccess() throws InstanceNotFoundException {
    Vehicle created = vehicleService.createVehicle("NEW001", "Truck", 1L);

    assertNotNull(created);
    assertEquals("NEW001", created.getVehiclePlate());
  }

  @Test
  void givenInvalidOrganization_whenCreateVehicle_thenThrows() {
    when(organizationService.findOrganizationById(anyLong()))
        .thenThrow(new InstanceNotFoundException("Organization", 999L));

    assertThrows(InstanceNotFoundException.class,
        () -> vehicleService.createVehicle("FAIL01", "Car", 999L));
  }

  @Test
  void givenEmptyPlate_whenCreateVehicle_thenConstraintViolation() {
    assertThrows(ConstraintViolationException.class,
        () -> vehicleService.createVehicle("", "Car", 1L));
  }

  @Test
  void givenValidId_whenDismantleVehicle_thenDismantled()
      throws InstanceNotFoundException, AlreadyDismantledException {
    vehicleService.dismantleVehicleById(1L);

    assertNotNull(vehicle.getDismantleAt());
  }

  @Test
  void givenAlreadyDismantled_whenDismantleAgain_thenThrows()
      throws InstanceNotFoundException, AlreadyDismantledException {
    vehicle.setDismantleAt(java.time.LocalDateTime.now());

    assertThrows(AlreadyDismantledException.class,
        () -> vehicleService.dismantleVehicleById(1L));
  }

  @Test
  void givenNonExistent_whenDismantleVehicle_thenThrows() {
    when(vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> vehicleService.dismantleVehicleById(999L));
  }

  @Test
  void givenValidData_whenUpdateVehicle_thenUpdated()
      throws InstanceNotFoundException, AlreadyDismantledException {
    Vehicle updated = vehicleService.updateVehicle(1L, "NEWPLATE", "Van");

    assertEquals("NEWPLATE", updated.getVehiclePlate());
    assertEquals("Van", updated.getType());
  }

  @Test
  void givenDismantledVehicle_whenUpdate_thenThrows() {
    vehicle.setDismantleAt(java.time.LocalDateTime.now());

    assertThrows(AlreadyDismantledException.class,
        () -> vehicleService.updateVehicle(1L, "NEW", "Van"));
  }

  @Test
  void givenNonExistent_whenUpdateVehicle_thenThrows() {
    when(vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> vehicleService.updateVehicle(999L, "NEW", "Van"));
  }

  @Test
  void givenEmptyPlate_whenUpdateVehicle_thenConstraintViolation() {
    assertThrows(ConstraintViolationException.class,
        () -> vehicleService.updateVehicle(1L, "", "Van"));
  }

  @Test
  void givenValidId_whenFindVehicleById_thenFound() throws InstanceNotFoundException {
    Vehicle found = vehicleService.findVehicleById(1L);
    assertNotNull(found);
    assertEquals("12345ABC", found.getVehiclePlate());
  }

  @Test
  void givenInvalidId_whenFindVehicleById_thenThrows() {
    when(vehicleRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> vehicleService.findVehicleById(999L));
  }

  @Test
  void givenOrganizationId_whenFindVehiclesByOrganizationId_thenReturned() {
    when(vehicleRepository.findByOrganizationIdOrderByVehiclePlate(1L)).thenReturn(List.of(vehicle));

    List<Vehicle> result = vehicleService.findVehiclesByOrganizationId(1L);
    assertEquals(1, result.size());
  }

  @Test
  void givenOrganizationId_whenFindActiveVehiclesByOrganizationId_thenReturned() {
    when(vehicleRepository.findVehiclesByOrganizationIdAndDismantleAtIsNullOrderByVehiclePlate(1L))
        .thenReturn(List.of(vehicle));

    List<Vehicle> result = vehicleService.findActiveVehiclesByOrganizationId(1L);
    assertEquals(1, result.size());
  }

  @Test
  void whenFindAllVehicles_thenReturnAll() {
    when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

    List<Vehicle> result = vehicleService.findAllVehicles();
    assertEquals(1, result.size());
  }

  @Test
  void whenFindAllActiveVehicles_thenReturnActive() {
    when(vehicleRepository.findVehiclesByDismantleAtIsNullOrderByVehiclePlate()).thenReturn(List.of(vehicle));

    List<Vehicle> result = vehicleService.findAllActiveVehicles();
    assertEquals(1, result.size());
  }
}
