package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.rest.dtos.FireDto;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import java.util.ArrayList;
import java.util.List;

public class FireConversor {

  private FireConversor() {
  }

  public static FireDto toFireDto(Fire fire) {

    List<QuadrantInfoDto> cuadrantDtoList = new ArrayList<>();
    if (fire.getQuadrantGids() != null && !fire.getQuadrantGids().isEmpty()) {
      for (Quadrant quadrant : fire.getQuadrantGids()) {
        cuadrantDtoList.add(QuadrantInfoConversor.toQuadrantDto(quadrant));
      }
    }
    return new FireDto(fire.getId(), fire.getDescription(), fire.getType(), fire.getFireIndex(), cuadrantDtoList,
        fire.getCreatedAt(), fire.getExtinguishedAt());
  }

  public static FireDto toFireDtoWithoutQuadrants(Fire fire) {

    return new FireDto(fire.getId(), fire.getDescription(), fire.getType(), fire.getFireIndex(), fire.getCreatedAt(),
        fire.getExtinguishedAt());
  }

  public static Fire toFire(FireDto fireDto) {

    return new Fire(fireDto.getDescription(), fireDto.getType(), fireDto.getFireIndex());
  }
}
