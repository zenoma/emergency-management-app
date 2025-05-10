package es.udc.fireproject.backend.model.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationRepository;
import es.udc.fireproject.backend.model.entities.organization.OrganizationType;
import es.udc.fireproject.backend.model.entities.organization.OrganizationTypeRepository;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementServiceImpl;
import es.udc.fireproject.backend.utils.OrganizationOM;
import es.udc.fireproject.backend.utils.OrganizationTypeOM;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;


@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {

  private final Organization defaultOrganization = OrganizationOM.withDefaultValues();


  @Mock
  OrganizationTypeRepository organizationTypeRepository;

  @Mock
  OrganizationRepository organizationRepository;

  @InjectMocks
  PersonalManagementServiceImpl personalManagementService;


  @BeforeEach
  public void setUp() {
    defaultOrganization.setId(1L);

    lenient().when(organizationTypeRepository.findByName(anyString()))
        .thenReturn(OrganizationTypeOM.withDefaultValues());
    lenient().when(organizationRepository.save(any())).thenReturn(defaultOrganization);
    lenient().when(organizationRepository.findById(any())).thenReturn(Optional.of(defaultOrganization));

  }


  @Test
  void givenNoData_whenCallFindAllOrganizationTypes_thenReturnEmptyList() {
    final List<OrganizationType> result = personalManagementService.findAllOrganizationTypes();

    Assertions.assertTrue(result.isEmpty(), "Result must be Empty");
  }

  @Test
  void givenData_whenFindAllOrganizationTypes_thenReturnNotEmptyList() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();

    final List<OrganizationType> list = new ArrayList<>();
    list.add(organizationType);

    when(organizationTypeRepository.findAll()).thenReturn(list);

    final List<OrganizationType> result = personalManagementService.findAllOrganizationTypes();

    Assertions.assertTrue(result.contains(list.get(0)), "Result must contain the same Data");
  }

  @Test
  void givenNoData_whenCallFindAll_thenReturnEmptyList() {
    final List<Organization> result = personalManagementService.findAllOrganizations();

    Assertions.assertTrue(result.isEmpty(), "Result must be Empty");
  }

  @Test
  void givenData_whenFindAll_thenReturnNotEmptyList() {

    final List<Organization> list = new ArrayList<>();
    list.add(defaultOrganization);

    when(organizationRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(list);

    final List<Organization> result = personalManagementService.findAllOrganizations();

    Assertions.assertFalse(result.isEmpty(), "Result must be not empty");
    Assertions.assertTrue(result.contains(list.get(0)), "Result must contain the same Data");
  }


  @Test
  void givenEmptyName_whenCreateOrganization_thenInvalidArgumentException() {
    defaultOrganization.setName("");

    Assertions.assertThrows(ConstraintViolationException.class, () ->
            personalManagementService.createOrganization(defaultOrganization)
        , "ConstraintViolationException error was expected");
  }

  @Test
  void givenEmptyAddress_whenCreateOrganization_thenInvalidArgumentException() {
    defaultOrganization.setHeadquartersAddress("");

    Assertions.assertThrows(ConstraintViolationException.class, () ->
            personalManagementService.createOrganization(defaultOrganization),
        "ConstraintViolationException error was expected");
  }


  @Test
  void givenEmptyOrganizationTypeName_whenCreateOrganization_thenConstraintViolationException() {
    defaultOrganization.setName("");
    Assertions.assertThrows(ConstraintViolationException.class, () ->
            personalManagementService.createOrganization(defaultOrganization),
        "ConstraintViolationException error was expected");
  }


  @Test
  void givenValidData_whenCreateOrganization_thenReturnOrganizationWithId() {

    final Organization result = personalManagementService.createOrganization(defaultOrganization.getCode(),
        defaultOrganization.getName(),
        defaultOrganization.getHeadquartersAddress(),
        defaultOrganization.getLocation(),
        defaultOrganization.getOrganizationType().getName());

    Assertions.assertNotNull(result.getId(), "Id must be not Null");


  }

  @Test
  void givenValidName_whenFindByNameOrCode_thenFoundOrganization() {
    final List<Organization> organizationList = new ArrayList<>();
    organizationList.add(defaultOrganization);

    when(organizationRepository.findByNameIgnoreCaseContainsOrCodeIgnoreCaseContains(anyString(),
        anyString())).thenReturn(organizationList);

    Assertions.assertEquals(organizationList,
        personalManagementService.findOrganizationByNameOrCode(defaultOrganization.getName()));

  }

  @Test
  void givenValidCode_whenFindByNameOrCode_thenFoundOrganization() {
    final List<Organization> organizationList = new ArrayList<>();
    organizationList.add(defaultOrganization);

    when(organizationRepository.findByNameIgnoreCaseContainsOrCodeIgnoreCaseContains(anyString(),
        anyString())).thenReturn(organizationList);

    Assertions.assertEquals(organizationList, personalManagementService.findOrganizationByNameOrCode(""));

  }

  @Test
  void givenValidNameAndValidCode_whenFindByNameOrCode_thenFoundOrganization() {
    final List<Organization> organizationList = new ArrayList<>();
    organizationList.add(defaultOrganization);

    when(organizationRepository.findByNameIgnoreCaseContainsOrCodeIgnoreCaseContains(anyString(),
        anyString())).thenReturn(organizationList);

    Assertions.assertEquals(organizationList,
        personalManagementService.findOrganizationByNameOrCode(defaultOrganization.getName()));
  }

  @Test
  void givenInvalidName_whenFindByNameOrCode_thenFoundOrganization() {

    Assertions.assertTrue(personalManagementService.findOrganizationByNameOrCode(null).isEmpty(),
        "Found item must be null");
  }


  @Test
  void givenValidName_whenFindByNameOrCode_thenFoundMultipleOrganizations() {
    List<String> names = Arrays.asList("Centro 1", "Centro 2", "Centro 3");
    List<Organization> list = OrganizationOM.withNames(names);

    when(organizationRepository.findByNameIgnoreCaseContainsOrCodeIgnoreCaseContains(anyString(),
        anyString())).thenReturn(list);

    Assertions.assertEquals(3, personalManagementService.findOrganizationByNameOrCode("Centro").size(),
        "Expected results must be 3");
  }


  @Test
  void givenValidName_whenFindByOrganizationTypeName_thenFoundMultipleOrganizations() {
    List<String> names = Arrays.asList("Centro 1", "Centro 2", "Centro 3");
    List<Organization> list = OrganizationOM.withNames(names);

    when(
        organizationRepository.findByOrganizationType_NameIgnoreCaseContainsOrderByCodeAsc(anyString()))
        .thenReturn(list);

    Assertions.assertEquals(3, personalManagementService.findOrganizationByOrganizationTypeName("Dummy").size(),
        "Expected results must be 3");
  }

  @Test
  void givenValidId_whenDelete_thenDeletedSuccessfully() {
    Organization organization = OrganizationOM.withDefaultValues();

    personalManagementService.deleteOrganizationById(organization.getId());

    Assertions.assertTrue(personalManagementService.findAllOrganizations().isEmpty(), "Expected result must be Empty");
  }

  @Test
  void givenValidData_whenUpdate_thenUpdatedSuccessfully() throws InstanceNotFoundException {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    when(organizationTypeRepository.save(any())).thenReturn(organizationType);
    personalManagementService.createOrganizationType(organizationType.getName());

    when(organizationTypeRepository.findByName(anyString())).thenReturn(organizationType);
    Organization createdOrganization = personalManagementService.createOrganization(defaultOrganization);

    Organization updatedOrganization = personalManagementService.updateOrganization(createdOrganization.getId(),
        "New Name",
        "New Code",
        "New HeadQuarters Address",
        defaultOrganization.getLocation());

    Assertions.assertEquals(createdOrganization, updatedOrganization);

  }

  @Test
  void givenInvadalidData_whenUpdate_thenConstraintViolationException() {
    final OrganizationType organizationType = OrganizationTypeOM.withDefaultValues();
    when(organizationTypeRepository.save(any())).thenReturn(organizationType);
    personalManagementService.createOrganizationType(organizationType.getName());

    when(organizationTypeRepository.findByName(anyString())).thenReturn(organizationType);
    Organization createdOrganization = personalManagementService.createOrganization(defaultOrganization);

    Long id = createdOrganization.getId();
    Point location = createdOrganization.getLocation();
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
    when(organizationTypeRepository.save(any())).thenReturn(organizationType);
    personalManagementService.createOrganizationType(organizationType.getName());

    when(organizationTypeRepository.findByName(anyString())).thenReturn(organizationType);
    Organization createdOrganization = personalManagementService.createOrganization(defaultOrganization);

    Point location = createdOrganization.getLocation();

    when(organizationRepository.findById(-1L)).thenReturn(Optional.empty());
    Assertions.assertThrows(InstanceNotFoundException.class, () -> personalManagementService.updateOrganization(-1L,
            "",
            "",
            "",
            location)
        , "InstanceNotFoundException error was expected");

  }

}