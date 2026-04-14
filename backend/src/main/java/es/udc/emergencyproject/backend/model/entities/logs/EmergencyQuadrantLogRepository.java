package es.udc.emergencyproject.backend.model.entities.logs;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmergencyQuadrantLogRepository extends JpaRepository<EmergencyQuadrantLog, Long> {

  EmergencyQuadrantLog findByEmergencyIdAndQuadrantId(Long emergencyId, Integer quadrantId);

  List<EmergencyQuadrantLog> findByEmergencyIdAndLinkedAtBetweenOrderByLinkedAt(Long emergencyId,
      LocalDateTime startDate,
      LocalDateTime endDate);


  @Query("SELECT eql.quadrant.id FROM EmergencyQuadrantLog eql WHERE eql.emergency.id = :emergencyId")
  List<Integer> findQuadrantIdsByEmergencyId(@Param("emergencyId") Long emergencyId);


}
