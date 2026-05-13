package es.udc.emergencyproject.backend.model.entities.resource.team;

import java.util.List;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

public interface TeamRepository extends JpaRepository<Team, Long> {

  List<Team> findTeamsByCodeContains(String code);

  List<Team> findTeamsByOrganizationIdOrderByCode(Long organizationId);

  List<Team> findTeamsByOrganizationIdAndDismantleAtIsNullOrderByCode(Long organizationId);

  List<Team> findTeamsByDismantleAtIsNullOrderByCode();

  @Query("select t from Team t join t.organization o "
      + "where t.removed = false and t.dismantled = false and t.dismantleAt is null "
      + "and t.status = es.udc.emergencyproject.backend.model.entities.resource.ResourceStatus.AVAILABLE "
      + "and o.location is not null "
      + "order by function('ST_Distance', o.location, :location) asc")
  List<Team> findAvailableClosestToLocation(@Param("location") Point location, Pageable pageable);


}
