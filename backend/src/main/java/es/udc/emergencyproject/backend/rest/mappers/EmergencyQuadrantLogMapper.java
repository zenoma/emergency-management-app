package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.logs.EmergencyQuadrantLog;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyQuadrantLogDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantDto;

public class EmergencyQuadrantLogMapper {

  private EmergencyQuadrantLogMapper() {
  }


  public static EmergencyQuadrantLogDto toEmergencyQuadrantLogDto(EmergencyQuadrantLog emergencyQuadrantLog) {

    EmergencyResponseDto emergencyResponseDto = EmergencyMapper.toEmergencyDtoWithoutQuadrants(
        emergencyQuadrantLog.getEmergency());
    QuadrantDto quadrantDto = QuadrantMapper.toQuadrantDto(
        emergencyQuadrantLog.getQuadrant());

    return new EmergencyQuadrantLogDto(quadrantDto, emergencyResponseDto, emergencyQuadrantLog.getLinkedAt(),
        emergencyQuadrantLog.getExtinguishedAt());

  }


}
