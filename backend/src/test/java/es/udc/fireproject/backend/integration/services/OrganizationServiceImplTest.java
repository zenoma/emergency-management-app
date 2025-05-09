package es.udc.fireproject.backend.integration.services;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.utils.OrganizationOM;
import es.udc.fireproject.backend.utils.OrganizationTypeOM;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootTest
@Transactional
class OrganizationServiceImplTest {

  @Autowired
  PersonalManagementService personalManagementService;

  @Test
  void givenInvalidString_whenCreateOrganizationType_theConstraintViolationException() {

    String name = OrganizationTypeOM.withInvalidName().getName();
    Assertions.assertThrows(ConstraintViolationException.class,
        () -> personalManagementService.createOrganizationType(name),
        "ConstraintViolationException error was expected");
  }

  @Test
  void givenValidData_whenCreationOrganization_thenReturnOrganizationWithId() {

    OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    organizationType = personalManagementService.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();

    Organization result = personalManagementService.createOrganization(organization.getCode(),
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
    personalManagementService.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();

    Organization result = personalManagementService.createOrganization(organization);

    Assertions.assertNotNull(result.getId(), "Id must be not Null");
    Assertions.assertNotNull(result.getCreatedAt(), "Created date must be not Null");

  }


  @Test
  void givenInvalidOrganizationData_whenCreationOrganization_thenConstraintViolationException() {

    Organization organization = OrganizationOM.withInvalidValues();

    Assertions.assertThrows(ConstraintViolationException.class,
        () -> personalManagementService.createOrganization(organization),
        "ConstraintViolationException error was expected");

  }

  @Test
  void givenValidName_whenFindByNameOrCode_thenFoundOrganization() {
    final Organization organization = OrganizationOM.withDefaultValues();
    final List<Organization> organizationList = new ArrayList<>();
    organizationList.add(organization);

    personalManagementService.createOrganizationType(organization.getOrganizationType().getName());
    personalManagementService.createOrganization(organization);

    Assertions.assertEquals(organizationList,
        personalManagementService.findOrganizationByNameOrCode(organization.getName()));
  }


  @Test
  void givenInvalidName_whenFindByNameOrCode_thenReturnEmptyList() {

    Assertions.assertTrue(personalManagementService.findOrganizationByNameOrCode("").isEmpty(),
        "Found item must be null");
  }


  @Test
  void givenValidId_whenFindById_thenFoundOrganization() throws InstanceNotFoundException {
    Organization organization = OrganizationOM.withDefaultValues();
    personalManagementService.createOrganizationType(organization.getOrganizationType().getName());
    organization = personalManagementService.createOrganization(organization);

    Assertions.assertEquals(organization, personalManagementService.findOrganizationById(organization.getId()));
  }

  @Test
  void givenInvalidName_whenFindOrganizationById_thenInstanceNotFoundException() {

    Assertions.assertThrows(InstanceNotFoundException.class, () -> personalManagementService.findOrganizationById(-1L),
        "InstanceNotFoundException not thrown");
  }

  @Test
  void givenValidId_whenFindOrganizationTypeById_thenFoundOrganizationType() throws InstanceNotFoundException {
    OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    organizationType = personalManagementService.createOrganizationType(organizationType.getName());

    Assertions.assertEquals(organizationType,
        personalManagementService.findOrganizationTypeById(organizationType.getId()));
  }

  @Test
  void givenInvalidName_whenFindOrganizationTypeById_thenInstanceNotFoundException() {

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> personalManagementService.findOrganizationTypeById(-1L), "InstanceNotFoundException not thrown");
  }

