package es.udc.fireproject.backend.rest.dtos.mappers;

import es.udc.fireproject.backend.model.entities.logs.FireQuadrantLog;
import es.udc.fireproject.backend.rest.dtos.FireQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.FireResponseDto;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;

public class FireQuadrantLogMapper {

  private FireQuadrantLogMapper() {
  }


  public static FireQuadrantLogDto toFireQuadrantLogDto(FireQuadrantLog fireQuadrantLog) {

    FireResponseDto fireResponseDto = FireMapper.toFireDtoWithoutQuadrants(fireQuadrantLog.getFire());
    QuadrantInfoDto quadrantInfoDto = QuadrantInfoMapper.toQuadrantDtoWithoutTeamsAndVehicles(
        fireQuadrantLog.getQuadrant());

    return new FireQuadrantLogDto(quadrantInfoDto, fireResponseDto, fireQuadrantLog.getLinkedAt(),
        fireQuadrantLog.getExtinguishedAt());

  }


}
