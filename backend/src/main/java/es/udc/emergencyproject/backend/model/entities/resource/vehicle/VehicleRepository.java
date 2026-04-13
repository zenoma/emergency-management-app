package es.udc.emergencyproject.backend.model.entities.resource.vehicle;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

  List<Vehicle> findByOrganizationIdOrderByVehiclePlate(Long organizationId);


  List<Vehicle> findVehiclesByOrganizationIdAndDismantleAtIsNullOrderByVehiclePlate(Long organizationId);


  List<Vehicle> findVehiclesByDismantleAtIsNullOrderByVehiclePlate();
}
