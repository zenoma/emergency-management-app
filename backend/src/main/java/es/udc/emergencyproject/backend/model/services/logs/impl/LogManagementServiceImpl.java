package es.udc.emergencyproject.backend.model.services.logs.impl;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.emergency.EmergencyRepository;
import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLog;
import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLogRepository;
import es.udc.emergencyproject.backend.model.entities.logs.GeneralLogEventType;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.emergencyproject.backend.model.services.logs.LogManagementService;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentLogDto;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import es.udc.emergencyproject.backend.rest.mappers.AssignmentLogMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
  private final EmergencyRepository emergencyRepository;


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
    var logs = assignmentLogRepository.findByEmergencyId(emergencyId);
    long teamsMobilized = logs.stream().filter(l -> l.getEventType() == GeneralLogEventType.ASSIGNMENT_COMPLETED
        && l.getResource() != null && l.getResource().getResourceType() != null
        && l.getResource().getResourceType().name().equals("TEAM")).count();
    long vehiclesMobilized = logs.stream().filter(l -> l.getEventType() == GeneralLogEventType.ASSIGNMENT_COMPLETED
        && l.getResource() != null && l.getResource().getResourceType() != null
        && l.getResource().getResourceType().name().equals("VEHICLE")).count();
    Set<Integer> quadrantIds = logs.stream()
        .map(l -> l.getQuadrant() != null ? l.getQuadrant().getId() : null)
        .filter(Objects::nonNull)
        .collect(Collectors.toCollection(HashSet::new));

    if (quadrantIds.isEmpty()) {
      Emergency e = emergencyRepository.findById(emergencyId).orElse(null);
      if (e != null) {
        if (e.getQuadrantGids() != null && !e.getQuadrantGids().isEmpty()) {
          e.getQuadrantGids().stream().map(q -> q.getId()).forEach(quadrantIds::add);
        } else if (e.getLocation() != null) {
          var opt = quadrantRepository.findByContainingPoint(e.getLocation());
          opt.ifPresent(quadrant -> quadrantIds.add(quadrant.getId()));
        }
      }
    }

    long affectedQuadrants = quadrantIds.size();

    BigDecimal maxBurnedHectares = BigDecimal.ZERO;
    if (!quadrantIds.isEmpty()) {
      try {
        Double hectares = quadrantRepository.findHectaresByQuadrantIds(new ArrayList<>(quadrantIds));
        if (hectares != null) {
          maxBurnedHectares = BigDecimal.valueOf(hectares).setScale(3, RoundingMode.HALF_UP);
        }
      } catch (Exception ex) {
        System.err.println("Failed to compute hectares for quadrants: " + ex.getMessage());
      }
    }

    Emergency e = emergencyRepository.findById(emergencyId).orElse(null);
    if (e != null && e.getLocation() != null) {
      maxBurnedHectares = BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
    }

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
  public void registerAssignmentEvent(Assignment assignment,
      GeneralLogEventType eventType, String details) {
    var a = assignment;
    var gl = new AssignmentLog(a, a.getEmergency(),
        a.getEmergencyQuadrant() != null ? a.getEmergencyQuadrant().getQuadrant() : null, a.getResource(), eventType,
        LocalDateTime.now(), details);
    assignmentLogRepository.save(gl);
  }
}
