package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import es.udc.fireproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.rest.dtos.VehicleResponseDto;
import es.udc.fireproject.backend.rest.dtos.conversors.VehicleConversor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

  @Autowired
  PersonalManagementService personalManagementService;

  @Autowired
  FireManagementService fireManagementService;

  @PostMapping("")
  public VehicleResponseDto create(@RequestAttribute Long userId,
      @RequestBody Map<String, String> jsonParams)
      throws InstanceNotFoundException {

    Vehicle vehicle = personalManagementService.createVehicle(jsonParams.get("vehiclePlate"), jsonParams.get("type"),
        Long.valueOf(jsonParams.get("organizationId")));
    return VehicleConversor.toVehicleDto(vehicle);

  }

  @DeleteMapping("/{id}")
  public void delete(@RequestAttribute Long userId, @PathVariable Long id)
      throws InstanceNotFoundException, AlreadyDismantledException {
    personalManagementService.dismantleVehicleById(id);
  }

  @PutMapping("/{id}")
  public void update(@RequestAttribute Long userId, @PathVariable Long id,
      @RequestBody VehicleResponseDto vehicleResponseDto)
      throws InstanceNotFoundException, AlreadyDismantledException {
    personalManagementService.updateVehicle(id, vehicleResponseDto.getVehiclePlate(), vehicleResponseDto.getType());
  }

  @GetMapping("/{id}")
  public VehicleResponseDto findById(@RequestAttribute Long userId, @PathVariable Long id)
      throws InstanceNotFoundException {
    return VehicleConversor.toVehicleDto(personalManagementService.findVehicleById(id));
  }

  @GetMapping("")
  public List<VehicleResponseDto> findAll(@RequestAttribute Long userId,
      @RequestParam(required = false) Long organizationId) {

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
    return vehicleResponseDtos;
  }

  @GetMapping("/active")
  public List<VehicleResponseDto> findAllActiveByOrganizationId(@RequestAttribute Long userId,
      @RequestParam(required = false) Long organizationId) {

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
    return vehicleResponseDtos;
  }

  @PostMapping("/{id}/deploy")
  public VehicleResponseDto deploy(@RequestAttribute Long userId, @PathVariable Long id,
      @RequestBody Map<String, String> jsonParams)
      throws InstanceNotFoundException, AlreadyDismantledException {

    return VehicleConversor.toVehicleDto(
        fireManagementService.deployVehicle(id, Integer.valueOf(jsonParams.get("gid"))));
  }

  @PostMapping("/{id}/retract")
  public VehicleResponseDto retract(@RequestAttribute Long userId, @PathVariable Long id)
      throws InstanceNotFoundException, AlreadyDismantledException {

    return VehicleConversor.toVehicleDto(fireManagementService.retractVehicle(id));

  }
}

