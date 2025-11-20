package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.logs.GlobalStatistics;
import es.udc.fireproject.backend.rest.dtos.GlobalStatisticsDto;
import java.math.BigDecimal;

public class GlobalStatisticsConversor {

  private GlobalStatisticsConversor() {

  }

  public static GlobalStatisticsDto toGlobalStatisticsDto(GlobalStatistics globalStatistics) {

    return new GlobalStatisticsDto(globalStatistics.getTeamsMobilized(),
        globalStatistics.getVehiclesMobilized(),
        BigDecimal.valueOf(globalStatistics.getMaxBurnedHectares()),
        globalStatistics.getAffectedQuadrants());
  }


}
