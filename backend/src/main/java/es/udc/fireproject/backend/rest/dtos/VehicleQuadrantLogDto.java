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

  private VehicleResponseDto vehicleResponseDto;

  private QuadrantInfoDto quadrantInfoDto;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime deployAt;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime retractAt;


  public VehicleQuadrantLogDto() {
  }

  public VehicleQuadrantLogDto(VehicleResponseDto vehicleResponseDto, QuadrantInfoDto quadrantInfoDto,
      LocalDateTime deployAt,
      LocalDateTime retractAt) {
    this.vehicleResponseDto = vehicleResponseDto;
    this.quadrantInfoDto = quadrantInfoDto;
    this.deployAt = deployAt;
    this.retractAt = retractAt;
  }

}
