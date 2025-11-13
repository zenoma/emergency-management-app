package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.AlreadyExistException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.rest.dtos.TeamResponseDto;
import es.udc.fireproject.backend.rest.dtos.UserDto;
import es.udc.fireproject.backend.rest.dtos.conversors.TeamConversor;
import es.udc.fireproject.backend.rest.dtos.conversors.UserConversor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/teams")
public class TeamController {

  @Autowired
  PersonalManagementService personalManagementService;

  @Autowired
  FireManagementService fireManagementService;


  @PostMapping("")
  public TeamResponseDto create(@RequestAttribute Long userId,
      @RequestBody Map<String, String> jsonParams)
      throws InstanceNotFoundException, AlreadyExistException {

    Team team = personalManagementService.createTeam(jsonParams.get("code"),
        Long.valueOf(jsonParams.get("organizationId")));
    return TeamConversor.toTeamDto(team);

  }

  @GetMapping("")
  public List<TeamResponseDto> findAll(@RequestAttribute Long userId,
      @RequestParam(required = false) String code,
      @RequestParam(required = false) Long organizationId) {

    List<TeamResponseDto> teamResponseDtoList = new ArrayList<>();

    if (code != null) {
      for (Team team : personalManagementService.findTeamByCode(code)) {
        teamResponseDtoList.add(TeamConversor.toTeamDto(team));
      }
    } else if (organizationId != null) {
      for (Team team : personalManagementService.findTeamsByOrganizationId(organizationId)) {
        teamResponseDtoList.add(TeamConversor.toTeamDto(team));
      }
    } else {
      for (Team team : personalManagementService.findTeamByCode("")) {
        teamResponseDtoList.add(TeamConversor.toTeamDto(team));
      }
    }

    return teamResponseDtoList;
  }

  @GetMapping("/active")
  public List<TeamResponseDto> findAllActiveByOrganizationId(@RequestAttribute Long userId,
      @RequestParam(required = false) Long organizationId) {

    List<TeamResponseDto> teamResponseDtoList = new ArrayList<>();

    if (organizationId != null) {
      for (Team team : personalManagementService.findActiveTeamsByOrganizationId(organizationId)) {
        teamResponseDtoList.add(TeamConversor.toTeamDto(team));
      }
    } else {
      for (Team team : personalManagementService.findAllActiveTeams()) {
        teamResponseDtoList.add(TeamConversor.toTeamDto(team));
      }
    }

    return teamResponseDtoList;
  }

  @GetMapping("/{id}")
  public TeamResponseDto findById(@RequestAttribute Long userId, @PathVariable Long id)
      throws InstanceNotFoundException {
    return TeamConversor.toTeamDto(personalManagementService.findTeamById(id));
  }

  @GetMapping("/myTeam")
  public TeamResponseDto findMyTeam(@RequestAttribute Long userId)
      throws InstanceNotFoundException {
    return TeamConversor.toTeamDto(personalManagementService.findTeamByUserId(userId));
  }

  @PostMapping("/{id}/addUser")
  public void addUser(@RequestAttribute Long userId, @PathVariable Long id, @RequestBody Map<String, String> jsonParams)
      throws InstanceNotFoundException, AlreadyDismantledException {
    personalManagementService.addMember(id, Long.valueOf(jsonParams.get("memberId")));
  }


  @DeleteMapping("/{id}")
  public void delete(@RequestAttribute Long userId, @PathVariable Long id)
      throws InstanceNotFoundException, AlreadyDismantledException {
    personalManagementService.dismantleTeamById(id);
  }

  @PostMapping("/{id}/deleteUser")
  public void deleteUser(@RequestAttribute Long userId, @PathVariable Long id,
      @RequestBody Map<String, String> jsonParams)
      throws InstanceNotFoundException, AlreadyDismantledException {
    personalManagementService.deleteMember(id, Long.valueOf(jsonParams.get("memberId")));
  }

  @GetMapping("/{id}/users")
  public List<UserDto> findAllUsers(@RequestAttribute Long userId, @PathVariable Long id)
      throws InstanceNotFoundException {
    List<UserDto> userDtoList = new ArrayList<>();
    for (User user : personalManagementService.findAllUsersByTeamId(id)) {
      userDtoList.add(UserConversor.toUserDto(user));
    }
    return userDtoList;
  }

  @PutMapping("/{id}")
  public void update(@RequestAttribute Long userId, @PathVariable Long id, @RequestBody TeamResponseDto teamResponseDto)
      throws InstanceNotFoundException, AlreadyDismantledException {
    personalManagementService.updateTeam(id, teamResponseDto.getCode());
  }


  @PostMapping("/{id}/deploy")
  public TeamResponseDto deploy(@RequestAttribute Long userId, @PathVariable Long id,
      @RequestBody Map<String, String> jsonParams)
      throws InstanceNotFoundException, AlreadyDismantledException {

    return TeamConversor.toTeamDto(fireManagementService.deployTeam(id, Integer.valueOf(jsonParams.get("gid"))));
  }

  @PostMapping("/{id}/retract")
  public TeamResponseDto retract(@RequestAttribute Long userId, @PathVariable Long id)
      throws InstanceNotFoundException, AlreadyDismantledException {

    return TeamConversor.toTeamDto(fireManagementService.retractTeam(id));

  }

}
