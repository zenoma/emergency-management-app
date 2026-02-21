package es.udc.fireproject.backend.rest.dtos.mappers;

import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.VehicleResponseDto;

public class VehicleMapper {

  private VehicleMapper() {
  }

  public static Vehicle toVehicle(VehicleResponseDto vehicleResponseDto) {
    return new Vehicle(vehicleResponseDto.getVehiclePlate(),
        vehicleResponseDto.getType(),
        OrganizationMapper.toOrganization(vehicleResponseDto.getOrganization()));

  }

  public static VehicleResponseDto toVehicleDto(Vehicle vehicle) {
    QuadrantInfoDto quadrantInfoDto = new QuadrantInfoDto();
    if (vehicle.getQuadrant() != null) {
      quadrantInfoDto = QuadrantInfoMapper.toQuadrantDtoWithoutTeamsAndVehicles(vehicle.getQuadrant());
    }

    VehicleResponseDto vehicleResponseDto = new VehicleResponseDto(vehicle.getId(),
        vehicle.getVehiclePlate(),
        vehicle.getType(),
        vehicle.getCreatedAt(),
        OrganizationMapper.toOrganizationResponseDto(vehicle.getOrganization()),
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
        OrganizationMapper.toOrganizationResponseDto(vehicle.getOrganization()),
        vehicle.getDeployAt(),
        vehicle.getDismantleAt());

  }
}
