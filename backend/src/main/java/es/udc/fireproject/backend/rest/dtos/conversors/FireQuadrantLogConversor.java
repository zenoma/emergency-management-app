package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.logs.FireQuadrantLog;
import es.udc.fireproject.backend.rest.dtos.FireDto;
import es.udc.fireproject.backend.rest.dtos.FireQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;

public class FireQuadrantLogConversor {

  private FireQuadrantLogConversor() {
  }


  public static FireQuadrantLogDto toFireQuadrantLogDto(FireQuadrantLog fireQuadrantLog) {

    FireDto fireDto = FireConversor.toFireDtoWithoutQuadrants(fireQuadrantLog.getFire());
    QuadrantInfoDto quadrantInfoDto = QuadrantInfoConversor.toQuadrantDtoWithoutTeamsAndVehicles(
        fireQuadrantLog.getQuadrant());

    return new FireQuadrantLogDto(fireDto, quadrantInfoDto, fireQuadrantLog.getLinkedAt(),
        fireQuadrantLog.getExtinguishedAt());

  }


}
