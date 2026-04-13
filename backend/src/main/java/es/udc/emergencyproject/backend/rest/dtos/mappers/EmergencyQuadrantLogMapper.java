package es.udc.emergencyproject.backend.rest.dtos.mappers;

import es.udc.emergencyproject.backend.model.entities.logs.EmergencyQuadrantLog;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyQuadrantLogDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantInfoDto;

public class EmergencyQuadrantLogMapper {

  private EmergencyQuadrantLogMapper() {
  }


  public static EmergencyQuadrantLogDto toEmergencyQuadrantLogDto(EmergencyQuadrantLog emergencyQuadrantLog) {

    EmergencyResponseDto emergencyResponseDto = EmergencyMapper.toEmergencyDtoWithoutQuadrants(
        emergencyQuadrantLog.getEmergency());
    QuadrantInfoDto quadrantInfoDto = QuadrantInfoMapper.toQuadrantDtoWithoutTeamsAndVehicles(
        emergencyQuadrantLog.getQuadrant());

    return new EmergencyQuadrantLogDto(quadrantInfoDto, emergencyResponseDto, emergencyQuadrantLog.getLinkedAt(),
        emergencyQuadrantLog.getExtinguishedAt());

  }


}
