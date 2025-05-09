package es.udc.fireproject.backend.model.entities.logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleQuadrantLogRepository extends JpaRepository<VehicleQuadrantLog, Long> {

    VehicleQuadrantLog findByVehicleIdAndQuadrantIdAndRetractAtIsNull(Long vehicleId, Integer quadrantId);

    List<VehicleQuadrantLog> findByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Integer quadrantId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT vql.vehicle.id FROM VehicleQuadrantLog vql WHERE vql.quadrant.id = :quadrantId")
    List<Long> findVehiclesIdsByQuadrantsGid(@Param("quadrantId") Integer quadrantId);

}
