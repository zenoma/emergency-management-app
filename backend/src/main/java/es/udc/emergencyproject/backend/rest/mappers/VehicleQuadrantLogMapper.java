package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantDto;
import es.udc.emergencyproject.backend.rest.dtos.VehicleQuadrantLogDto;
import es.udc.emergencyproject.backend.rest.dtos.VehicleResponseDto;

public class VehicleQuadrantLogMapper {

  private VehicleQuadrantLogMapper() {
  }

  public static VehicleQuadrantLogDto toVehicleQuadrantDto(VehicleQuadrantLog vehicleQuadrantLog) {

    VehicleResponseDto vehicleResponseDto = VehicleMapper.toVehicleDtoWithoutQuadrantInfo(
        vehicleQuadrantLog.getVehicle());
    QuadrantDto quadrantDto = QuadrantMapper.toQuadrantDto(
        vehicleQuadrantLog.getQuadrant());

    return new VehicleQuadrantLogDto(vehicleResponseDto, quadrantDto, vehicleQuadrantLog.getDeployAt(),
        vehicleQuadrantLog.getRetractAt());

  }


}
