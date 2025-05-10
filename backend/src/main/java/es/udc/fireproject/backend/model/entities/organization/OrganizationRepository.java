package es.udc.fireproject.backend.model.entities.organization;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  List<Organization> findByNameIgnoreCaseContainsOrCodeIgnoreCaseContains(String name, String code);

  List<Organization> findByOrganizationType_NameIgnoreCaseContainsOrderByCodeAsc(String name);


}
