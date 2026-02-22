package es.udc.fireproject.backend.model.services.personalmanagement;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.entities.vehicle.VehicleRepository;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.model.services.utils.ConstraintValidator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

  private static final String VEHICLE_NOT_FOUND = "Vehicle not found";
  private final VehicleRepository vehicleRepository;
  private final OrganizationService organizationService;
  private final FireManagementService fireManagementService;

  @Override
  public Vehicle createVehicle(String vehiclePlate, String type, Long organizationId) throws InstanceNotFoundException {
    Organization organization = organizationService.findOrganizationById(organizationId);

    Vehicle vehicle = new Vehicle(vehiclePlate, type, organization);
    vehicle.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

    ConstraintValidator.validate(vehicle);

    return vehicleRepository.save(vehicle);
  }


  @Override
  public void dismantleVehicleById(Long id) throws InstanceNotFoundException, AlreadyDismantledException {
    Vehicle vehicle = vehicleRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(VEHICLE_NOT_FOUND, id));

    if (vehicle.getDismantleAt() == null) {
      fireManagementService.retractVehicle(vehicle.getId());
      vehicle.setDismantleAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

      vehicleRepository.save(vehicle);
    } else {
      throw new AlreadyDismantledException(Vehicle.class.getSimpleName(), vehicle.getVehiclePlate());
    }

  }

  @Override
  public Vehicle updateVehicle(Long id, String vehiclePlate, String type)
      throws InstanceNotFoundException, AlreadyDismantledException {
    Vehicle vehicle = vehicleRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(VEHICLE_NOT_FOUND, id));
    if (vehicle.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), vehicle.getVehiclePlate());
    }

    vehicle.setVehiclePlate(vehiclePlate);
    vehicle.setType(type);

    ConstraintValidator.validate(vehicle);
    return vehicleRepository.save(vehicle);
  }


  @Override
  public Vehicle findVehicleById(Long id) throws InstanceNotFoundException {
    return vehicleRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(VEHICLE_NOT_FOUND, id));
  }


  @Override
  public List<Vehicle> findVehiclesByOrganizationId(Long organizationId) {
    return vehicleRepository.findByOrganizationIdOrderByVehiclePlate(organizationId);
  }

  @Override
  public List<Vehicle> findActiveVehiclesByOrganizationId(Long organizationId) {

    return vehicleRepository.findVehiclesByOrganizationIdAndDismantleAtIsNullOrderByVehiclePlate(organizationId);

  }


  @Override
  public List<Vehicle> findAllVehicles() {
    return vehicleRepository.findAll();
  }


  @Override
  public List<Vehicle> findAllActiveVehicles() {
    return vehicleRepository.findVehiclesByDismantleAtIsNullOrderByVehiclePlate();
  }

}
