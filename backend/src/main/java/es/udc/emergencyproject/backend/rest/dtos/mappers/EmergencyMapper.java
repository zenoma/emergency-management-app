package es.udc.emergencyproject.backend.rest.dtos.mappers;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto.EmergencyIndexEnum;
import java.util.Collections;

public class EmergencyMapper {

  private EmergencyMapper() {
  }

  public static EmergencyResponseDto toEmergencyDto(Emergency emergency) {

    EmergencyResponseDto emergencyResponseDto = new EmergencyResponseDto(
        emergency.getId(),
        emergency.getDescription(),
        emergency.getType(),
        EmergencyIndexEnum.fromValue(emergency.getEmergencyIndex().toString()),
        emergency.getCreatedAt(),
        emergency.getExtinguishedAt());

    emergencyResponseDto.setQuadrantInfo(
        emergency.getQuadrantGids() != null ?
            emergency.getQuadrantGids().stream().map(QuadrantInfoMapper::toQuadrantDto).toList()
            : Collections.emptyList());

    return emergencyResponseDto;
  }

  public static EmergencyResponseDto toEmergencyDtoWithoutQuadrants(Emergency emergency) {

    return new EmergencyResponseDto(emergency.getId(),
        emergency.getDescription(),
        emergency.getType(),
        EmergencyIndexEnum.fromValue(emergency.getEmergencyIndex().toString()),
        emergency.getCreatedAt(),
        emergency.getExtinguishedAt());
  }

  public static Emergency toEmergency(EmergencyRequestDto fireRequestDto) {

    Emergency emergency = new Emergency();
    emergency.setDescription(fireRequestDto.getDescription());
    emergency.setType(fireRequestDto.getType());
    emergency.setEmergencyIndex(EmergencyIndex.valueOf(fireRequestDto.getEmergencyIndex().toString()));

    return emergency;
  }
}
