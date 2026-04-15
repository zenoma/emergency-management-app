package es.udc.emergencyproject.backend.model.services.logs.impl;

import es.udc.emergencyproject.backend.model.entities.logs.GeneralLogRepository;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import es.udc.emergencyproject.backend.rest.dtos.GlobalLogDto;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import es.udc.emergencyproject.backend.rest.mappers.GeneralLogMapper;
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
  private final GeneralLogRepository generalLogRepository;


  @Override
  public List<GlobalLogDto> findAllEmergenciesLogByEmergencyIdAndDate(Long emergencyId, LocalDate startDate,
      LocalDate endDate) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.plusDays(1).atStartOfDay();
    return generalLogRepository.findByEmergencyId(emergencyId).stream()
        .filter(gl -> !gl.getEventAt().isBefore(start) && gl.getEventAt().isBefore(end))
        .map(GeneralLogMapper::toGlobalLogDto).collect(Collectors.toList());
  }

  @Override
  public GlobalStatisticsDto getGlobalStatistics(Long emergencyId) {
    // Simple implementation based on GeneralLogRepository data: counts mobilized teams/vehicles and affected quadrants
    var logs = generalLogRepository.findByEmergencyId(emergencyId);
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
  public void logGeneral(es.udc.emergencyproject.backend.model.entities.logs.GeneralLog gl) {
    generalLogRepository.save(gl);
  }

  @Override
  public void registerAssignmentEvent(es.udc.emergencyproject.backend.model.entities.assignment.Assignment assignment,
      es.udc.emergencyproject.backend.model.entities.logs.GeneralLogEventType eventType, String details) {
    var a = assignment;
    var gl = new es.udc.emergencyproject.backend.model.entities.logs.GeneralLog(a, a.getEmergency(),
        a.getEmergencyQuadrant() != null ? a.getEmergencyQuadrant().getQuadrant() : null, a.getResource(), eventType,
        LocalDateTime.now(), details);
    generalLogRepository.save(gl);
  }
 }
