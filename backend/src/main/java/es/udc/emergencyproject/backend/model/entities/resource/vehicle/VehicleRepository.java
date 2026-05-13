package es.udc.emergencyproject.backend.model.entities.resource.vehicle;

import java.util.List;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

  List<Vehicle> findByOrganizationIdOrderByVehiclePlate(Long organizationId);


  List<Vehicle> findVehiclesByOrganizationIdAndDismantleAtIsNullOrderByVehiclePlate(Long organizationId);


  List<Vehicle> findVehiclesByDismantleAtIsNullOrderByVehiclePlate();

  @Query("select v from Vehicle v join v.organization o "
      + "where v.removed = false and v.dismantled = false and v.dismantleAt is null "
      + "and v.status = es.udc.emergencyproject.backend.model.entities.resource.ResourceStatus.AVAILABLE "
      + "and o.location is not null "
      + "order by function('ST_Distance', o.location, :location) asc")
  List<Vehicle> findAvailableClosestToLocation(@Param("location") Point location, Pageable pageable);
}
