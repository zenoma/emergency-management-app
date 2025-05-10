package es.udc.fireproject.backend.rest.dtos.conversors;


import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.TeamDto;
import es.udc.fireproject.backend.rest.dtos.UserDto;
import java.util.ArrayList;
import java.util.List;

public class TeamConversor {

  private TeamConversor() {

  }

  public static Team toTeam(TeamDto teamDto) {
    return new Team(teamDto.getCode(), OrganizationConversor.toOrganization(teamDto.getOrganizationDto()));

  }

  public static TeamDto toTeamDto(Team team) {
    List<UserDto> userDtoList = new ArrayList<>();
    if (team.getUserList() != null && !team.getUserList().isEmpty()) {
      for (User user : team.getUserList()) {
        userDtoList.add(UserConversor.toUserDto(user));
      }
    }
    QuadrantInfoDto quadrantInfoDto = new QuadrantInfoDto();
    if (team.getQuadrant() != null) {
      quadrantInfoDto = QuadrantInfoConversor.toQuadrantDtoWithoutTeamsAndVehicles(team.getQuadrant());
    }
    return new TeamDto(team.getId(),
        team.getCode(),
        team.getCreatedAt(),
        OrganizationConversor.toOrganizationDto(team.getOrganization()),
        userDtoList,
        quadrantInfoDto,
        team.getDeployAt(), team.getDismantleAt());

  }

  public static TeamDto toTeamDtoWithoutQuadrantInfo(Team team) {
    List<UserDto> userDtoList = new ArrayList<>();
    if (team.getUserList() != null && !team.getUserList().isEmpty()) {
      for (User user : team.getUserList()) {
        userDtoList.add(UserConversor.toUserDto(user));
      }
    }
    return new TeamDto(team.getId(),
        team.getCode(),
        team.getCreatedAt(),
        OrganizationConversor.toOrganizationDto(team.getOrganization()),
        userDtoList,
        team.getDeployAt(), team.getDismantleAt());

  }
}
