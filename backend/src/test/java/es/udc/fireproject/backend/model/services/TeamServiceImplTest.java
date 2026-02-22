package es.udc.fireproject.backend.model.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.organization.OrganizationRepository;
import es.udc.fireproject.backend.model.entities.organization.OrganizationTypeRepository;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.team.TeamRepository;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRepository;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.AlreadyExistException;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.model.services.personalmanagement.OrganizationService;
import es.udc.fireproject.backend.model.services.personalmanagement.TeamServiceImpl;
import es.udc.fireproject.backend.utils.OrganizationOM;
import es.udc.fireproject.backend.utils.OrganizationTypeOM;
import es.udc.fireproject.backend.utils.TeamOM;
import es.udc.fireproject.backend.utils.UserOM;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

  private final Long INVALID_TEAM_ID = -1L;
  private final Long INVALID_USER_ID = -1L;

  @Mock
  TeamRepository teamRepository;
  @Mock
  UserRepository userRepository;
  @Mock
  OrganizationRepository organizationRepository;
  @Mock
  OrganizationTypeRepository organizationTypeRepository;

  @Mock
  OrganizationService organizationService;

  @Mock
  FireManagementService fireManagementService;
  @Mock
  BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  TeamServiceImpl teamService;

  @BeforeEach
  public void setUp() {

    lenient().when(organizationTypeRepository.findByName(any())).thenReturn(OrganizationTypeOM.withDefaultValues());
    lenient().when(organizationRepository.findById(any())).thenReturn(Optional.of(OrganizationOM.withDefaultValues()));
    lenient().when(organizationRepository.save(any())).thenReturn(OrganizationOM.withDefaultValues());
    lenient().when(teamRepository.findById(any())).thenReturn(Optional.of(TeamOM.withDefaultValues()));
    lenient().when(teamRepository.save(any())).thenReturn(TeamOM.withDefaultValues());
    lenient().when(userRepository.findById(any())).thenReturn(Optional.of(UserOM.withDefaultValues()));

  }

  @Test
  void givenNoData_whenCallFindByCode_thenReturnEmptyList() {
    final List<Team> result = teamService.findTeamByCode("");

    Assertions.assertTrue(result.isEmpty(), "Result must be Empty");
  }

  @Test
  void givenValidData_whenCallFindByCode_thenReturnFoundTeam() {

    List<Team> resultList = List.of(TeamOM.withDefaultValues());

    when(teamRepository.findTeamsByCodeContains(Mockito.anyString())).thenReturn(resultList);

    final List<Team> result = teamService.findTeamByCode("");

    Assertions.assertEquals(resultList, result, "Result must contain the same elements");
  }


  @Test
  void givenValidData_whenCallCreate_thenReturnCreatedTeam() throws InstanceNotFoundException, AlreadyExistException {

    Assertions.assertEquals(TeamOM.withDefaultValues(), teamService.createTeam("a", 1L),
        "Elements are not equal");
  }

  @Test
  void givenInvalidData_whenCallCreate_thenReturnConstraintViolationException() throws InstanceNotFoundException {

    Assertions.assertThrows(ConstraintViolationException.class, () ->
            teamService.createTeam("", 1L)
        , "ConstraintViolationException error was expected");
  }


  @Test
  void givenValidId_whenDismantle_thenDismantleSuccessfully()
      throws InstanceNotFoundException, AlreadyExistException, AlreadyDismantledException {

    Team team = TeamOM.withDefaultValues();
    team = teamService.createTeam(team.getCode(), team.getOrganization().getId());
    team.setId(1L);

    when(teamRepository.findById(any())).thenReturn(Optional.of(TeamOM.withDefaultValues()));

    teamService.dismantleTeamById(team.getId());

    Team finalTeam = team;

    Assertions.assertNotNull(teamService.findTeamById(team.getId()).getDismantleAt(),
        "Expected result must be not empty");


  }


  @Test
  void givenInvalidCode_whenUpdate_thenConstraintViolationException() {
    Team team = TeamOM.withDefaultValues();
    team.setCode("");

    when(teamRepository.findById(any())).thenReturn(java.util.Optional.of(TeamOM.withDefaultValues()));

    Long id = team.getId();
    String code = team.getCode();
    Assertions.assertThrows(ConstraintViolationException.class, () -> teamService.updateTeam(id, code),
        "ConstraintViolationException error was expected");
  }

  @Test
  void givenInvalidId_whenUpdate_thenInstanceNotFoundException() {
    when(teamRepository.findById(any())).thenReturn(Optional.empty());

    Assertions.assertThrows(InstanceNotFoundException.class, () -> teamService.updateTeam(-1L, ""),
        "InstanceNotFoundException error was expected");
  }

  @Test
  void givenValidCode_whenUpdate_thenUpdateSuccessfully() throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = TeamOM.withDefaultValues();
    team.setCode("New Name");

    when(teamRepository.findById(any())).thenReturn(java.util.Optional.of(TeamOM.withDefaultValues()));
    when(teamRepository.save(any())).thenReturn(team);

    Team updatedTeam = teamService.updateTeam(team.getId(), team.getCode());
    Assertions.assertEquals(team, updatedTeam);
  }

  @Test
  void givenValidUser_whenAddMember_thenMemberAddedSuccessfully() throws
      InstanceNotFoundException, DuplicateInstanceException, AlreadyExistException, AlreadyDismantledException {

    Organization organization = OrganizationOM.withDefaultValues();

    Team team = TeamOM.withDefaultValues();
    team = teamService.createTeam(team.getCode(),
        organization.getId());

    User user = UserOM.withDefaultValues();
    team = teamService.addMember(team.getId(), user.getId());
    team.setUserList(List.of(user));

    Assertions.assertTrue(team.getUserList().contains(user), "User must belong to the Team");
  }

  @Test
  void givenInvalidUser_whenAddMember_thenConstraintViolationException() throws
      InstanceNotFoundException, DuplicateInstanceException, AlreadyExistException {
    when(teamRepository.findById(any())).thenReturn(Optional.empty());

    Organization organization = OrganizationOM.withDefaultValues();

    Team team = TeamOM.withDefaultValues();
    team = teamService.createTeam(team.getCode(),
        organization.getId());

    User user = UserOM.withDefaultValues();

    final Team finalTeam = team;
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            teamService.addMember(finalTeam.getId(), INVALID_USER_ID),
        "InstanceNotFoundException error was expected");
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            teamService.addMember(INVALID_TEAM_ID, user.getId()),
        "InstanceNotFoundException error was expected");

  }


  @Test
  void givenValidUser_whenDeleteMember_thenMemberAddedSuccessfully() throws
      InstanceNotFoundException, DuplicateInstanceException, AlreadyDismantledException {

    User user = UserOM.withDefaultValues();
    Team team = TeamOM.withDefaultValues();
    user.setTeam(team);
    teamService.addMember(user.getTeam().getId(), user.getId());

    teamService.deleteMember(user.getTeam().getId(), user.getId());

    Assertions.assertNull(user.getTeam().getUserList(), "User must not belong to the Team");
  }

  @Test
  void givenInvalidUser_whenDeleteMember_thenConstraintViolationException() throws
      InstanceNotFoundException, DuplicateInstanceException, AlreadyExistException, AlreadyDismantledException {

    Organization organization = OrganizationOM.withDefaultValues();

    Team team = TeamOM.withDefaultValues();
    team = teamService.createTeam(team.getCode(),
        organization.getId());

    User user = UserOM.withDefaultValues();

    teamService.addMember(team.getId(), user.getId());

    when(teamRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());
    when(userRepository.findById(INVALID_TEAM_ID)).thenReturn(Optional.empty());

    final Team finalTeam = team;
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            teamService.deleteMember(finalTeam.getId(), INVALID_USER_ID),
        "InstanceNotFoundException error was expected");
    Assertions.assertThrows(InstanceNotFoundException.class, () ->
            teamService.deleteMember(INVALID_TEAM_ID, user.getId()),
        "InstanceNotFoundException error was expected");

  }

  @Test
  void givenValidUsers_whenFindAllUsers_thenNumberFoundCorrect() throws
      InstanceNotFoundException, DuplicateInstanceException, AlreadyExistException, AlreadyDismantledException {

    Organization organization = OrganizationOM.withDefaultValues();

    Team team = TeamOM.withDefaultValues();
    team = teamService.createTeam(team.getCode(),
        organization.getId());

    int itemNumber = 3;
    List<User> userList = UserOM.withRandomNames(itemNumber);

    Assertions.assertEquals(userList.size(), itemNumber, "Size must be equal to added Members");
  }

  @Test
  void givenTeamInvalidID_whenFindAllUsers_thenConstraintViolationException() throws
      InstanceNotFoundException, DuplicateInstanceException, AlreadyExistException, AlreadyDismantledException {

    when(teamRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> teamService.findAllUsersByTeamId(INVALID_TEAM_ID),
        "InstanceNotFoundException error was expected");

  }

}
