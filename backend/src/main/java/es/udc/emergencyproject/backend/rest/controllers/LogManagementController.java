package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.logs.EmergencyQuadrantLog;
import es.udc.emergencyproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.emergencyproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.services.emergencymanagement.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.logsmanagement.LogManagementService;
import es.udc.emergencyproject.backend.rest.dtos.EmergencyQuadrantLogDto;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import es.udc.emergencyproject.backend.rest.dtos.TeamQuadrantLogDto;
import es.udc.emergencyproject.backend.rest.dtos.VehicleQuadrantLogDto;
import es.udc.emergencyproject.backend.rest.dtos.mappers.EmergencyQuadrantLogMapper;
import es.udc.emergencyproject.backend.rest.dtos.mappers.GlobalStatisticsMapper;
import es.udc.emergencyproject.backend.rest.dtos.mappers.TeamQuadrantLogMapper;
import es.udc.emergencyproject.backend.rest.dtos.mappers.VehicleQuadrantLogMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogManagementController implements LogsApi {

  private final LogManagementService logManagementService;
  private final EmergencyManagementService emergencyManagementService;

  @Override
  public ResponseEntity<List<EmergencyQuadrantLogDto>> findAllEmergenciesLog() {

    List<EmergencyQuadrantLogDto> emergencyQuadrantLogDtos = new ArrayList<>();

    for (EmergencyQuadrantLog emergencyQuadrantLog : logManagementService.findAllEmergencyQuadrantLogs()) {
      emergencyQuadrantLogDtos.add(EmergencyQuadrantLogMapper.toEmergencyQuadrantLogDto(emergencyQuadrantLog));
    }

    return ResponseEntity.ok(emergencyQuadrantLogDtos);
  }


  @Override
  public ResponseEntity<List<EmergencyQuadrantLogDto>> findAllEmergenciesLogByEmergencyIdAndDate(Long id,
      LocalDate startDate,
      LocalDate endDate) {

    List<EmergencyQuadrantLogDto> emergencyQuadrantLogDtos = new ArrayList<>();

    Emergency emergency = emergencyManagementService.findEmergencyById(id);

    for (EmergencyQuadrantLog emergencyQuadrantLog : logManagementService.findEmergenciesByEmergencyIdAndLinkedAt(
        emergency,
        startDate.atStartOfDay(),
        endDate.plusDays(1).atStartOfDay())) {

      emergencyQuadrantLogDtos.add(EmergencyQuadrantLogMapper.toEmergencyQuadrantLogDto(emergencyQuadrantLog));
    }

    return ResponseEntity.ok(emergencyQuadrantLogDtos);
  }

  @Override
  public ResponseEntity<List<TeamQuadrantLogDto>> findTeamLogsByQuadrantIdAndDate(Integer quadrantId,
      LocalDate startDate, LocalDate endDate) {
    List<TeamQuadrantLogDto> teamQuadrantLogDtos = new ArrayList<>();

    Quadrant quadrant = emergencyManagementService.findQuadrantById(quadrantId);

    for (TeamQuadrantLog teamQuadrantLog : logManagementService.findTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt(
        quadrant,
        startDate.atStartOfDay(),
        endDate.plusDays(1).atStartOfDay())) {
      teamQuadrantLogDtos.add(TeamQuadrantLogMapper.toTeamQuadrantLogDto(teamQuadrantLog));
    }

    return ResponseEntity.ok(teamQuadrantLogDtos);
  }

  @Override
  public ResponseEntity<List<VehicleQuadrantLogDto>> findVehicleLogsByQuadrantIdAndDate(Integer quadrantId,
      LocalDate startDate, LocalDate endDate) {
    List<VehicleQuadrantLogDto> vehicleQuadrantLogDtos = new ArrayList<>();

    Quadrant quadrant = emergencyManagementService.findQuadrantById(quadrantId);

    for (VehicleQuadrantLog vehicleQuadrantLog : logManagementService.findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(
        quadrant,
        startDate.atStartOfDay(),
        endDate.plusDays(1).atStartOfDay())) {

      vehicleQuadrantLogDtos.add(VehicleQuadrantLogMapper.toVehicleQuadrantDto(vehicleQuadrantLog));
    }

    return ResponseEntity.ok(vehicleQuadrantLogDtos);
  }

  @Override
  public ResponseEntity<GlobalStatisticsDto> getGlobalStatistics(Long emergencyId) {

    Emergency emergency = emergencyManagementService.findEmergencyById(emergencyId);

    GlobalStatisticsDto globalStatisticsDto = GlobalStatisticsMapper.toGlobalStatisticsDto(
        logManagementService.getGlobalStatisticsByEmergencyId(emergency));

    return ResponseEntity.ok(globalStatisticsDto);
  }


}
