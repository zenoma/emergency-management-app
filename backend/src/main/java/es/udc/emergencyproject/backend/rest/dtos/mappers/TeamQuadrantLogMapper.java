package es.udc.emergencyproject.backend.rest.dtos.mappers;

import es.udc.emergencyproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.emergencyproject.backend.rest.dtos.TeamQuadrantLogDto;
import es.udc.emergencyproject.backend.rest.dtos.TeamResponseDto;

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
