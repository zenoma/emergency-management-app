package es.udc.emergencyproject.backend.integration.services;

import es.udc.emergencyproject.backend.IntegrationTest;
import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.organization.OrganizationType;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.user.User;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyExistException;
import es.udc.emergencyproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.personal.PersonalManagementFacade;
import es.udc.emergencyproject.backend.model.services.resources.ResourceManagementFacade;
import es.udc.emergencyproject.backend.utils.OrganizationOM;
import es.udc.emergencyproject.backend.utils.OrganizationTypeOM;
import es.udc.emergencyproject.backend.utils.TeamOM;
import es.udc.emergencyproject.backend.utils.UserOM;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class TeamServiceImplTest extends IntegrationTest {

  private static final Long INVALID_TEAM_ID = -1L;
  private static final Long INVALID_USER_ID = -1L;

  private final PersonalManagementFacade personalManagementFacade;
  private final ResourceManagementFacade resourceManagementFacade;

  @Test
  void givenNoData_whenCallFindByCode_thenReturnEmptyList() {
    final List<Team> result = resourceManagementFacade.findTeamByCode("");

    Assertions.assertTrue(result.isEmpty(), "Result must be Empty");
  }

  @Test
  void givenValidData_whenCallFindByCode_thenReturnFoundTeam() throws InstanceNotFoundException, AlreadyExistException {
    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementFacade.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = resourceManagementFacade.createTeam(team.getCode(),
        organization.getId());
    List<Team> resultList = List.of(team);

    final List<Team> result = resourceManagementFacade.findTeamByCode(team.getCode());

    Assertions.assertEquals(resultList, result, "Result must contain the same elements");
  }


  @Test
  void givenInvalidData_whenCallCreate_thenReturnConstraintViolationException() {
    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementFacade.createOrganization(organization);

    Long id = organization.getId();
    Assertions.assertThrows(ConstraintViolationException.class, () ->
            resourceManagementFacade.createTeam("", id)
        , "ConstraintViolationException error was expected");
  }

  @Test
  void givenInvalidOrganizationId_whenCallCreate_thenReturnInstanceNotFoundException() {

    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            resourceManagementFacade.createTeam("", 1L)
        , "InstanceNotFoundException error was expected");
  }


  @Test
  void givenValidId_whenDismantle_thenDismantleSuccessfully()
      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementFacade.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = resourceManagementFacade.createTeam(team.getCode(),
        organization.getId());
    resourceManagementFacade.dismantleTeamById(team.getId());

    Assertions.assertNotNull(resourceManagementFacade.findTeamById(team.getId()).getDismantleAt(),
        "Expected result must be not empty");
  }


  @Test
  void givenInvalidCode_whenUpdate_thenConstraintViolationException()
      throws InstanceNotFoundException, AlreadyExistException {
    Team team = TeamOM.withDefaultValues();
    Organization organization = team.getOrganization();
    OrganizationType organizationType = organization.getOrganizationType();
    personalManagementFacade.createOrganizationType(organizationType.getName());
    organization = personalManagementFacade.createOrganization(organization);

    team = resourceManagementFacade.createTeam(team.getCode(), organization.getId());
    team.setCode("");

    Long id = team.getId();
    String code = team.getCode();
    Assertions.assertThrows(ConstraintViolationException.class, () -> resourceManagementFacade.updateTeam(id, code),
        "ConstraintViolationException error was expected");
  }

  @Test
  void givenInvalidId_whenUpdate_thenInstanceNotFoundException() {

    Assertions.assertThrows(InstanceNotFoundException.class, () -> resourceManagementFacade.updateTeam(-1L, ""),
        "InstanceNotFoundException error was expected");
  }

  @Test
  void givenValidCode_whenUpdate_thenUpdateSuccessfully()
      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementFacade.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = resourceManagementFacade.createTeam(team.getCode(),
        organization.getId());
    team.setCode("New Name");

    Team updatedTeam = resourceManagementFacade.updateTeam(team.getId(), team.getCode());
    Assertions.assertEquals(team, updatedTeam);
  }

  @Test
  void givenValidUser_whenAddMember_thenMemberAddedSuccessfully()
      throws InstanceNotFoundException, DuplicateInstanceException, AlreadyDismantledException, AlreadyExistException {

    OrganizationType organizationTypeOm = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationTypeOm.getName());

    Organization organizationOm = OrganizationOM.withDefaultValues();
    Organization organization = personalManagementFacade.createOrganization(organizationOm);

    Team teamOm = TeamOM.withDefaultValues();
    Team team = resourceManagementFacade.createTeam(teamOm.getCode(), organization.getId());

    User userOm = UserOM.withDefaultValues();
    User user = personalManagementFacade.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    user.setTeam(team);

    resourceManagementFacade.addMemberToTeam(team.getId(), user.getId());

    Assertions.assertTrue(resourceManagementFacade.findAllUsersByTeamId(team.getId()).contains(user),
        "User must belong to the Team");
  }

  @Test
  void givenInvalidUser_whenAddMember_thenConstraintViolationException()
      throws InstanceNotFoundException, DuplicateInstanceException, AlreadyExistException {

    OrganizationType organizationTypeOm = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationTypeOm.getName());

    Organization organizationOm = OrganizationOM.withDefaultValues();
    Organization organization = personalManagementFacade.createOrganization(organizationOm);

    Team teamOm = TeamOM.withDefaultValues();
    Team team = resourceManagementFacade.createTeam(teamOm.getCode(), organization.getId());

    User userOm = UserOM.withDefaultValues();
    User user = personalManagementFacade.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    user.setTeam(team);

    final Team finalTeam = user.getTeam();
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            resourceManagementFacade.addMemberToTeam(finalTeam.getId(), INVALID_USER_ID),
        "InstanceNotFoundException error was expected");

    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            resourceManagementFacade.addMemberToTeam(INVALID_TEAM_ID, userOm.getId()),
        "InstanceNotFoundException error was expected");

  }


  @Test
  void givenValidUser_whenDeleteMember_thenMemberDeletedSuccessfully()
      throws InstanceNotFoundException, DuplicateInstanceException, AlreadyDismantledException, AlreadyExistException {

    OrganizationType organizationTypeOm = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationTypeOm.getName());

    Organization organizationOm = OrganizationOM.withDefaultValues();
    Organization organization = personalManagementFacade.createOrganization(organizationOm);

    Team teamOm = TeamOM.withDefaultValues();
    Team team = resourceManagementFacade.createTeam(teamOm.getCode(), organization.getId());

    User userOm = UserOM.withDefaultValues();
    User user = personalManagementFacade.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    resourceManagementFacade.addMemberToTeam(team.getId(), user.getId());

    Assertions.assertTrue(resourceManagementFacade.findAllUsersByTeamId(team.getId()).contains(user),
        "User must belong to the Team");

    resourceManagementFacade.deleteMemberFromTeam(team.getId(), user.getId());

    Assertions.assertNull(user.getTeam(), "User must not belong to the Team");
  }

  @Test
  void givenInvalidUser_whenDeleteMember_thenConstraintViolationException()
      throws InstanceNotFoundException, DuplicateInstanceException, AlreadyDismantledException, AlreadyExistException {

    OrganizationType organizationTypeOm = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationTypeOm.getName());

    Organization organizationOm = OrganizationOM.withDefaultValues();
    Organization organization = personalManagementFacade.createOrganization(organizationOm);

    Team teamOm = TeamOM.withDefaultValues();
    Team team = resourceManagementFacade.createTeam(teamOm.getCode(), organization.getId());

    User userOm = UserOM.withDefaultValues();
    User user = personalManagementFacade.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    user.setTeam(team);
    resourceManagementFacade.addMemberToTeam(user.getTeam().getId(), user.getId());

    Assertions.assertTrue(resourceManagementFacade.findAllUsersByTeamId(user.getTeam().getId()).contains(user),
        "User must belong to the Team");

    final Team finalTeam = user.getTeam();
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            resourceManagementFacade.deleteMemberFromTeam(finalTeam.getId(), INVALID_USER_ID),
        "InstanceNotFoundException error was expected");
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            resourceManagementFacade.deleteMemberFromTeam(INVALID_TEAM_ID, user.getId()),
        "InstanceNotFoundException error was expected");

  }

  @Test
  void givenValidUsers_whenFindAllUsers_thenNumberFoundCorrect()
      throws InstanceNotFoundException, DuplicateInstanceException, AlreadyDismantledException, AlreadyExistException {

    OrganizationType organizationTypeOm = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationTypeOm.getName());

    Organization organizationOm = OrganizationOM.withDefaultValues();
    Organization organization = personalManagementFacade.createOrganization(organizationOm);

    Team teamOm = TeamOM.withDefaultValues();
    Team team = resourceManagementFacade.createTeam(teamOm.getCode(), organization.getId());

    int itemNumber = 3;
    List<User> userList = UserOM.withRandomNames(itemNumber);
    for (User userOm : userList) {
      User user = personalManagementFacade.signUp(userOm.getEmail(),
          userOm.getPassword(),
          userOm.getFirstName(),
          userOm.getLastName(),
          String.valueOf(userOm.getPhoneNumber()),
          userOm.getDni());

      user.setTeam(team);
      resourceManagementFacade.addMemberToTeam(team.getId(), user.getId());
    }

    Assertions.assertEquals(resourceManagementFacade.findAllUsersByTeamId(team.getId()).size(), itemNumber,
        "Size must be equal to added Members");
  }

  @Test
  void givenTeamInvalidID_whenFindAllUsers_thenConstraintViolationException()
      throws InstanceNotFoundException, DuplicateInstanceException, AlreadyDismantledException, AlreadyExistException {
    OrganizationType organizationTypeOm = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationTypeOm.getName());

    Organization organizationOm = OrganizationOM.withDefaultValues();
    Organization organization = personalManagementFacade.createOrganization(organizationOm);

    Team teamOm = TeamOM.withDefaultValues();
    Team team = resourceManagementFacade.createTeam(teamOm.getCode(), organization.getId());

    int itemNumber = 3;
    List<User> userList = UserOM.withRandomNames(itemNumber);
    for (User userOm : userList) {
      User user = personalManagementFacade.signUp(userOm.getEmail(),
          userOm.getPassword(),
          userOm.getFirstName(),
          userOm.getLastName(),
          String.valueOf(userOm.getPhoneNumber()),
          userOm.getDni());

      user.setTeam(team);
      resourceManagementFacade.addMemberToTeam(team.getId(), user.getId());

    }

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> resourceManagementFacade.findAllUsersByTeamId(INVALID_TEAM_ID),
        "InstanceNotFoundException error was expected");

  }


  @Test
  void givenUserId_whenFindByUserId_thenTeamFound()
      throws DuplicateInstanceException, InstanceNotFoundException, AlreadyDismantledException, AlreadyExistException {
    OrganizationType organizationTypeOm = OrganizationTypeOM.withDefaultValues();
    personalManagementFacade.createOrganizationType(organizationTypeOm.getName());

    Organization organizationOm = OrganizationOM.withDefaultValues();
    Organization organization = personalManagementFacade.createOrganization(organizationOm);

    Team teamOm = TeamOM.withDefaultValues();
    Team team = resourceManagementFacade.createTeam(teamOm.getCode(), organization.getId());

    User userOm = UserOM.withDefaultValues();
    User user = personalManagementFacade.signUp(userOm.getEmail(), userOm.getPassword(), userOm.getFirstName(),
        userOm.getLastName(),
        String.valueOf(userOm.getPhoneNumber()), userOm.getDni());

    team = resourceManagementFacade.addMemberToTeam(team.getId(), user.getId());

    Assertions.assertEquals(team, resourceManagementFacade.findTeamByUserId(user.getId()));

  }

  @Test
  void givenTeams_whenFindActiveTeamsByOrganizationId_thenActiveTeamsFound()
      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementFacade.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = resourceManagementFacade.createTeam(team.getCode(),
        organization.getId());
    resourceManagementFacade.dismantleTeamById(team.getId());

    Team team2 = TeamOM.withDefaultValues();
    team2.setCode("TEAM-02");
    team2 = resourceManagementFacade.createTeam(team2.getCode(),
        organization.getId());

    Team team3 = TeamOM.withDefaultValues();
    team3.setCode("TEAM-03");
    team3 = resourceManagementFacade.createTeam(team3.getCode(),
        organization.getId());

    ArrayList<Team> teams = new ArrayList<>();

    teams.add(team2);
    teams.add(team3);

    Assertions.assertEquals(teams, resourceManagementFacade.findActiveTeamsByOrganizationId(organization.getId()));
  }


  @Test
  void givenTeams_whenFindAllActiveTeams_thenActiveTeamsFound()
      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {
    OrganizationType organizationType = personalManagementFacade.createOrganizationType(
        OrganizationTypeOM.withDefaultValues().getName());
    Organization organization = OrganizationOM.withDefaultValues();
    organization.setOrganizationType(organizationType);
    organization = personalManagementFacade.createOrganization(organization);

    Team team = TeamOM.withDefaultValues();
    team = resourceManagementFacade.createTeam(team.getCode(),
        organization.getId());
    resourceManagementFacade.dismantleTeamById(team.getId());

    Team team2 = TeamOM.withDefaultValues();
    team2.setCode("TEAM-02");
    team2 = resourceManagementFacade.createTeam(team2.getCode(),
        organization.getId());

    Organization organization2 = OrganizationOM.withOrganizationTypeAndRandomNames("Organization 2");
    organization2.setOrganizationType(organizationType);
    organization2 = personalManagementFacade.createOrganization(organization2);

    Team team3 = TeamOM.withDefaultValues();
    team3.setCode("TEAM-03");
    team3 = resourceManagementFacade.createTeam(team3.getCode(),
        organization2.getId());
    resourceManagementFacade.dismantleTeamById(team3.getId());

    Team team4 = TeamOM.withDefaultValues();
    team4.setCode("TEAM-04");
    team4 = resourceManagementFacade.createTeam(team4.getCode(),
        organization2.getId());

    ArrayList<Team> teams = new ArrayList<>();

    teams.add(team2);
    teams.add(team4);

    Assertions.assertEquals(teams, resourceManagementFacade.findAllActiveTeams());
  }


}
