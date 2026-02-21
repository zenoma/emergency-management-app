package es.udc.fireproject.backend.rest.dtos.mappers;


import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.TeamResponseDto;
import es.udc.fireproject.backend.rest.dtos.UserDto;
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
    QuadrantInfoDto quadrantInfoDto = new QuadrantInfoDto();
    if (team.getQuadrant() != null) {
      quadrantInfoDto = QuadrantInfoMapper.toQuadrantDtoWithoutTeamsAndVehicles(team.getQuadrant());
    }
    TeamResponseDto teamResponseDto = new TeamResponseDto(team.getId(),
        team.getCode(),
        team.getCreatedAt(),
        OrganizationMapper.toOrganizationResponseDto(team.getOrganization()),
        team.getDeployAt(), team.getDismantleAt());

    teamResponseDto.setUserList(userDtoList);
    teamResponseDto.quadrantInfo(quadrantInfoDto);

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
