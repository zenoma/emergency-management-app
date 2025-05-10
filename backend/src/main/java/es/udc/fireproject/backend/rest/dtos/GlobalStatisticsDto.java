package es.udc.fireproject.backend.rest.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class GlobalStatisticsDto {

  private int teamsMobilized;
  private int vehiclesMobilized;
  private double maxBurnedHectares;
  private int affectedQuadrants;

  public GlobalStatisticsDto(int teamsMobilized, int vehiclesMobilized, double maxBurnedHectares,
      int affectedQuadrants) {
    this.teamsMobilized = teamsMobilized;
    this.vehiclesMobilized = vehiclesMobilized;
    this.maxBurnedHectares = maxBurnedHectares;
    this.affectedQuadrants = affectedQuadrants;
  }

}
