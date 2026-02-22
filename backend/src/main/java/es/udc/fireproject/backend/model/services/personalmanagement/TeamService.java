package es.udc.fireproject.backend.model.services.personalmanagement;

import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.AlreadyExistException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import java.util.List;


public interface TeamService {


  List<Team> findTeamByCode(String code);

  Team createTeam(String code, Long organizationId) throws InstanceNotFoundException, AlreadyExistException;

  void dismantleTeamById(Long id) throws InstanceNotFoundException, AlreadyDismantledException;

  Team updateTeam(Long id, String code) throws InstanceNotFoundException, AlreadyDismantledException;

  Team addMember(Long teamId, Long userId) throws InstanceNotFoundException, AlreadyDismantledException;

  void deleteMember(Long teamId, Long userId) throws InstanceNotFoundException, AlreadyDismantledException;

  List<User> findAllUsersByTeamId(Long teamId) throws InstanceNotFoundException;

  Team findTeamById(Long teamId) throws InstanceNotFoundException;

  Team findTeamByUserId(Long userId) throws InstanceNotFoundException;

  List<Team> findTeamsByOrganizationId(Long organizationId);

  List<Team> findActiveTeamsByOrganizationId(Long organizationId);

  List<Team> findAllActiveTeams();


}
