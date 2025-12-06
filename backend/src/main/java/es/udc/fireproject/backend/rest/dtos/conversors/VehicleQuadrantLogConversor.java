package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.VehicleQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.VehicleResponseDto;

public class VehicleQuadrantLogConversor {

  private VehicleQuadrantLogConversor() {
  }

  public static VehicleQuadrantLogDto toVehicleQuadrantDto(VehicleQuadrantLog vehicleQuadrantLog) {

    VehicleResponseDto vehicleResponseDto = VehicleConversor.toVehicleDtoWithoutQuadrantInfo(
        vehicleQuadrantLog.getVehicle());
    QuadrantInfoDto quadrantInfoDto = QuadrantInfoConversor.toQuadrantDtoWithoutTeamsAndVehicles(
        vehicleQuadrantLog.getQuadrant());

    return new VehicleQuadrantLogDto(vehicleResponseDto, quadrantInfoDto, vehicleQuadrantLog.getDeployAt(),
        vehicleQuadrantLog.getRetractAt());

  }


}
