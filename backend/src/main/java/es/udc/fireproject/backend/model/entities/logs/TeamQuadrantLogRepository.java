package es.udc.fireproject.backend.model.entities.logs;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamQuadrantLogRepository extends JpaRepository<TeamQuadrantLog, Long> {

  TeamQuadrantLog findByTeamIdAndQuadrantIdAndRetractAtIsNull(Long teamId, Integer quadrantId);

  List<TeamQuadrantLog> findByQuadrantIdAndDeployAtBetweenOrderByDeployAt(Integer quadrantId, LocalDateTime startDate,
      LocalDateTime endDate);


  @Query("SELECT tql.team.id FROM TeamQuadrantLog tql WHERE tql.quadrant.id = :quadrantId")
  List<Long> findTeamsIdsByQuadrantsGid(@Param("quadrantId") Integer quadrantId);

  @Query("SELECT tql.team.id FROM TeamQuadrantLog tql WHERE tql.quadrant.id IN :quadrantIds")
  List<Long> findTeamsIdsByQuadrantsGids(@Param("quadrantIds") List<Integer> quadrantIds);

}
