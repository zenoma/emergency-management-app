package es.udc.fireproject.backend.model.entities.logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FireQuadrantLogRepository extends JpaRepository<FireQuadrantLog, Long> {

    FireQuadrantLog findByFireIdAndQuadrantId(Long fireId, Integer quadrantId);

    List<FireQuadrantLog> findByFireIdAndLinkedAtBetweenOrderByLinkedAt(Long fireId, LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT fql.quadrant.id FROM FireQuadrantLog fql WHERE fql.fire.id = :fireId")
    List<Integer> findQuadrantIdsByFireId(@Param("fireId") Long fireId);


}
