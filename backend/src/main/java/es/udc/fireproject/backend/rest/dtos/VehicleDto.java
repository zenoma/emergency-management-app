package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Objects;

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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getVehiclePlate() {
    return vehiclePlate;
  }

  public void setVehiclePlate(String vehiclePlate) {
    this.vehiclePlate = vehiclePlate;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OrganizationDto getOrganization() {
    return organization;
  }

  public void setOrganization(OrganizationDto organization) {
    this.organization = organization;
  }

  public QuadrantInfoDto getQuadrant() {
    return quadrant;
  }

  public void setQuadrant(QuadrantInfoDto quadrant) {
    this.quadrant = quadrant;
  }

  public LocalDateTime getDeployAt() {
    return deployAt;
  }

  public void setDeployAt(LocalDateTime deployAt) {
    this.deployAt = deployAt;
  }

  public LocalDateTime getDismantleAt() {
    return dismantleAt;
  }

  public void setDismantleAt(LocalDateTime dismantleAt) {
    this.dismantleAt = dismantleAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VehicleDto that = (VehicleDto) o;
    return Objects.equals(id, that.id) && Objects.equals(vehiclePlate, that.vehiclePlate) && Objects.equals(type,
        that.type) && Objects.equals(createdAt, that.createdAt) && Objects.equals(organization, that.organization)
        && Objects.equals(quadrant, that.quadrant) && Objects.equals(deployAt, that.deployAt) && Objects.equals(
        dismantleAt, that.dismantleAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, vehiclePlate, type, createdAt, organization, quadrant, deployAt, dismantleAt);
  }

  @Override
  public String toString() {
    return "VehicleDto{" +
        "id=" + id +
        ", vehiclePlate='" + vehiclePlate + '\'' +
        ", type='" + type + '\'' +
        ", createdAt=" + createdAt +
        ", organization=" + organization +
        ", quadrant=" + quadrant +
        ", deployAt=" + deployAt +
        ", dismantleAt=" + dismantleAt +
        '}';
  }
}
