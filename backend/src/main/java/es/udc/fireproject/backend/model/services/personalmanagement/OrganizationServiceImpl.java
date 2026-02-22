package es.udc.fireproject.backend.model.services.personalmanagement;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationRepository;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.entities.organization.OrganizationTypeRepository;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.utils.ConstraintValidator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

  private final OrganizationRepository organizationRepository;
  private final OrganizationTypeRepository organizationTypeRepository;


  public List<Organization> findOrganizationByNameOrCode(String nameOrCode) {
    return organizationRepository.findByNameIgnoreCaseContainsOrCodeIgnoreCaseContains(nameOrCode, nameOrCode);
  }


  public Organization findOrganizationById(Long id) throws InstanceNotFoundException {
    return organizationRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException("Organization not found", id));

  }


  public List<Organization> findOrganizationByOrganizationTypeName(String organizationTypeName) {
    return organizationRepository.findByOrganizationType_NameIgnoreCaseContainsOrderByCodeAsc(organizationTypeName);
  }

  public List<OrganizationType> findAllOrganizationTypes() {
    return organizationTypeRepository.findAll();

  }


  public List<Organization> findAllOrganizations() {
    return organizationRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
  }


  public OrganizationType createOrganizationType(String name) {
    OrganizationType organizationType = new OrganizationType();
    organizationType.setName(name);

    ConstraintValidator.validate(organizationType);
    return organizationTypeRepository.save(organizationType);
  }


  public OrganizationType findOrganizationTypeById(Long id) throws InstanceNotFoundException {
    return organizationTypeRepository.findById(id).orElseThrow(
        () -> new InstanceNotFoundException("Organization type not found", id));

  }


  public Organization createOrganization(String code, String name, String headquartersAddress, Point location,
      String organizationTypeName) {
    OrganizationType organizationType = organizationTypeRepository.findByName(organizationTypeName);
    Organization organization = new Organization(code, name, headquartersAddress, location, organizationType);

    organization.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    organization.setOrganizationType(organizationType);
    ConstraintValidator.validate(organization);
    return organizationRepository.save(organization);
  }


  public Organization createOrganization(Organization organization) {

    return createOrganization(organization.getCode(),
        organization.getName(),
        organization.getHeadquartersAddress(),
        organization.getLocation(),
        organization.getOrganizationType().getName());
  }


  public void deleteOrganizationById(Long id) {
    organizationRepository.deleteById(id);
  }


  public Organization updateOrganization(Long id, String name, String code, String headquartersAddress, Point location)
      throws InstanceNotFoundException {

    Organization organization = organizationRepository.findById(id).orElseThrow(
        () -> new InstanceNotFoundException("Organization not found", id));

    organization.setName(name);
    organization.setCode(code);
    organization.setHeadquartersAddress(headquartersAddress);
    organization.setLocation(location);

    ConstraintValidator.validate(organization);
    return organizationRepository.save(organization);
  }
}
