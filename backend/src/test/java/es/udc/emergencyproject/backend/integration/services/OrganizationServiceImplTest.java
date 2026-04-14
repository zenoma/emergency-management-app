package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.organization.OrganizationType;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import es.udc.emergencyproject.backend.utils.OrganizationOM;
import es.udc.emergencyproject.backend.utils.OrganizationTypeOM;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;


@RequiredArgsConstructor
class OrganizationServiceImplTest extends IntegrationTest {

  private final PersonalManagementFacade personalManagementFacade;

  @Test
  void givenInvalidString_whenCreateOrganizationType_theConstraintViolationException() {

    String name = OrganizationTypeOM.withInvalidName().getName();
    Assertions.assertThrows(ConstraintViolationException.class,
        () -> personalManagementFacade.createOrganizationType(name),
        "ConstraintViolationException error was expected");
  }

  @Test
  void givenValidData_whenCreationOrganization_thenReturnOrganizationWithId() {

    OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    organizationType = personalManagementFacade.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();

    Organization result = personalManagementFacade.createOrganization(organization.getCode(),
        organization.getName(),
        organization.getHeadquartersAddress(),
        organization.getLocation(),
        organizationType.getName());

    Assertions.assertNotNull(result.getId(), "Id must be not Null");
    Assertions.assertNotNull(result.getCreatedAt(), "Created date must be not Null");

  }

  @Test
  void givenValidOrganization_whenCreationOrganization_thenReturnOrganizationWithId() {

    OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();

    Organization result = personalManagementFacade.createOrganization(organization);

    Assertions.assertNotNull(result.getId(), "Id must be not Null");
    Assertions.assertNotNull(result.getCreatedAt(), "Created date must be not Null");

  }


  @Test
  void givenInvalidOrganizationData_whenCreationOrganization_thenConstraintViolationException() {

    Organization organization = OrganizationOM.withInvalidValues();

    Assertions.assertThrows(ConstraintViolationException.class,
        () -> personalManagementFacade.createOrganization(organization),
        "ConstraintViolationException error was expected");

  }

  @Test
  void givenValidName_whenFindByNameOrCode_thenFoundOrganization() {
    final Organization organization = OrganizationOM.withDefaultValues();

    final OrganizationType createdOrganizationType = personalManagementFacade.createOrganizationType(
        organization.getOrganizationType().getName());
    final Organization createdOrganization = personalManagementFacade.createOrganization(organization);

    final List<Organization> organizationList = new ArrayList<>();
    organization.setId(createdOrganization.getId());
    organization.getOrganizationType().setId(createdOrganization.getOrganizationType().getId());
    organizationList.add(organization);

    Assertions.assertEquals(organizationList,
        personalManagementFacade.findOrganizationByNameOrCode(organization.getName()));
  }


  @Test
  void givenInvalidName_whenFindByNameOrCode_thenReturnEmptyList() {

    Assertions.assertTrue(personalManagementFacade.findOrganizationByNameOrCode("").isEmpty(),
        "Found item must be null");
  }


  @Test
  void givenValidId_whenFindById_thenFoundOrganization() throws InstanceNotFoundException {
    Organization organization = OrganizationOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organization.getOrganizationType().getName());
    organization = personalManagementFacade.createOrganization(organization);

