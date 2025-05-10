package es.udc.fireproject.backend.model.entities.organization;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationTypeRepository extends JpaRepository<OrganizationType, Long> {

  OrganizationType findByName(String name);


}
