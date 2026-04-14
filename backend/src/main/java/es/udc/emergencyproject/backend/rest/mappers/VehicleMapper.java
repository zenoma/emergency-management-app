package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.rest.dtos.VehicleResponseDto;

public class VehicleMapper {

  private VehicleMapper() {
  }

  public static Vehicle toVehicle(VehicleResponseDto vehicleResponseDto) {
    return new Vehicle(vehicleResponseDto.getVehiclePlate(),
        vehicleResponseDto.getType(),
        OrganizationMapper.toOrganization(vehicleResponseDto.getOrganization()));

  }

  public static VehicleResponseDto toVehicleDto(Vehicle vehicle) {

    return new VehicleResponseDto(vehicle.getId(),
        vehicle.getVehiclePlate(),
        vehicle.getType(),
        vehicle.getCreatedAt(),
        OrganizationMapper.toOrganizationResponseDto(vehicle.getOrganization()),
        vehicle.getDeployAt(),
        vehicle.getDismantleAt());

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
