package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.logs.FireQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.services.firemanagement.FireManagementService;
import es.udc.fireproject.backend.model.services.logsmanagement.LogManagementService;
import es.udc.fireproject.backend.rest.dtos.FireQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.GlobalStatisticsDto;
import es.udc.fireproject.backend.rest.dtos.TeamQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.VehicleQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.mappers.FireQuadrantLogMapper;
import es.udc.fireproject.backend.rest.dtos.mappers.GlobalStatisticsMapper;
import es.udc.fireproject.backend.rest.dtos.mappers.TeamQuadrantLogMapper;
import es.udc.fireproject.backend.rest.dtos.mappers.VehicleQuadrantLogMapper;
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
  private final FireManagementService fireManagementService;

  @Override
  public ResponseEntity<List<FireQuadrantLogDto>> findAllFiresLog() {

    List<FireQuadrantLogDto> fireQuadrantLogDtos = new ArrayList<>();

    for (FireQuadrantLog fireQuadrantLog : logManagementService.findAllFireQuadrantLogs()) {
      fireQuadrantLogDtos.add(FireQuadrantLogMapper.toFireQuadrantLogDto(fireQuadrantLog));
    }

    return ResponseEntity.ok(fireQuadrantLogDtos);
  }


  @Override
  public ResponseEntity<List<FireQuadrantLogDto>> findAllFiresLogByFireIdAndDate(Long id, LocalDate startDate,
      LocalDate endDate) {

    List<FireQuadrantLogDto> fireQuadrantLogDtos = new ArrayList<>();

    Fire fire = fireManagementService.findFireById(id);

    for (FireQuadrantLog fireQuadrantLog : logManagementService.findFiresByFireIdAndLinkedAt(fire,
        startDate.atStartOfDay(),
        endDate.plusDays(1).atStartOfDay())) {

      fireQuadrantLogDtos.add(FireQuadrantLogMapper.toFireQuadrantLogDto(fireQuadrantLog));
    }

    return ResponseEntity.ok(fireQuadrantLogDtos);
  }

  @Override
  public ResponseEntity<List<TeamQuadrantLogDto>> findTeamLogsByQuadrantIdAndDate(Integer quadrantId,
      LocalDate startDate, LocalDate endDate) {
    List<TeamQuadrantLogDto> teamQuadrantLogDtos = new ArrayList<>();

    Quadrant quadrant = fireManagementService.findQuadrantById(quadrantId);

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

    Quadrant quadrant = fireManagementService.findQuadrantById(quadrantId);

    for (VehicleQuadrantLog vehicleQuadrantLog : logManagementService.findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(
        quadrant,
        startDate.atStartOfDay(),
        endDate.plusDays(1).atStartOfDay())) {

      vehicleQuadrantLogDtos.add(VehicleQuadrantLogMapper.toVehicleQuadrantDto(vehicleQuadrantLog));
    }

    return ResponseEntity.ok(vehicleQuadrantLogDtos);
  }

  @Override
  public ResponseEntity<GlobalStatisticsDto> getGlobalStatistics(Long fireId) {

    Fire fire = fireManagementService.findFireById(fireId);

    GlobalStatisticsDto globalStatisticsDto = GlobalStatisticsMapper.toGlobalStatisticsDto(
        logManagementService.getGlobalStatisticsByFireId(fire));

    return ResponseEntity.ok(globalStatisticsDto);
  }


}
