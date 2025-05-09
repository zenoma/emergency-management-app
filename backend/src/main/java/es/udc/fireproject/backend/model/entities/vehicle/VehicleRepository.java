package es.udc.fireproject.backend.model.entities.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByOrganizationIdOrderByVehiclePlate(Long organizationId);


    List<Vehicle> findVehiclesByOrganizationIdAndDismantleAtIsNullOrderByVehiclePlate(Long organizationId);


    List<Vehicle> findVehiclesByDismantleAtIsNullOrderByVehiclePlate();
}
