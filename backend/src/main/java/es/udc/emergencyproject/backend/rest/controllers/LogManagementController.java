package es.udc.emergencyproject.backend.rest.controllers;

import es.udc.emergencyproject.backend.model.services.emergency.EmergencyManagementService;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import es.udc.emergencyproject.backend.rest.dtos.GlobalLogDto;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import java.time.LocalDate;
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
  public ResponseEntity<List<GlobalLogDto>> findAllEmergenciesLogByEmergencyIdAndDate(Long id, LocalDate startDate,
      LocalDate endDate) {
    var logs = logManagementService.findAllEmergenciesLogByEmergencyIdAndDate(id, startDate, endDate);
    return ResponseEntity.ok(logs);
  }

  

  @Override
  public ResponseEntity<GlobalStatisticsDto> getGlobalStatistics(Long emergencyId) {
    var stats = logManagementService.getGlobalStatistics(emergencyId);
    return ResponseEntity.ok(stats);
  }
}
