package es.udc.emergencyproject.backend.model.services.logs.impl;

import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLog;
import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLogRepository;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentLogDto;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import es.udc.emergencyproject.backend.rest.mappers.AssignmentLogMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LogManagementServiceImpl implements LogManagementService {

  private final QuadrantRepository quadrantRepository;
  private final AssignmentLogRepository assignmentLogRepository;


  @Override
  public List<AssignmentLogDto> findAllEmergenciesLogByEmergencyIdAndDate(Long emergencyId, LocalDate startDate,
      LocalDate endDate) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.plusDays(1).atStartOfDay();
    return assignmentLogRepository.findByEmergencyId(emergencyId).stream()
        .filter(gl -> !gl.getEventAt().isBefore(start) && gl.getEventAt().isBefore(end))
        .map(AssignmentLogMapper::toGlobalLogDto).collect(Collectors.toList());
  }

  @Override
  public GlobalStatisticsDto getGlobalStatistics(Long emergencyId) {
    // Simple implementation based on GeneralLogRepository data: counts mobilized teams/vehicles and affected quadrants
    var logs = assignmentLogRepository.findByEmergencyId(emergencyId);
    long teamsMobilized = logs.stream().filter(l -> l.getEventType().name().equals("RESOURCE_DEPLOYED")
        && l.getResource() != null && l.getResource().getResourceType().name().equals("TEAM")).count();
    long vehiclesMobilized = logs.stream().filter(l -> l.getEventType().name().equals("RESOURCE_DEPLOYED")
        && l.getResource() != null && l.getResource().getResourceType().name().equals("VEHICLE")).count();
    BigDecimal maxBurnedHectares = BigDecimal.ZERO; // legacy placeholder - domain specific calculation not implemented
    long affectedQuadrants = logs.stream().map(l -> l.getQuadrant() != null ? l.getQuadrant().getId() : null)
        .filter(java.util.Objects::nonNull).distinct().count();

    var stats = new GlobalStatisticsDto();
    stats.setTeamsMobilized((int) teamsMobilized);
    stats.setVehiclesMobilized((int) vehiclesMobilized);
    stats.setMaxBurnedHectares(maxBurnedHectares);
    stats.setAffectedQuadrants((int) affectedQuadrants);
    return stats;
  }

  @Override
  public void logGeneral(AssignmentLog gl) {
    assignmentLogRepository.save(gl);
  }

  @Override
  public void registerAssignmentEvent(es.udc.emergencyproject.backend.model.entities.assignment.Assignment assignment,
      es.udc.emergencyproject.backend.model.entities.logs.GeneralLogEventType eventType, String details) {
    var a = assignment;
    var gl = new AssignmentLog(a, a.getEmergency(),
        a.getEmergencyQuadrant() != null ? a.getEmergencyQuadrant().getQuadrant() : null, a.getResource(), eventType,
        LocalDateTime.now(), details);
    assignmentLogRepository.save(gl);
  }
}
