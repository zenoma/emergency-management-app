package es.udc.emergencyproject.backend.model.entities.resource.team;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

  List<Team> findTeamsByCodeContains(String code);

  List<Team> findTeamsByOrganizationIdOrderByCode(Long organizationId);

  List<Team> findTeamsByOrganizationIdAndDismantleAtIsNullOrderByCode(Long organizationId);

  List<Team> findTeamsByDismantleAtIsNullOrderByCode();


}
