package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyType;
import es.udc.emergencyproject.backend.model.exceptions.AlreadyDismantledException;
import es.udc.emergencyproject.backend.model.exceptions.ResolvedEmergencyException;
import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.emergency.recommendation.EmergencyRecommendationService;
import es.udc.emergencyproject.backend.rest.dtos.CoordinatesDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyResponseDto;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyTypeDto;
import es.udc.emergencyproject.backend.rest.dtos.RecommendedAssignmentDto;
import es.udc.emergencyproject.backend.rest.dtos.LinkQuadrantsRequestDto;
import es.udc.emergencyproject.backend.rest.dtos.QuadrantEmergencyRequestDto;
import es.udc.emergencyproject.backend.rest.mappers.EmergencyMapper;
import es.udc.emergencyproject.backend.rest.mappers.EmergencyTypeMapper;
import es.udc.emergencyproject.backend.rest.mappers.RecommendationMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequiredArgsConstructor
public class EmergenciesController implements EmergenciesApi {

  private final EmergencyManagementService emergencyManagementService;
  private final EmergencyRecommendationService emergencyRecommendationService;

  @Override
  public ResponseEntity<List<EmergencyResponseDto>> getAllEmergencys() {

    List<EmergencyResponseDto> emergencyResponseDtos = new ArrayList<>();

    for (Emergency emergency : emergencyManagementService.findAllEmergencies()) {
      emergencyResponseDtos.add(EmergencyMapper.toEmergencyDto(emergency));
    }
    return ResponseEntity.ok(emergencyResponseDtos);
  }

  @Override
  public ResponseEntity<List<EmergencyTypeDto>> getAllEmergencyTypes() {
    List<EmergencyType> types =
        emergencyManagementService.findAllEmergencyTypes();

    return ResponseEntity.ok(EmergencyTypeMapper.toEmergencyTypeDtoList(types));
  }

  @Override
  public ResponseEntity<EmergencyResponseDto> postEmergency(EmergencyRequestDto emergencyRequestDto) {

    Emergency emergency = EmergencyMapper.toEmergency(emergencyRequestDto);

    Long typeId = emergency.getEmergencyType() != null ? emergency.getEmergencyType().getId() : null;
    emergency = emergencyManagementService.createEmergency(emergency.getDescription(), typeId,
        emergency.getEmergencyIndex());

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(emergency.getId()).toUri();

    return ResponseEntity.created(location).body(EmergencyMapper.toEmergencyDto(emergency));

  }

  @Override
  public ResponseEntity<EmergencyResponseDto> putEmergency(Long id, EmergencyRequestDto emergencyRequestDto) {

    Emergency emergency = EmergencyMapper.toEmergency(emergencyRequestDto);

    Long updateTypeId = emergency.getEmergencyType() != null ? emergency.getEmergencyType().getId() : null;
    emergency = emergencyManagementService.updateEmergency(id, emergency.getDescription(), updateTypeId,
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

  @Override
  public ResponseEntity<List<EmergencyResponseDto>> getActiveEmergencys() {

    List<EmergencyResponseDto> emergencyResponseDtos = new ArrayList<>();

    for (Emergency emergency : emergencyManagementService.findActiveEmergencies()) {
      emergencyResponseDtos.add(EmergencyMapper.toEmergencyDto(emergency));
    }
    return ResponseEntity.ok(emergencyResponseDtos);
  }


  @Override
  public ResponseEntity<EmergencyResponseDto> postLinkEmergencyToPoint(Long id, CoordinatesDto coordinatesDto) {

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
    Coordinate coordinate = new Coordinate(coordinatesDto.getLon(), coordinatesDto.getLat());

    EmergencyResponseDto emergencyResponseDto = EmergencyMapper.toEmergencyDto(
        emergencyManagementService.linkEmergencyToPoint(id, geometryFactory.createPoint(coordinate)));

    return ResponseEntity.ok(emergencyResponseDto);

  }

  @Override
  public ResponseEntity<EmergencyResponseDto> postLinkEmergencyToQuadrants(
      Long id, LinkQuadrantsRequestDto requestDto) {

    Emergency emergency = emergencyManagementService.linkEmergencyToQuadrants(id, requestDto.getQuadrantGids());

    return ResponseEntity.ok(EmergencyMapper.toEmergencyDto(emergency));
  }

  @Override
  public ResponseEntity<EmergencyResponseDto> postRemoveQuadrant(Long id,
      QuadrantEmergencyRequestDto quadrantEmergencyRequestDto)
      throws ResolvedEmergencyException, AlreadyDismantledException {

    final EmergencyResponseDto emergencyResponseDto = EmergencyMapper.toEmergencyDto(
        emergencyManagementService.removeQuadrantByEmergencyId(id, quadrantEmergencyRequestDto.getQuadrantId()));

    return ResponseEntity.ok(emergencyResponseDto);
  }


  @Override
  public ResponseEntity<EmergencyResponseDto> postResolveEmergency(Long id) {

    final Emergency emergency = emergencyManagementService.resolveEmergency(id);

    final EmergencyResponseDto emergencyResponseDto = EmergencyMapper.toEmergencyDto(emergency);

    return ResponseEntity.ok(emergencyResponseDto);
  }

  @Override
  public ResponseEntity<List<RecommendedAssignmentDto>> getEmergencyRecommendations(Long id) {
    return ResponseEntity.ok(RecommendationMapper.toDtoList(emergencyRecommendationService.recommendForEmergency(id)));
  }


}
