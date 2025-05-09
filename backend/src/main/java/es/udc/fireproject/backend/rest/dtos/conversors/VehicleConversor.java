package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.rest.dtos.QuadrantInfoDto;
import es.udc.fireproject.backend.rest.dtos.VehicleDto;

public class VehicleConversor {

    private VehicleConversor() {
    }

    public static Vehicle toVehicle(VehicleDto vehicleDto) {
        return new Vehicle(vehicleDto.getVehiclePlate(),
                vehicleDto.getType(),
                OrganizationConversor.toOrganization(vehicleDto.getOrganization()));

    }

    public static VehicleDto toVehicleDto(Vehicle vehicle) {
        QuadrantInfoDto quadrantInfoDto = new QuadrantInfoDto();
        if (vehicle.getQuadrant() != null) {
            quadrantInfoDto = QuadrantInfoConversor.toQuadrantDtoWithoutTeamsAndVehicles(vehicle.getQuadrant());
        }

        return new VehicleDto(vehicle.getId(),
                vehicle.getVehiclePlate(),
                vehicle.getType(),
                vehicle.getCreatedAt(),
                OrganizationConversor.toOrganizationDto(vehicle.getOrganization()),
                quadrantInfoDto, vehicle.getDeployAt(),
                vehicle.getDismantleAt());

    }

    public static VehicleDto toVehicleDtoWithoutQuadrantInfo(Vehicle vehicle) {

        return new VehicleDto(vehicle.getId(),
                vehicle.getVehiclePlate(),
                vehicle.getType(),
                vehicle.getCreatedAt(),
                OrganizationConversor.toOrganizationDto(vehicle.getOrganization()),
                vehicle.getDeployAt(),
                vehicle.getDismantleAt());

    }
}
