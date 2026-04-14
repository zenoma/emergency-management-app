package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyTypeDto;
import java.util.ArrayList;
import java.util.List;

public class EmergencyTypeMapper {

  private EmergencyTypeMapper() {

  }

  public static EmergencyTypeDto toEmergencyTypeDto(EmergencyType emergencyType) {
    return new EmergencyTypeDto(
        emergencyType.getId(),
        emergencyType.getName()
    );
  }

  public static List<EmergencyTypeDto> toEmergencyTypeDtoList(List<EmergencyType> emergencyTypes) {
    List<EmergencyTypeDto> responseDtos = new ArrayList<>();
    for (EmergencyType emergencyTypeDto : emergencyTypes) {
      responseDtos.add(toEmergencyTypeDto(emergencyTypeDto));
    }
    return responseDtos;
  }
}
