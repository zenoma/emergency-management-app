package es.udc.fireproject.backend.model.entities.team;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findTeamsByCodeContains(String code);

    List<Team> findTeamsByOrganizationIdOrderByCode(Long organizationId);

    List<Team> findTeamsByOrganizationIdAndDismantleAtIsNullOrderByCode(Long organizationId);

    List<Team> findTeamsByDismantleAtIsNullOrderByCode();


}
