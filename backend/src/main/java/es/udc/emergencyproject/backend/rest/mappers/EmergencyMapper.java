package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.rest.dtos.CoordinatesDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto.EmergencyIndexEnum;
import java.util.Collections;

public class EmergencyMapper {

  private EmergencyMapper() {
  }

  public static EmergencyResponseDto toEmergencyDto(Emergency emergency) {

    EmergencyResponseDto emergencyResponseDto = new EmergencyResponseDto();
    emergencyResponseDto.setId(emergency.getId());
    emergencyResponseDto.setDescription(emergency.getDescription());
    if (emergency.getEmergencyType() != null) {
      emergencyResponseDto.setEmergencyTypeName(emergency.getEmergencyType().getName());
    }
    emergencyResponseDto.setEmergencyIndex(EmergencyIndexEnum.fromValue(emergency.getEmergencyIndex().toString()));
    emergencyResponseDto.setCreatedAt(emergency.getCreatedAt());
    emergencyResponseDto.setResolvedAt(emergency.getResolvedAt());

    emergencyResponseDto.setQuadrantInfo(
        emergency.getQuadrantGids() != null ?
            emergency.getQuadrantGids().stream().map(QuadrantMapper::toQuadrantDto).toList()
            : Collections.emptyList());

    if (emergency.getLocation() != null) {
      CoordinatesDto coords = new CoordinatesDto();
      coords.setLon(emergency.getLocation().getX());
      coords.setLat(emergency.getLocation().getY());
      emergencyResponseDto.setLocation(coords);
    }
    // emergencyTypeId and emergencyTypeName added to response

    return emergencyResponseDto;
  }

  public static EmergencyResponseDto toEmergencyDtoWithoutQuadrants(Emergency emergency) {

    return new EmergencyResponseDto(emergency.getId(),
        emergency.getDescription(),
        emergency.getEmergencyType().getName(),
        EmergencyIndexEnum.fromValue(emergency.getEmergencyIndex().toString()),
        emergency.getCreatedAt(),
        emergency.getResolvedAt());
  }

  public static Emergency toEmergency(EmergencyRequestDto emergencyRequestDto) {

    Emergency emergency = new Emergency();
    emergency.setDescription(emergencyRequestDto.getDescription());
    // construct emergency with EmergencyType reference using provided id
    if (emergencyRequestDto.getEmergencyTypeId() != null) {
      emergency.setEmergencyType(new EmergencyType(emergencyRequestDto.getEmergencyTypeId(), ""));
    }
    emergency.setEmergencyIndex(EmergencyIndex.valueOf(emergencyRequestDto.getEmergencyIndex().toString()));

    return emergency;
  }
}
