package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Objects;

public class VehicleQuadrantLogDto extends BaseDto {

  private static final long serialVersionUID = 7866765077374416159L;

  private VehicleDto vehicleDto;

  private QuadrantInfoDto quadrantInfoDto;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime deployAt;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime retractAt;


  public VehicleQuadrantLogDto() {
  }

  public VehicleQuadrantLogDto(VehicleDto vehicleDto, QuadrantInfoDto quadrantInfoDto, LocalDateTime deployAt,
      LocalDateTime retractAt) {
    this.vehicleDto = vehicleDto;
    this.quadrantInfoDto = quadrantInfoDto;
    this.deployAt = deployAt;
    this.retractAt = retractAt;
  }

  public VehicleDto getVehicleDto() {
    return vehicleDto;
  }

  public void setVehicleDto(VehicleDto vehicleDto) {
    this.vehicleDto = vehicleDto;
  }

  public QuadrantInfoDto getQuadrantInfoDto() {
    return quadrantInfoDto;
  }

  public void setQuadrantInfoDto(QuadrantInfoDto quadrantInfoDto) {
    this.quadrantInfoDto = quadrantInfoDto;
  }

  public LocalDateTime getDeployAt() {
    return deployAt;
  }

  public void setDeployAt(LocalDateTime deployAt) {
    this.deployAt = deployAt;
  }

  public LocalDateTime getRetractAt() {
    return retractAt;
  }

  public void setRetractAt(LocalDateTime retractAt) {
    this.retractAt = retractAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VehicleQuadrantLogDto that = (VehicleQuadrantLogDto) o;
    return Objects.equals(vehicleDto, that.vehicleDto) && Objects.equals(quadrantInfoDto, that.quadrantInfoDto)
        && Objects.equals(deployAt, that.deployAt) && Objects.equals(retractAt, that.retractAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vehicleDto, quadrantInfoDto, deployAt, retractAt);
  }


  @Override
  public String toString() {
    return "VehicleQuadrantLogDto{" +
        "vehicleDto=" + vehicleDto +
        ", quadrantInfoDto=" + quadrantInfoDto +
        ", deployAt=" + deployAt +
        ", retractAt=" + retractAt +
        '}';
  }
}