    Assertions.assertEquals(organization, personalManagementFacade.findOrganizationById(organization.getId()));
  }

  @Test
  void givenInvalidName_whenFindOrganizationById_thenInstanceNotFoundException() {

    Assertions.assertThrows(InstanceNotFoundException.class, () -> personalManagementFacade.findOrganizationById(-1L),
        "InstanceNotFoundException not thrown");
  }

  @Test
  void givenValidId_whenFindOrganizationTypeById_thenFoundOrganizationType() throws InstanceNotFoundException {
    OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    organizationType = personalManagementFacade.createOrganizationType(organizationType.getName());

    Assertions.assertEquals(organizationType,
        personalManagementFacade.findOrganizationTypeById(organizationType.getId()));
  }

  @Test
  void givenInvalidName_whenFindOrganizationTypeById_thenInstanceNotFoundException() {

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> personalManagementFacade.findOrganizationTypeById(-1L), "InstanceNotFoundException not thrown");
  }

  @Test
  void givenValidId_whenFindByOrganizationTypeName_thenFoundOrganization() {
    List<OrganizationType> organizationTypes = OrganizationTypeOM.withNames(Arrays.asList("Type1", "Type2", "Type3"));
    organizationTypes.forEach(organizationType ->
        personalManagementFacade.createOrganizationType(organizationType.getName()));

    List<Organization> organizations = new ArrayList<>();
    Organization organization = OrganizationOM.withOrganizationTypeAndRandomNames(organizationTypes.get(0).getName());
    organization = personalManagementFacade.createOrganization(organization);
    organizations.add(organization);

    organization = OrganizationOM.withOrganizationTypeAndRandomNames(organizationTypes.get(0).getName());
    organization = personalManagementFacade.createOrganization(organization);
    organizations.add(organization);

    organization = OrganizationOM.withOrganizationTypeAndRandomNames(organizationTypes.get(1).getName());
    personalManagementFacade.createOrganization(organization);

    Assertions.assertEquals(organizations.size(),
        personalManagementFacade.findOrganizationByOrganizationTypeName(organizationTypes.get(0).getName()).size(),
        "The result must contain the same number of items");

    Assertions.assertTrue(
        personalManagementFacade.findOrganizationByOrganizationTypeName(organizationTypes.get(0).getName())
            .containsAll(organizations),
        "The result must contain all items");
  }

  @Test
  void givenInvalidName_whenFindByOrganizationTypeName_thenReturnEmptyList() {

    Assertions.assertTrue(personalManagementFacade.findOrganizationByOrganizationTypeName("").isEmpty(),
        "The result must be an empty list");
  }


  @Test
  void givenData_whenFindAllOrganizationTypes_thenReturnFoundOrganizationType() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    final OrganizationType createdOrganizationType = personalManagementFacade.createOrganizationType(
        organizationType.getName());

    final List<OrganizationType> result = personalManagementFacade.findAllOrganizationTypes();

    organizationType.setId(createdOrganizationType.getId());

    Assertions.assertTrue(result.contains(organizationType), "Result must contain the same Data");
  }

  @Test
  void givenNoData_whenCallFindAll_thenReturnEmptyList() {
    final List<Organization> result = personalManagementFacade.findAllOrganizations();

    Assertions.assertTrue(result.isEmpty(), "Result must be Empty");
  }

  @Test
  void givenData_whenFindAll_thenReturnNotEmptyList() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationType.getName());
    final Organization organization = OrganizationOM.withDefaultValues();
    final Organization createdOrganization = personalManagementFacade.createOrganization(organization);

    organization.getOrganizationType().setId(createdOrganization.getOrganizationType().getId());
    organization.setId(createdOrganization.getId());

    final List<Organization> result = personalManagementFacade.findAllOrganizations();

    Assertions.assertFalse(result.isEmpty(), "Result must be not empty");
    Assertions.assertTrue(result.contains(organization), "Result must contain the same Data");
  }

  @Test
  void givenValidId_whenDelete_thenDeletedSuccessfully() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();
    organization = personalManagementFacade.createOrganization(organization);

    personalManagementFacade.deleteOrganizationById(organization.getId());

    Assertions.assertTrue(personalManagementFacade.findAllOrganizations().isEmpty(), "Expected result must be Empty");
  }


  @Test
  void givenValidData_whenUpdate_thenUpdatedSuccessfully() throws InstanceNotFoundException {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();
    organization = personalManagementFacade.createOrganization(organization);

    Organization updatedOrganization = personalManagementFacade.updateOrganization(organization.getId(),
        "New Name",
        "New Code",
        "New HeadQuarters Address",
        organization.getLocation());

    Assertions.assertEquals(organization, updatedOrganization);


  }

  @Test
  void givenInvadalidData_whenUpdate_thenConstraintViolationException() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();
    organization = personalManagementFacade.createOrganization(organization);

    Long id = organization.getId();
    Point location = organization.getLocation();
    Assertions.assertThrows(ConstraintViolationException.class, () -> personalManagementFacade.updateOrganization(id,
            "",
            "",
            "",
            location)
        , "ConstraintViolationException error was expected");


  }

  @Test
  void givenInvadalidId_whenUpdate_thenInstanceNotFoundException() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();
    organization = personalManagementFacade.createOrganization(organization);

    Point location = organization.getLocation();
    Assertions.assertThrows(InstanceNotFoundException.class, () -> personalManagementFacade.updateOrganization(-1L,
            "",
            "",
            "",
            location)
        , "InstanceNotFoundException error was expected");


  }
}
