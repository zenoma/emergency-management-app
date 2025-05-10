package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.TeamDto;
import es.udc.fireproject.backend.rest.dtos.TeamQuadrantLogDto;

public class TeamQuadrantLogConversor {

  private TeamQuadrantLogConversor() {
  }


  public static TeamQuadrantLogDto toTeamQuadrantLogDto(TeamQuadrantLog teamQuadrantLog) {

    TeamDto teamDto = TeamConversor.toTeamDtoWithoutQuadrantInfo(teamQuadrantLog.getTeam());
    QuadrantInfoDto quadrantInfoDto = QuadrantInfoConversor.toQuadrantDtoWithoutTeamsAndVehicles(
        teamQuadrantLog.getQuadrant());

    return new TeamQuadrantLogDto(teamDto, quadrantInfoDto, teamQuadrantLog.getDeployAt(),
        teamQuadrantLog.getRetractAt());

  }


}
