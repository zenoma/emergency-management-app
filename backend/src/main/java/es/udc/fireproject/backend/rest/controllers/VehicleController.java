package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.rest.dtos.VehicleQuadrantRequestDto;
import es.udc.fireproject.backend.rest.dtos.VehicleRequestDto;
import es.udc.fireproject.backend.rest.dtos.VehicleResponseDto;
import es.udc.fireproject.backend.rest.dtos.VehicleUpdateRequestDto;
import es.udc.fireproject.backend.rest.dtos.conversors.VehicleConversor;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class VehicleController implements VehiclesApi {

  private final PersonalManagementService personalManagementService;

  private final FireManagementService fireManagementService;

  @Override
  public ResponseEntity<VehicleResponseDto> postVehicle(VehicleRequestDto vehicleRequestDto) {

    Vehicle vehicle = personalManagementService.createVehicle(
        vehicleRequestDto.getVehiclePlate(),
        vehicleRequestDto.getType(),
        vehicleRequestDto.getOrganizationId()
    );
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(vehicle.getId()).toUri();

    return ResponseEntity.created(location).body(VehicleConversor.toVehicleDto(vehicle));
  }

  @Override
  public ResponseEntity<Void> deleteVehicleById(Long id) {
    personalManagementService.dismantleVehicleById(id);

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<VehicleResponseDto> putVehicleById(Long id, VehicleUpdateRequestDto vehicleUpdateRequestDto) {

    final Vehicle vehicle = personalManagementService.updateVehicle(id, vehicleUpdateRequestDto.getVehiclePlate(),
        vehicleUpdateRequestDto.getType());

    return ResponseEntity.ok(VehicleConversor.toVehicleDto(vehicle));
  }

  @Override
  public ResponseEntity<VehicleResponseDto> getVehicleById(Long id) {
    final Vehicle vehicle = personalManagementService.findVehicleById(id);

    return ResponseEntity.ok(VehicleConversor.toVehicleDto(vehicle));
  }

  @Override
  public ResponseEntity<List<VehicleResponseDto>> getVehicles(String code, Long organizationId) {

    List<VehicleResponseDto> vehicleResponseDtos = new ArrayList<>();

    if (organizationId != null) {
      for (Vehicle vehicle : personalManagementService.findVehiclesByOrganizationId(organizationId)) {
        vehicleResponseDtos.add(VehicleConversor.toVehicleDto(vehicle));
      }
    } else {
      for (Vehicle vehicle : personalManagementService.findAllVehicles()) {
        vehicleResponseDtos.add(VehicleConversor.toVehicleDto(vehicle));
      }
    }

    return ResponseEntity.ok(vehicleResponseDtos);
  }

  @Override
  public ResponseEntity<List<VehicleResponseDto>> getActiveVehiclesByOrganizationId(Long organizationId) {

    List<VehicleResponseDto> vehicleResponseDtos = new ArrayList<>();

    if (organizationId != null) {
      for (Vehicle vehicle : personalManagementService.findActiveVehiclesByOrganizationId(organizationId)) {
        vehicleResponseDtos.add(VehicleConversor.toVehicleDto(vehicle));
      }
    } else {
      for (Vehicle vehicle : personalManagementService.findAllActiveVehicles()) {
        vehicleResponseDtos.add(VehicleConversor.toVehicleDto(vehicle));
      }
    }
    return ResponseEntity.ok(vehicleResponseDtos);
  }

  @Override
  public ResponseEntity<VehicleResponseDto> postVehicleDeployById(Long id,
      VehicleQuadrantRequestDto vehicleQuadrantRequestDto) {

    final VehicleResponseDto vehicleResponseDto = VehicleConversor.toVehicleDto(
        fireManagementService.deployVehicle(id, vehicleQuadrantRequestDto.getQuadrantId()));

    return ResponseEntity.ok(vehicleResponseDto);
  }

  @Override
  public ResponseEntity<VehicleResponseDto> postVehicleRetractById(Long id) {

    final VehicleResponseDto vehicleResponseDto = VehicleConversor.toVehicleDto(
        fireManagementService.retractVehicle(id));

    return ResponseEntity.ok(vehicleResponseDto);

  }
}

