package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.ExtinguishedEmergencyException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.services.emergencymanagement.EmergencyManagementService;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantEmergencyRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.mappers.EmergencyMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequiredArgsConstructor
public class EmergenciesController implements EmergenciesApi {

  private final EmergencyManagementService emergencyManagementService;

  @Override
  public ResponseEntity<List<EmergencyResponseDto>> getAllEmergencys() {

    List<EmergencyResponseDto> emergencyResponseDtos = new ArrayList<>();

    for (Emergency emergency : emergencyManagementService.findAllEmergencies()) {
      emergencyResponseDtos.add(EmergencyMapper.toEmergencyDto(emergency));
    }
    return ResponseEntity.ok(emergencyResponseDtos);
  }

  @Override
  public ResponseEntity<EmergencyResponseDto> postEmergency(EmergencyRequestDto emergencyRequestDto) {

    Emergency emergency = EmergencyMapper.toEmergency(emergencyRequestDto);

    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), emergency.getType(),
        emergency.getEmergencyIndex());

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(emergency.getId()).toUri();

    return ResponseEntity.created(location).body(EmergencyMapper.toEmergencyDto(emergency));

  }

  @Override
  public ResponseEntity<EmergencyResponseDto> putEmergency(Long id, EmergencyRequestDto emergencyRequestDto) {

    Emergency emergency = EmergencyMapper.toEmergency(emergencyRequestDto);

    emergency = emergencyManagementService.updateEmergency(id, emergency.getDescription(), emergency.getType(),
        emergency.getEmergencyIndex());

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(emergency.getId()).toUri();

    return ResponseEntity.created(location).body(EmergencyMapper.toEmergencyDto(emergency));

  }

  @Override
  public ResponseEntity<EmergencyResponseDto> getEmergencyById(Long id) {
    final EmergencyResponseDto emergencyResponseDto = EmergencyMapper.toEmergencyDto(
        emergencyManagementService.findEmergencyById(id));
    return ResponseEntity.ok(emergencyResponseDto);
  }

  @PostMapping("/{id}/extinguishEmergency")
  public EmergencyResponseDto extinguishEmergency(@RequestAttribute Long userId,
      @PathVariable Long id)

      throws InstanceNotFoundException, ExtinguishedEmergencyException, AlreadyDismantledException {
    return EmergencyMapper.toEmergencyDto(emergencyManagementService.extinguishEmergency(id));
  }

  @Override
  public ResponseEntity<EmergencyResponseDto> postExtinguishQuadrant(Long id,
      QuadrantEmergencyRequestDto quadrantEmergencyRequestDto)
      throws ExtinguishedEmergencyException, AlreadyDismantledException {

    final EmergencyResponseDto emergencyResponseDto = EmergencyMapper.toEmergencyDto(
        emergencyManagementService.extinguishQuadrantByEmergencyId(id, quadrantEmergencyRequestDto.getQuadrantId()));

    return ResponseEntity.ok(emergencyResponseDto);
  }

  @Override
  public ResponseEntity<EmergencyResponseDto> postExtinguishEmergency(Long id) {

    final Emergency emergency = emergencyManagementService.extinguishEmergency(id);

    final EmergencyResponseDto emergencyResponseDto = EmergencyMapper.toEmergencyDto(emergency);

    return ResponseEntity.ok(emergencyResponseDto);
  }


}
