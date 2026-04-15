package es.udc.emergencyproject.backend.model.services.logs;

import es.udc.emergencyproject.backend.model.entities.assignment.Assignment;
import es.udc.emergencyproject.backend.model.entities.logs.GeneralLog;
import es.udc.emergencyproject.backend.model.entities.logs.GeneralLogEventType;
import es.udc.emergencyproject.backend.rest.dtos.GlobalLogDto;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import java.time.LocalDate;
import java.util.List;

public interface LogManagementService {


  List<GlobalLogDto> findAllEmergenciesLogByEmergencyIdAndDate(Long emergencyId, LocalDate startDate,
      LocalDate endDate);

  GlobalStatisticsDto getGlobalStatistics(Long emergencyId);


  void logGeneral(GeneralLog gl);


  void registerAssignmentEvent(Assignment assignment,
      GeneralLogEventType eventType, String details);

}
