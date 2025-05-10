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
public class VehicleDto extends BaseDto {

  private static final long serialVersionUID = -6531781678159378396L;

  private Long id;
  private String vehiclePlate;
  private String type;
  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime createdAt;
  private OrganizationDto organization;
  private QuadrantInfoDto quadrant;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime deployAt;


  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime dismantleAt;

  public VehicleDto() {
  }

  public VehicleDto(Long id, String vehiclePlate, String type, LocalDateTime createdAt, OrganizationDto organization,
      QuadrantInfoDto quadrant, LocalDateTime deployAt, LocalDateTime dismantleAt) {
    this.id = id;
    this.vehiclePlate = vehiclePlate;
    this.type = type;
    this.createdAt = createdAt;
    this.organization = organization;
    this.quadrant = quadrant;
    this.deployAt = deployAt;
    this.dismantleAt = dismantleAt;
  }

  public VehicleDto(Long id, String vehiclePlate, String type, LocalDateTime createdAt, OrganizationDto organization,
      LocalDateTime deployAt, LocalDateTime dismantleAt) {
    this.id = id;
    this.vehiclePlate = vehiclePlate;
    this.type = type;
    this.createdAt = createdAt;
    this.organization = organization;
    this.deployAt = deployAt;
    this.dismantleAt = dismantleAt;
  }

}
