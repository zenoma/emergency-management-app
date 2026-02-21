package es.udc.fireproject.backend.rest.dtos.mappers;

import es.udc.fireproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.TeamQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.TeamResponseDto;

public class TeamQuadrantLogMapper {

  private TeamQuadrantLogMapper() {
  }

  public static TeamQuadrantLogDto toTeamQuadrantLogDto(TeamQuadrantLog teamQuadrantLog) {

    TeamResponseDto teamResponseDto = TeamMapper.toTeamDtoWithoutQuadrantInfo(teamQuadrantLog.getTeam());
    QuadrantInfoDto quadrantInfoDto = QuadrantInfoMapper.toQuadrantDtoWithoutTeamsAndVehicles(
        teamQuadrantLog.getQuadrant());

    return new TeamQuadrantLogDto(teamResponseDto, quadrantInfoDto, teamQuadrantLog.getDeployAt(),
        teamQuadrantLog.getRetractAt());

  }


}
