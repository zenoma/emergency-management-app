package es.udc.emergencyproject.backend.rest.dtos.mappers;

import es.udc.emergencyproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.emergencyproject.backend.rest.dtos.VehicleQuadrantLogDto;
import es.udc.emergencyproject.backend.rest.dtos.VehicleResponseDto;

public class VehicleQuadrantLogMapper {

  private VehicleQuadrantLogMapper() {
  }

  public static VehicleQuadrantLogDto toVehicleQuadrantDto(VehicleQuadrantLog vehicleQuadrantLog) {

    VehicleResponseDto vehicleResponseDto = VehicleMapper.toVehicleDtoWithoutQuadrantInfo(
        vehicleQuadrantLog.getVehicle());
    QuadrantInfoDto quadrantInfoDto = QuadrantInfoMapper.toQuadrantDtoWithoutTeamsAndVehicles(
        vehicleQuadrantLog.getQuadrant());

    return new VehicleQuadrantLogDto(vehicleResponseDto, quadrantInfoDto, vehicleQuadrantLog.getDeployAt(),
        vehicleQuadrantLog.getRetractAt());

  }


}
