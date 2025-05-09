package es.udc.fireproject.backend.model.entities.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    List<Organization> findByNameIgnoreCaseContainsOrCodeIgnoreCaseContains(String name, String code);

    List<Organization> findByOrganizationType_NameIgnoreCaseContainsOrderByCodeAsc(String name);


}
