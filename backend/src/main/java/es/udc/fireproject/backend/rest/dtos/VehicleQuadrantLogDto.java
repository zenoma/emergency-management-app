package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
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

}
