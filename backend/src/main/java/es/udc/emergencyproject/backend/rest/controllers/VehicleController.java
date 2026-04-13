package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.entities.resource.vehicle.Vehicle;
import es.udc.emergencyproject.backend.model.services.emergencymanagement.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.personalmanagement.PersonaManagementFacade;
import es.udc.emergencyproject.backend.rest.dtos.VehicleQuadrantRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.VehicleRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.VehicleResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.VehicleUpdateRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.mappers.VehicleMapper;
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

  private final PersonaManagementFacade personaManagementFacade;

  private final EmergencyManagementService emergencyManagementService;

  @Override
  public ResponseEntity<VehicleResponseDto> postVehicle(VehicleRequestDto vehicleRequestDto) {

    Vehicle vehicle = personaManagementFacade.createVehicle(
        vehicleRequestDto.getVehiclePlate(),
        vehicleRequestDto.getType(),
        vehicleRequestDto.getOrganizationId()
    );
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(vehicle.getId()).toUri();

    return ResponseEntity.created(location).body(VehicleMapper.toVehicleDto(vehicle));
  }

  @Override
  public ResponseEntity<Void> deleteVehicleById(Long id) {
    personaManagementFacade.dismantleVehicleById(id);

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<VehicleResponseDto> putVehicleById(Long id, VehicleUpdateRequestDto vehicleUpdateRequestDto) {

    final Vehicle vehicle = personaManagementFacade.updateVehicle(id, vehicleUpdateRequestDto.getVehiclePlate(),
        vehicleUpdateRequestDto.getType());

    return ResponseEntity.ok(VehicleMapper.toVehicleDto(vehicle));
  }

  @Override
  public ResponseEntity<VehicleResponseDto> getVehicleById(Long id) {
    final Vehicle vehicle = personaManagementFacade.findVehicleById(id);

    return ResponseEntity.ok(VehicleMapper.toVehicleDto(vehicle));
  }

  @Override
  public ResponseEntity<List<VehicleResponseDto>> getVehicles(String code, Long organizationId) {

    List<VehicleResponseDto> vehicleResponseDtos = new ArrayList<>();

    if (organizationId != null) {
      for (Vehicle vehicle : personaManagementFacade.findVehiclesByOrganizationId(organizationId)) {
        vehicleResponseDtos.add(VehicleMapper.toVehicleDto(vehicle));
      }
    } else {
      for (Vehicle vehicle : personaManagementFacade.findAllVehicles()) {
        vehicleResponseDtos.add(VehicleMapper.toVehicleDto(vehicle));
      }
    }

    return ResponseEntity.ok(vehicleResponseDtos);
  }

  @Override
  public ResponseEntity<List<VehicleResponseDto>> getActiveVehiclesByOrganizationId(Long organizationId) {

    List<VehicleResponseDto> vehicleResponseDtos = new ArrayList<>();

    if (organizationId != null) {
      for (Vehicle vehicle : personaManagementFacade.findActiveVehiclesByOrganizationId(organizationId)) {
        vehicleResponseDtos.add(VehicleMapper.toVehicleDto(vehicle));
      }
    } else {
      for (Vehicle vehicle : personaManagementFacade.findAllActiveVehicles()) {
        vehicleResponseDtos.add(VehicleMapper.toVehicleDto(vehicle));
      }
    }
    return ResponseEntity.ok(vehicleResponseDtos);
  }

  @Override
  public ResponseEntity<VehicleResponseDto> postVehicleDeployById(Long id,
      VehicleQuadrantRequestDto vehicleQuadrantRequestDto) {

    final VehicleResponseDto vehicleResponseDto = VehicleMapper.toVehicleDto(
        emergencyManagementService.deployVehicle(id, vehicleQuadrantRequestDto.getQuadrantId()));

    return ResponseEntity.ok(vehicleResponseDto);
  }

  @Override
  public ResponseEntity<VehicleResponseDto> postVehicleRetractById(Long id) {

    final VehicleResponseDto vehicleResponseDto = VehicleMapper.toVehicleDto(
        emergencyManagementService.retractVehicle(id));

    return ResponseEntity.ok(vehicleResponseDto);

  }
}