  @Test
  void givenValidId_whenFindByOrganizationTypeName_thenFoundOrganization() {
    List<OrganizationType> organizationTypes = OrganizationTypeOM.withNames(Arrays.asList("Type1", "Type2", "Type3"));
    organizationTypes.forEach(organizationType ->
        personalManagementService.createOrganizationType(organizationType.getName()));

    List<Organization> organizations = new ArrayList<>();
    Organization organization = OrganizationOM.withOrganizationTypeAndRandomNames(organizationTypes.get(0).getName());
    organization = personalManagementService.createOrganization(organization);
    organizations.add(organization);

    organization = OrganizationOM.withOrganizationTypeAndRandomNames(organizationTypes.get(0).getName());
    organization = personalManagementService.createOrganization(organization);
    organizations.add(organization);

    organization = OrganizationOM.withOrganizationTypeAndRandomNames(organizationTypes.get(1).getName());
    personalManagementService.createOrganization(organization);

    Assertions.assertEquals(organizations.size(),
        personalManagementService.findOrganizationByOrganizationTypeName(organizationTypes.get(0).getName()).size(),
        "The result must contain the same number of items");

    Assertions.assertTrue(
        personalManagementService.findOrganizationByOrganizationTypeName(organizationTypes.get(0).getName())
            .containsAll(organizations),
        "The result must contain all items");
  }

  @Test
  void givenInvalidName_whenFindByOrganizationTypeName_thenReturnEmptyList() {

    Assertions.assertTrue(personalManagementService.findOrganizationByOrganizationTypeName("").isEmpty(),
        "The result must be an empty list");
  }


  @Test
  void givenData_whenFindAllOrganizationTypes_thenReturnFoundOrganizationType() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementService.createOrganizationType(organizationType.getName());

    final List<OrganizationType> result = personalManagementService.findAllOrganizationTypes();

    Assertions.assertTrue(result.contains(organizationType), "Result must contain the same Data");
  }

  @Test
  void givenNoData_whenCallFindAll_thenReturnEmptyList() {
    final List<Organization> result = personalManagementService.findAllOrganizations();

    Assertions.assertTrue(result.isEmpty(), "Result must be Empty");
  }

  @Test
  void givenData_whenFindAll_thenReturnNotEmptyList() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementService.createOrganizationType(organizationType.getName());
    final Organization organization = OrganizationOM.withDefaultValues();
    personalManagementService.createOrganization(organization);

    final List<Organization> result = personalManagementService.findAllOrganizations();

    Assertions.assertFalse(result.isEmpty(), "Result must be not empty");
    Assertions.assertTrue(result.contains(organization), "Result must contain the same Data");
  }

  @Test
  void givenValidId_whenDelete_thenDeletedSuccessfully() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementService.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();
    organization = personalManagementService.createOrganization(organization);

    personalManagementService.deleteOrganizationById(organization.getId());

    Assertions.assertTrue(personalManagementService.findAllOrganizations().isEmpty(), "Expected result must be Empty");
  }


  @Test
  void givenValidData_whenUpdate_thenUpdatedSuccessfully() throws InstanceNotFoundException {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementService.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();
    organization = personalManagementService.createOrganization(organization);

    Organization updatedOrganization = personalManagementService.updateOrganization(organization.getId(),
        "New Name",
        "New Code",
        "New HeadQuarters Address",
        organization.getLocation());

    Assertions.assertEquals(organization, updatedOrganization);


  }

  @Test
  void givenInvadalidData_whenUpdate_thenConstraintViolationException() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementService.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();
    organization = personalManagementService.createOrganization(organization);

    Long id = organization.getId();
    Point location = organization.getLocation();
    Assertions.assertThrows(ConstraintViolationException.class, () -> personalManagementService.updateOrganization(id,
            "",
            "",
            "",
            location)
        , "ConstraintViolationException error was expected");


  }

  @Test
  void givenInvadalidId_whenUpdate_thenInstanceNotFoundException() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    personalManagementService.createOrganizationType(organizationType.getName());

    Organization organization = OrganizationOM.withDefaultValues();
    organization = personalManagementService.createOrganization(organization);

    Point location = organization.getLocation();
    Assertions.assertThrows(InstanceNotFoundException.class, () -> personalManagementService.updateOrganization(-1L,
            "",
            "",
            "",
            location)
        , "InstanceNotFoundException error was expected");


  }
}
