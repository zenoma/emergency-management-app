package es.udc.emergencyproject.backend.rest.dtos.mappers;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyIndex;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto.EmergencyIndexEnum;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantInfoDto;
import java.util.ArrayList;
import java.util.List;

public class EmergencyMapper {

  private EmergencyMapper() {
  }

  public static EmergencyResponseDto toEmergencyDto(Emergency emergency) {

    List<QuadrantInfoDto> cuadrantDtoList = new ArrayList<>();
    EmergencyResponseDto fireResponseDto = new EmergencyResponseDto(
        emergency.getId(),
        emergency.getDescription(),
        emergency.getType(),
        EmergencyIndexEnum.fromValue(emergency.getEmergencyIndex().toString()),
        emergency.getCreatedAt(),
        emergency.getExtinguishedAt());

    fireResponseDto.setQuadrantInfo(cuadrantDtoList);

    return fireResponseDto;
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
