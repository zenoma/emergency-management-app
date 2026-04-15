package es.udc.emergencyproject.backend.rest.mappers;


import es.udc.emergencyproject.backend.model.entities.resource.team.Team;
import es.udc.emergencyproject.backend.model.entities.user.User;
import es.udc.emergencyproject.backend.rest.dtos.TeamResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.UserDto;
import java.util.ArrayList;
import java.util.List;

public class TeamMapper {

  private TeamMapper() {

  }

  public static Team toTeam(TeamResponseDto teamResponseDto) {
    return new Team(teamResponseDto.getCode(),
        OrganizationMapper.toOrganization(teamResponseDto.getOrganization()));

  }

  public static TeamResponseDto toTeamDto(Team team) {
    List<UserDto> userDtoList = new ArrayList<>();
    if (team.getUserList() != null && !team.getUserList().isEmpty()) {
      for (User user : team.getUserList()) {
        userDtoList.add(UserMapper.toUserDto(user));
      }
    }

    TeamResponseDto teamResponseDto = new TeamResponseDto(team.getId(),
        team.getCode(),
        team.getCreatedAt(),
        OrganizationMapper.toOrganizationResponseDto(team.getOrganization()),
        team.getDeployAt(), team.getDismantleAt());

    teamResponseDto.setUserList(userDtoList);

    // extra info
    teamResponseDto.setStatus(team.getStatus() != null ? team.getStatus().name() : null);
    teamResponseDto.setResourceType(team.getResourceType() != null ? team.getResourceType().name() : null);
    teamResponseDto.setRemoved(team.getRemoved());
    teamResponseDto.setDismantled(team.getDismantled());

    return teamResponseDto;

  }

  public static TeamResponseDto toTeamDtoWithoutQuadrantInfo(Team team) {
    List<UserDto> userDtoList = new ArrayList<>();
    if (team.getUserList() != null && !team.getUserList().isEmpty()) {
      for (User user : team.getUserList()) {
        userDtoList.add(UserMapper.toUserDto(user));
      }
    }
    return new TeamResponseDto(team.getId(),
        team.getCode(),
        team.getCreatedAt(),
        OrganizationMapper.toOrganizationResponseDto(team.getOrganization()),
        team.getDeployAt(), team.getDismantleAt());

  }
}
