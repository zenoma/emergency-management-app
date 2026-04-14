package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantDto;
import es.udc.emergencyproject.backend.rest.dtos.TeamQuadrantLogDto;
import es.udc.emergencyproject.backend.rest.dtos.TeamResponseDto;

public class TeamQuadrantLogMapper {

  private TeamQuadrantLogMapper() {
  }

  public static TeamQuadrantLogDto toTeamQuadrantLogDto(TeamQuadrantLog teamQuadrantLog) {

    TeamResponseDto teamResponseDto = TeamMapper.toTeamDto(teamQuadrantLog.getTeam());
    QuadrantDto quadrantDto = QuadrantMapper.toQuadrantDto(
        teamQuadrantLog.getQuadrant());

    return new TeamQuadrantLogDto(teamResponseDto, quadrantDto, teamQuadrantLog.getDeployAt(),
        teamQuadrantLog.getRetractAt());

  }


}
