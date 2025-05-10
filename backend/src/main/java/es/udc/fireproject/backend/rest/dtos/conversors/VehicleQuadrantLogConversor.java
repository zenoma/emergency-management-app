package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.VehicleDto;
import es.udc.fireproject.backend.rest.dtos.VehicleQuadrantLogDto;

public class VehicleQuadrantLogConversor {

  private VehicleQuadrantLogConversor() {
  }


  public static VehicleQuadrantLogDto toVehicleQuadrantDto(VehicleQuadrantLog vehicleQuadrantLog) {

    VehicleDto vehicleDto = VehicleConversor.toVehicleDtoWithoutQuadrantInfo(vehicleQuadrantLog.getVehicle());
    QuadrantInfoDto quadrantInfoDto = QuadrantInfoConversor.toQuadrantDtoWithoutTeamsAndVehicles(
        vehicleQuadrantLog.getQuadrant());

    return new VehicleQuadrantLogDto(vehicleDto, quadrantInfoDto, vehicleQuadrantLog.getDeployAt(),
        vehicleQuadrantLog.getRetractAt());

  }


}
