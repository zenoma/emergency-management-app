package es.udc.fireproject.backend.model.services.personalmanagement;

import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import java.util.List;

public interface VehicleService {

  Vehicle createVehicle(String vehiclePlate, String type, Long organizationId) throws InstanceNotFoundException;

  void dismantleVehicleById(Long id) throws InstanceNotFoundException, AlreadyDismantledException;

  Vehicle updateVehicle(Long id, String vehiclePlate, String type)
      throws InstanceNotFoundException, AlreadyDismantledException;

  Vehicle findVehicleById(Long teamId) throws InstanceNotFoundException;

  List<Vehicle> findVehiclesByOrganizationId(Long organizationId);

  List<Vehicle> findActiveVehiclesByOrganizationId(Long organizationId);

  List<Vehicle> findAllVehicles();

  List<Vehicle> findAllActiveVehicles();
}
