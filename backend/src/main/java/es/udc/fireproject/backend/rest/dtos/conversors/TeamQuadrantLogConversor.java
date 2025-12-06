package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.TeamQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.TeamResponseDto;

public class TeamQuadrantLogConversor {

  private TeamQuadrantLogConversor() {
  }

  public static TeamQuadrantLogDto toTeamQuadrantLogDto(TeamQuadrantLog teamQuadrantLog) {

    TeamResponseDto teamResponseDto = TeamConversor.toTeamDtoWithoutQuadrantInfo(teamQuadrantLog.getTeam());
    QuadrantInfoDto quadrantInfoDto = QuadrantInfoConversor.toQuadrantDtoWithoutTeamsAndVehicles(
        teamQuadrantLog.getQuadrant());

    return new TeamQuadrantLogDto(teamResponseDto, quadrantInfoDto, teamQuadrantLog.getDeployAt(),
        teamQuadrantLog.getRetractAt());

  }


}
