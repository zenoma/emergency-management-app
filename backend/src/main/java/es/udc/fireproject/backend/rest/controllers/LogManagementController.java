package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.logs.FireQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.TeamQuadrantLog;
import es.udc.fireproject.backend.model.entities.logs.VehicleQuadrantLog;
import es.udc.fireproject.backend.model.exceptions.ExtinguishedFireException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.services.logsmanagement.LogManagementService;
import es.udc.fireproject.backend.rest.dtos.FireQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.GlobalStatisticsDto;
import es.udc.fireproject.backend.rest.dtos.TeamQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.VehicleQuadrantLogDto;
import es.udc.fireproject.backend.rest.dtos.conversors.FireQuadrantLogConversor;
import es.udc.fireproject.backend.rest.dtos.conversors.GlobalStatisticsConversor;
import es.udc.fireproject.backend.rest.dtos.conversors.TeamQuadrantLogConversor;
import es.udc.fireproject.backend.rest.dtos.conversors.VehicleQuadrantLogConversor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogManagementController {

  @Autowired
  LogManagementService logManagementService;

  @GetMapping("/fires")
  public List<FireQuadrantLogDto> findAllFiresLogs(@RequestAttribute Long userId) {

    List<FireQuadrantLogDto> fireQuadrantLogDtos = new ArrayList<>();

    for (FireQuadrantLog fireQuadrantLog : logManagementService.findAllFireQuadrantLogs()) {
      fireQuadrantLogDtos.add(FireQuadrantLogConversor.toFireQuadrantLogDto(fireQuadrantLog));
    }

    return fireQuadrantLogDtos;
  }

  @GetMapping("/fires/{id}")

  public List<FireQuadrantLogDto> findAllFiresLogsByFireIdAndDate(@RequestAttribute Long userId,
      @PathVariable Long id,
      @RequestParam(value = "startDate", required = true)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(value = "endDate", required = true)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)
      throws InstanceNotFoundException {

    List<FireQuadrantLogDto> fireQuadrantLogDtos = new ArrayList<>();

    for (FireQuadrantLog fireQuadrantLog : logManagementService.findFiresByFireIdAndLinkedAt(id, startDate, endDate)) {
      fireQuadrantLogDtos.add(FireQuadrantLogConversor.toFireQuadrantLogDto(fireQuadrantLog));
    }

    return fireQuadrantLogDtos;
  }

  @GetMapping("/teams")
  public List<TeamQuadrantLogDto> findTeamLogsByQuadrantIdBetweenDates(@RequestAttribute Long userId,
      @RequestParam(value = "quadrantId", required = true) Integer quadrantId,
      @RequestParam(value = "startDate", required = true)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(value = "endDate", required = true)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)

      throws InstanceNotFoundException {

    List<TeamQuadrantLogDto> teamQuadrantLogDtos = new ArrayList<>();

    for (TeamQuadrantLog teamQuadrantLog : logManagementService.findTeamsByQuadrantIdAndDeployAtBetweenOrderByDeployAt(
        quadrantId, startDate, endDate)) {
      teamQuadrantLogDtos.add(TeamQuadrantLogConversor.toTeamQuadrantLogDto(teamQuadrantLog));
    }

    return teamQuadrantLogDtos;
  }

  @GetMapping("/vehicles")
  public List<VehicleQuadrantLogDto> findVehicleLogsByQuadrantIdBetweenDates(@RequestAttribute Long userId,
      @RequestParam(value = "quadrantId", required = true) Integer quadrantId,
      @RequestParam(value = "startDate", required = true)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
      @RequestParam(value = "endDate", required = true)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate)

      throws InstanceNotFoundException {

    List<VehicleQuadrantLogDto> vehicleQuadrantLogDtos = new ArrayList<>();

    for (VehicleQuadrantLog vehicleQuadrantLog : logManagementService.findVehiclesByQuadrantIdAndDeployAtBetweenOrderByDeployAt(
        quadrantId, startDate, endDate)) {
      vehicleQuadrantLogDtos.add(VehicleQuadrantLogConversor.toVehicleQuadrantDto(vehicleQuadrantLog));
    }

    return vehicleQuadrantLogDtos;
  }

  @GetMapping("/statistics")
  public GlobalStatisticsDto getGlobalStatistics(@RequestAttribute Long userId,
      @RequestParam(value = "fireId", required = true) Long fireId)
      throws InstanceNotFoundException, ExtinguishedFireException {

    return GlobalStatisticsConversor.toGlobalStatisticsDto(
        logManagementService.getGlobalStatisticsByFireId(fireId));
  }


}
