package es.udc.fireproject.backend.model.services.personalmanagement;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import java.util.List;
import org.locationtech.jts.geom.Point;


public interface OrganizationService {

  OrganizationType createOrganizationType(String name);

  OrganizationType findOrganizationTypeById(Long id) throws InstanceNotFoundException;

  List<OrganizationType> findAllOrganizationTypes();

  List<Organization> findOrganizationByNameOrCode(String nameOrCode);

  Organization findOrganizationById(Long id) throws InstanceNotFoundException;

  List<Organization> findOrganizationByOrganizationTypeName(String organizationTypeName);

  List<Organization> findAllOrganizations();

  Organization createOrganization(String code, String name, String headquartersAddress, Point location,
      String organizationTypeName);

  Organization createOrganization(Organization organization);

  void deleteOrganizationById(Long id);

  Organization updateOrganization(Long id, String name, String code, String headquartersAddress, Point location)
      throws InstanceNotFoundException;


}
