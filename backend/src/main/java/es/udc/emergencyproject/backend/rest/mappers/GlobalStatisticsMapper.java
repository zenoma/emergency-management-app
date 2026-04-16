package es.udc.emergencyproject.backend.rest.mappers;

import es.udc.emergencyproject.backend.model.entities.logs.GlobalStatistics;
import es.udc.emergencyproject.backend.rest.dtos.GlobalStatisticsDto;
import java.math.BigDecimal;

public class GlobalStatisticsMapper {

  private GlobalStatisticsMapper() {

  }

  public static GlobalStatisticsDto toGlobalStatisticsDto(GlobalStatistics globalStatistics) {

    return new GlobalStatisticsDto(globalStatistics.getTeamsMobilized(),
        globalStatistics.getVehiclesMobilized(),
        BigDecimal.valueOf(globalStatistics.getMaxBurnedHectares()),
        globalStatistics.getAffectedQuadrants());
  }


}
