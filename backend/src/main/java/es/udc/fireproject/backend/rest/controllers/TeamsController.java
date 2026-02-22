package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.exceptions.PermissionException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonaManagementFacade;
import es.udc.fireproject.backend.rest.config.JwtInfo;
import es.udc.fireproject.backend.rest.config.JwtUtils;
import es.udc.fireproject.backend.rest.dtos.TeamAddDeleteUserRequestDto;
import es.udc.fireproject.backend.rest.dtos.TeamQuadrantRequestDto;
import es.udc.fireproject.backend.rest.dtos.TeamRequestDto;
import es.udc.fireproject.backend.rest.dtos.TeamResponseDto;
import es.udc.fireproject.backend.rest.dtos.UserDto;
import es.udc.fireproject.backend.rest.dtos.mappers.TeamMapper;
import es.udc.fireproject.backend.rest.dtos.mappers.UserMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TeamsController implements TeamsApi {

  private final PersonaManagementFacade personaManagementFacade;
  private final FireManagementService fireManagementService;

  @Override
  public ResponseEntity<List<TeamResponseDto>> getTeams(String code, Long organizationId) {

    List<TeamResponseDto> teamResponseDtoList = new ArrayList<>();

    if (code != null) {
      for (Team team : personaManagementFacade.findTeamByCode(code)) {
        teamResponseDtoList.add(TeamMapper.toTeamDto(team));
      }
    } else if (organizationId != null) {
      for (Team team : personaManagementFacade.findTeamsByOrganizationId(organizationId)) {
        teamResponseDtoList.add(TeamMapper.toTeamDto(team));
      }
    } else {
      for (Team team : personaManagementFacade.findTeamByCode("")) {
        teamResponseDtoList.add(TeamMapper.toTeamDto(team));
      }
    }

    return ResponseEntity.ok(teamResponseDtoList);
  }

  @Override
  public ResponseEntity<TeamResponseDto> postTeam(TeamRequestDto teamRequestDto) {
    Team team = personaManagementFacade.createTeam(
        teamRequestDto.getCode(),
        teamRequestDto.getOrganizationId()
    );
    return ResponseEntity.ok(TeamMapper.toTeamDto(team));
  }

  @Override
  public ResponseEntity<List<TeamResponseDto>> getActiveTeamsByOrganizationId(Long organizationId) {

    List<TeamResponseDto> teamResponseDtoList = new ArrayList<>();

    if (organizationId != null) {
      for (Team team : personaManagementFacade.findActiveTeamsByOrganizationId(organizationId)) {
        teamResponseDtoList.add(TeamMapper.toTeamDto(team));
      }
    } else {
      for (Team team : personaManagementFacade.findAllActiveTeams()) {
        teamResponseDtoList.add(TeamMapper.toTeamDto(team));
      }
    }

    return ResponseEntity.ok(teamResponseDtoList);
  }

  @Override
  public ResponseEntity<List<TeamResponseDto>> getTeamByUserId() {
    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    if (jwtInfo.isEmpty()) {
      throw new PermissionException();
    }

    final Long userId = jwtInfo.get().userId();

    final Team team = personaManagementFacade.findTeamByUserId(userId);
    return ResponseEntity.ok(List.of(TeamMapper.toTeamDto(team)));
  }

  @Override
  public ResponseEntity<TeamResponseDto> getTeamById(Long id) {
    final TeamResponseDto teamResponseDto = TeamMapper.toTeamDto(personaManagementFacade.findTeamById(id));
    return ResponseEntity.ok(teamResponseDto);
  }

  @Override
  public ResponseEntity<TeamResponseDto> putTeamById(Long id, TeamRequestDto teamRequestDto) {

    final Team team = personaManagementFacade.updateTeam(id, teamRequestDto.getCode());
    return ResponseEntity.ok(TeamMapper.toTeamDto(team));
  }

  @Override
  public ResponseEntity<Void> deleteTeamById(Long id) {
    personaManagementFacade.dismantleTeamById(id);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> postAddUserToTeamById(Long id,
      TeamAddDeleteUserRequestDto teamAddDeleteUserRequestDto) {

    personaManagementFacade.addMember(id, teamAddDeleteUserRequestDto.getMemberId());

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> postDeleteUserToTeamById(Long id,
      TeamAddDeleteUserRequestDto teamAddDeleteUserRequestDto) {
    personaManagementFacade.deleteMember(id, teamAddDeleteUserRequestDto.getMemberId());

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<List<UserDto>> getUsersOfTeamById(Long id) {

    List<UserDto> userDtoList = new ArrayList<>();
    for (User user : personaManagementFacade.findAllUsersByTeamId(id)) {
      userDtoList.add(UserMapper.toUserDto(user));
    }

    return ResponseEntity.ok(userDtoList);
  }

  @Override
  public ResponseEntity<TeamResponseDto> postTeamDeployById(Long id, TeamQuadrantRequestDto teamQuadrantRequestDto) {

    Team team = fireManagementService.deployTeam(id, teamQuadrantRequestDto.getQuadrantId());
    return ResponseEntity.ok(TeamMapper.toTeamDto(team));
  }

  @Override
  public ResponseEntity<TeamResponseDto> postTeamRetractById(Long id) {

    Team team = fireManagementService.retractTeam(id);
    return ResponseEntity.ok(TeamMapper.toTeamDto(team));

  }

}
