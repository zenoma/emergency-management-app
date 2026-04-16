package es.udc.emergencyproject.backend.model.services.logs;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.logs.AssignmentLog;
import es.udc.emergencyproject.backend.model.entities.logs.GeneralLogEventType;
import es.udc.emergencyproject.backend.rest.dtos.AssignmentLogDto;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import java.time.LocalDate;
import java.util.List;

public interface LogManagementService {


  List<AssignmentLogDto> findAllEmergenciesLogByEmergencyIdAndDate(Long emergencyId, LocalDate startDate,
      LocalDate endDate);

  GlobalStatisticsDto getGlobalStatistics(Long emergencyId);


  void logGeneral(AssignmentLog gl);


  void registerAssignmentEvent(Assignment assignment,
      GeneralLogEventType eventType, String details);

}
