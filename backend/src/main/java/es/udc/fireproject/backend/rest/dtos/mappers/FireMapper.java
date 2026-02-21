package es.udc.fireproject.backend.rest.dtos.mappers;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.fire.FireIndex;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.rest.dtos.FireRequestDto;
import es.udc.fireproject.backend.rest.dtos.FireResponseDto;
import es.udc.fireproject.backend.rest.dtos.FireResponseDto.FireIndexEnum;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import java.util.ArrayList;
import java.util.List;

public class FireMapper {

  private FireMapper() {
  }

  public static FireResponseDto toFireDto(Fire fire) {

    List<QuadrantInfoDto> cuadrantDtoList = new ArrayList<>();
    if (fire.getQuadrantGids() != null && !fire.getQuadrantGids().isEmpty()) {
      for (Quadrant quadrant : fire.getQuadrantGids()) {
        cuadrantDtoList.add(QuadrantInfoMapper.toQuadrantDto(quadrant));
      }
    }
    FireResponseDto fireResponseDto = new FireResponseDto(
        fire.getId(),
        fire.getDescription(),
        fire.getType(),
        FireIndexEnum.fromValue(fire.getFireIndex().toString()),
        fire.getCreatedAt(),
        fire.getExtinguishedAt());

    fireResponseDto.setQuadrantInfo(cuadrantDtoList);

    return fireResponseDto;
  }

  public static FireResponseDto toFireDtoWithoutQuadrants(Fire fire) {

    return new FireResponseDto(fire.getId(),
        fire.getDescription(),
        fire.getType(),
        FireIndexEnum.fromValue(fire.getFireIndex().toString()),
        fire.getCreatedAt(),
        fire.getExtinguishedAt());
  }

  public static Fire toFire(FireRequestDto fireRequestDto) {

    return new Fire(fireRequestDto.getDescription(),
        fireRequestDto.getType(),
        FireIndex.valueOf(fireRequestDto.getFireIndex().toString()));
  }
}
