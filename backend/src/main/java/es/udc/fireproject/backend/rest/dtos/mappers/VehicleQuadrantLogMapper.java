package es.udc.fireproject.backend.rest.dtos.mappers;

import es.udc.fireproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.VehicleQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.VehicleResponseDto;

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
