package es.udc.fireproject.backend.model.entities.logs;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleQuadrantLogRepository extends JpaRepository<VehicleQuadrantLog, Long> {

  VehicleQuadrantLog findByVehicleIdAndQuadrantIdAndRetractAtIsNull(Long vehicleId, Integer quadrantId);

  List<VehicleQuadrantLog> findByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Integer quadrantId,
      LocalDateTime startDate, LocalDateTime endDate);

  @Query("SELECT vql.vehicle.id FROM VehicleQuadrantLog vql WHERE vql.quadrant.id = :quadrantId")
  List<Long> findVehiclesIdsByQuadrantsGid(@Param("quadrantId") Integer quadrantId);

  @Query("SELECT vql.vehicle.id FROM VehicleQuadrantLog vql WHERE vql.quadrant.id IN :quadrantIds")
  List<Long> findVehiclesIdsByQuadrantsGids(@Param("quadrantIds") List<Integer> quadrantIds);

}
