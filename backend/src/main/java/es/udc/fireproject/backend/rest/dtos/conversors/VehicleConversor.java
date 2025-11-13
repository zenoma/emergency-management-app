package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.VehicleResponseDto;

public class VehicleConversor {

  private VehicleConversor() {
  }

  public static Vehicle toVehicle(VehicleResponseDto vehicleResponseDto) {
    return new Vehicle(vehicleResponseDto.getVehiclePlate(),
        vehicleResponseDto.getType(),
        OrganizationConversor.toOrganization(vehicleResponseDto.getOrganization()));

  }

  public static VehicleResponseDto toVehicleDto(Vehicle vehicle) {
    QuadrantInfoDto quadrantInfoDto = new QuadrantInfoDto();
    if (vehicle.getQuadrant() != null) {
      quadrantInfoDto = QuadrantInfoConversor.toQuadrantDtoWithoutTeamsAndVehicles(vehicle.getQuadrant());
    }

    VehicleResponseDto vehicleResponseDto = new VehicleResponseDto(vehicle.getId(),
        vehicle.getVehiclePlate(),
        vehicle.getType(),
        vehicle.getCreatedAt(),
        OrganizationConversor.toOrganizationDto(vehicle.getOrganization()),
        vehicle.getDeployAt(),
        vehicle.getDismantleAt());

    vehicleResponseDto.setQuadrantInfo(quadrantInfoDto);

    return vehicleResponseDto;

  }

  public static VehicleResponseDto toVehicleDtoWithoutQuadrantInfo(Vehicle vehicle) {

    return new VehicleResponseDto(vehicle.getId(),
        vehicle.getVehiclePlate(),
        vehicle.getType(),
        vehicle.getCreatedAt(),
        OrganizationConversor.toOrganizationDto(vehicle.getOrganization()),
        vehicle.getDeployAt(),
        vehicle.getDismantleAt());

  }
}
