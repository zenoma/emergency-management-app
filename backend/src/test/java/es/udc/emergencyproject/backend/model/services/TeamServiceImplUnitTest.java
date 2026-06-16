package es.udc.emergencyproject.backend.model.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import es.udc.emergencyproject.backend.model.entities.organization.Organization;
import es.udc.emergencyproject.backend.model.entities.organization.OrganizationRepository;
import es.udc.emergencyproject.backend.model.entities.organization.OrganizationTypeRepository;
import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.resource.team.TeamRepository;
import es.udc.emergencyproject.backend.model.entities.user.User;
import es.udc.emergencyproject.backend.model.entities.user.UserRepository;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyExistException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.UserWithoutTeamException;
import es.udc.emergencyproject.backend.model.services.personal.OrganizationService;
import es.udc.emergencyproject.backend.model.services.resources.impl.TeamServiceImpl;
import es.udc.emergencyproject.backend.utils.OrganizationOM;
import es.udc.emergencyproject.backend.utils.OrganizationTypeOM;
import es.udc.emergencyproject.backend.utils.TeamOM;
import es.udc.emergencyproject.backend.utils.UserOM;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamServiceImplUnitTest {

  @Mock
  private TeamRepository teamRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private OrganizationRepository organizationRepository;
  @Mock
  private OrganizationTypeRepository organizationTypeRepository;
  @Mock
  private OrganizationService organizationService;

  @InjectMocks
  private TeamServiceImpl teamService;

  private Organization organization;
  private Team team;
  private User user;

  @BeforeEach
  void setUp() {
    organization = OrganizationOM.withDefaultValues();
    organization.setId(1L);
    organization.setOrganizationType(OrganizationTypeOM.withDefaultValues());

    team = TeamOM.withDefaultValues();
    team.setId(1L);
    team.setOrganization(organization);

    user = UserOM.withDefaultValues();
    user.setId(10L);

    lenient().when(organizationService.findOrganizationById(1L)).thenReturn(organization);
    lenient().when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
    lenient().when(teamRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    lenient().when(teamRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
    lenient().when(userRepository.findById(10L)).thenReturn(Optional.of(user));
    lenient().when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void givenNoData_whenFindByCode_thenReturnEmptyList() {
    when(teamRepository.findTeamsByCodeContains(anyString())).thenReturn(List.of());

    List<Team> result = teamService.findTeamByCode("");
    assertTrue(result.isEmpty());
  }

  @Test
  void givenValidCode_whenFindTeamByCode_thenReturnFound() {
    when(teamRepository.findTeamsByCodeContains("TEAM")).thenReturn(List.of(team));

    List<Team> result = teamService.findTeamByCode("TEAM");
    assertEquals(1, result.size());
  }

  @Test
  void givenValidData_whenCreateTeam_thenSuccess() throws InstanceNotFoundException, AlreadyExistException {
    when(teamRepository.findTeamsByOrganizationIdOrderByCode(1L)).thenReturn(List.of());

    Team created = teamService.createTeam("NEW-TEAM", 1L);
    assertNotNull(created);
  }

  @Test
  void givenExistingCode_whenCreateTeam_thenAlreadyExistException() {
    when(teamRepository.findTeamsByOrganizationIdOrderByCode(1L)).thenReturn(List.of(team));

    assertThrows(AlreadyExistException.class,
        () -> teamService.createTeam("TEAM-01", 1L));
  }

  @Test
  void givenEmptyCode_whenCreateTeam_thenConstraintViolation() {
    when(teamRepository.findTeamsByOrganizationIdOrderByCode(1L)).thenReturn(List.of());

    assertThrows(ConstraintViolationException.class,
        () -> teamService.createTeam("", 1L));
  }

  @Test
  void givenValidId_whenDismantleTeam_thenDismantled()
      throws InstanceNotFoundException, AlreadyDismantledException {
    team.setUserList(new ArrayList<>());

    teamService.dismantleTeamById(1L);
    assertNotNull(team.getDismantleAt());
  }

  @Test
  void givenAlreadyDismantled_whenDismantleAgain_thenThrows() {
    team.setDismantleAt(java.time.LocalDateTime.now());

    assertThrows(AlreadyDismantledException.class,
        () -> teamService.dismantleTeamById(1L));
  }

  @Test
  void givenInvalidId_whenDismantleTeam_thenThrows() {
    when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> teamService.dismantleTeamById(999L));
  }

  @Test
  void givenValidCode_whenUpdateTeam_thenUpdated()
      throws InstanceNotFoundException, AlreadyDismantledException {
    Team updated = teamService.updateTeam(1L, "NEW-CODE");
    assertEquals("NEW-CODE", updated.getCode());
  }

  @Test
  void givenDismantledTeam_whenUpdate_thenThrows() {
    team.setDismantleAt(java.time.LocalDateTime.now());

    assertThrows(AlreadyDismantledException.class,
        () -> teamService.updateTeam(1L, "NEW-CODE"));
  }

  @Test
  void givenInvalidId_whenUpdateTeam_thenThrows() {
    when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> teamService.updateTeam(999L, "CODE"));
  }

  @Test
  void givenEmptyCode_whenUpdateTeam_thenConstraintViolation() {
    assertThrows(ConstraintViolationException.class,
        () -> teamService.updateTeam(1L, ""));
  }

  @Test
  void givenValidUser_whenAddMember_thenAdded()
      throws InstanceNotFoundException, AlreadyDismantledException {
    team.setUserList(new ArrayList<>());

    Team result = teamService.addMember(1L, 10L);
    assertNotNull(result);
  }

  @Test
  void givenDismantledTeam_whenAddMember_thenThrows() {
    team.setDismantleAt(java.time.LocalDateTime.now());

    assertThrows(AlreadyDismantledException.class,
        () -> teamService.addMember(1L, 10L));
  }

  @Test
  void givenInvalidTeam_whenAddMember_thenThrows() {
    when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> teamService.addMember(999L, 10L));
  }

  @Test
  void givenInvalidUser_whenAddMember_thenThrows() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> teamService.addMember(1L, 999L));
  }

  @Test
  void givenValidUser_whenDeleteMember_thenDeleted()
      throws InstanceNotFoundException, AlreadyDismantledException {
    team.setUserList(new ArrayList<>(List.of(user)));

    teamService.deleteMember(1L, 10L);
    assertTrue(true);
  }

  @Test
  void givenDismantledTeam_whenDeleteMember_thenThrows() {
    team.setDismantleAt(java.time.LocalDateTime.now());

    assertThrows(AlreadyDismantledException.class,
        () -> teamService.deleteMember(1L, 10L));
  }

  @Test
  void givenUserNotInTeam_whenDeleteMember_thenThrows() {
    team.setUserList(new ArrayList<>());

    assertThrows(InstanceNotFoundException.class,
        () -> teamService.deleteMember(1L, 10L));
  }


  @Test
  void givenInvalidTeamId_whenFindAllUsersByTeamId_thenThrows() {
    when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> teamService.findAllUsersByTeamId(999L));
  }

  @Test
  void givenValidId_whenFindTeamById_thenFound() throws InstanceNotFoundException {
    Team found = teamService.findTeamById(1L);
    assertNotNull(found);
  }

  @Test
  void givenInvalidId_whenFindTeamById_thenThrows() {
    when(teamRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> teamService.findTeamById(999L));
  }

  @Test
  void givenValidUserId_whenFindTeamByUserId_thenFound()
      throws InstanceNotFoundException {
    user.setTeam(team);
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));

    Team found = teamService.findTeamByUserId(10L);
    assertNotNull(found);
  }

  @Test
  void givenUserWithoutTeam_whenFindTeamByUserId_thenThrows() {
    user.setTeam(null);
    when(userRepository.findById(10L)).thenReturn(Optional.of(user));

    assertThrows(UserWithoutTeamException.class,
        () -> teamService.findTeamByUserId(10L));
  }

  @Test
  void givenInvalidUserId_whenFindTeamByUserId_thenThrows() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThrows(InstanceNotFoundException.class,
        () -> teamService.findTeamByUserId(999L));
  }

  @Test
  void givenOrganizationId_whenFindTeamsByOrganizationId_thenReturned() {
    when(teamRepository.findTeamsByOrganizationIdOrderByCode(1L)).thenReturn(List.of(team));

    List<Team> result = teamService.findTeamsByOrganizationId(1L);
    assertEquals(1, result.size());
  }

  @Test
  void givenOrganizationId_whenFindActiveTeamsByOrganizationId_thenReturned() {
    when(teamRepository.findTeamsByOrganizationIdAndDismantleAtIsNullOrderByCode(1L))
        .thenReturn(List.of(team));

    List<Team> result = teamService.findActiveTeamsByOrganizationId(1L);
    assertEquals(1, result.size());
  }

  @Test
  void whenFindAllActiveTeams_thenReturned() {
    when(teamRepository.findTeamsByDismantleAtIsNullOrderByCode()).thenReturn(List.of(team));

    List<Team> result = teamService.findAllActiveTeams();
    assertEquals(1, result.size());
  }
}
