package es.udc.fireproject.backend.model.services.personalmanagement;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.team.TeamRepository;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRepository;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.AlreadyExistException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.UserWithoutTeamException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.model.services.utils.ConstraintValidator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

  private static final String TEAM_NOT_FOUND = "Team not found";
  private static final String USER_NOT_FOUND = "User not found";

  private final OrganizationService organizationService;
  private final FireManagementService fireManagementService;

  private final TeamRepository teamRepository;
  private final UserRepository userRepository;

  @Override
  public List<Team> findTeamByCode(String code) {
    return teamRepository.findTeamsByCodeContains(code);
  }

  @Override
  public Team createTeam(String code, Long organizationId) throws InstanceNotFoundException, AlreadyExistException {

    Organization organization = organizationService.findOrganizationById(organizationId);

    List<Team> teams = findTeamsByOrganizationId(organizationId);

    for (Team item : teams) {
      if (item.getCode().equals(code)) {
        throw new AlreadyExistException(Team.class.getSimpleName(), item.getCode());
      }
    }
    Team team = new Team(code, organization);
    team.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

    ConstraintValidator.validate(team);

    return teamRepository.save(team);
  }

  @Override
  @Transactional
  public void dismantleTeamById(Long id) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, id));

    if (team.getDismantleAt() == null) {
      if (findAllUsersByTeamId(id) != null) {
        List<User> userList = new ArrayList<>(findAllUsersByTeamId(id));
        for (User user : userList) {
          user.setTeam(null);
          userRepository.save(user);
        }
      }
      fireManagementService.retractTeam(team.getId());
      team.setDismantleAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

      teamRepository.save(team);
    } else {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }


  }

  @Override
  public Team updateTeam(Long id, String code) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, id));

    if (team.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }

    team.setCode(code);

    ConstraintValidator.validate(team);
    return teamRepository.save(team);


  }

  @Override
  public Team addMember(Long teamId, Long userId) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId));

    if (team.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, userId));

    user.setTeam(team);
    userRepository.save(user);

    List<User> userList = team.getUserList();
    if (userList == null) {
      userList = new ArrayList<>();
    }
    if (!userList.contains(user)) {
      userList.add(user);
      team.setUserList(userList);
    }
    return teamRepository.save(team);
  }

  @Override
  public void deleteMember(Long teamId, Long userId) throws InstanceNotFoundException, AlreadyDismantledException {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId));
    if (team.getDismantleAt() != null) {
      throw new AlreadyDismantledException(Team.class.getSimpleName(), team.getCode());
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, userId));
    if (!team.getUserList().contains(user)) {
      throw new InstanceNotFoundException(USER_NOT_FOUND, userId);
    }

    user.setTeam(null);
    userRepository.save(user);

  }

  @Override
  public List<User> findAllUsersByTeamId(Long teamId) throws InstanceNotFoundException {
    List<User> response = teamRepository.findById(teamId)
        .orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId)).getUserList();
    if (response != null) {
      response.sort(Comparator.comparing(User::getId));
    }
    return response;
  }


  @Override
  public Team findTeamById(Long teamId) throws InstanceNotFoundException {
    return teamRepository.findById(teamId).orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId));
  }

  @Override
  public Team findTeamByUserId(Long userId) throws InstanceNotFoundException {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new InstanceNotFoundException(USER_NOT_FOUND, userId));

    if (user.getTeam() == null) {
      throw new UserWithoutTeamException(TEAM_NOT_FOUND, userId);
    }

    Long teamId = user.getTeam().getId();
    return teamRepository.findById(teamId).orElseThrow(() -> new InstanceNotFoundException(TEAM_NOT_FOUND, teamId));
  }

  @Override
  public List<Team> findTeamsByOrganizationId(Long organizationId) {
    return teamRepository.findTeamsByOrganizationIdOrderByCode(organizationId);
  }

  @Override
  public List<Team> findActiveTeamsByOrganizationId(Long organizationId) {
    return teamRepository.findTeamsByOrganizationIdAndDismantleAtIsNullOrderByCode(organizationId);
  }

  @Override
  public List<Team> findAllActiveTeams() {
    return teamRepository.findTeamsByDismantleAtIsNullOrderByCode();
  }
}
