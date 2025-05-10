package es.udc.fireproject.backend.rest.dtos;

import java.util.Objects;

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

  public int getTeamsMobilized() {
    return teamsMobilized;
  }

  public void setTeamsMobilized(int teamsMobilized) {
    this.teamsMobilized = teamsMobilized;
  }

  public int getVehiclesMobilized() {
    return vehiclesMobilized;
  }

  public void setVehiclesMobilized(int vehiclesMobilized) {
    this.vehiclesMobilized = vehiclesMobilized;
  }

  public double getMaxBurnedHectares() {
    return maxBurnedHectares;
  }

  public void setMaxBurnedHectares(double maxBurnedHectares) {
    this.maxBurnedHectares = maxBurnedHectares;
  }

  public int getAffectedQuadrants() {
    return affectedQuadrants;
  }

  public void setAffectedQuadrants(int affectedQuadrants) {
    this.affectedQuadrants = affectedQuadrants;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GlobalStatisticsDto that = (GlobalStatisticsDto) o;
    return teamsMobilized == that.teamsMobilized && vehiclesMobilized == that.vehiclesMobilized
        && Double.compare(that.maxBurnedHectares, maxBurnedHectares) == 0
        && affectedQuadrants == that.affectedQuadrants;
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamsMobilized, vehiclesMobilized, maxBurnedHectares, affectedQuadrants);
  }

  @Override
  public String toString() {
    return "GlobalStatisticsDto{" +
        "teamsMobilized=" + teamsMobilized +
        ", vehiclesMobilized=" + vehiclesMobilized +
        ", maxBurnedHectares=" + maxBurnedHectares +
        ", affectedQuadrants=" + affectedQuadrants +
        '}';
  }
}
