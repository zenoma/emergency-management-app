package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class OrganizationDto extends BaseDto {


  private static final long serialVersionUID = 6003901619347671472L;

  @Id
  @NotBlank(groups = {OrganizationDto.AllValidations.class})
  private Long id;

  @NotBlank(groups = {OrganizationDto.AllValidations.class})
  private String code;


  @NotBlank(groups = {OrganizationDto.AllValidations.class})
  private String name;


  @NotBlank(groups = {OrganizationDto.AllValidations.class})
  @Column(name = "headquarters_address")
  private String headquartersAddress;


  private double lon;

  private double lat;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime createdAt;

  @NotNull(groups = {OrganizationDto.AllValidations.class})
  private Long organizationTypeId;

  @NotBlank(groups = {OrganizationDto.AllValidations.class})
  @JsonProperty("organizationType")
  private String organizationTypeName;

  public OrganizationDto() {
  }


  public OrganizationDto(Long id, String code, String name, String headquartersAddress, double lon, double lat,
      LocalDateTime createdAt, String organizationTypeName) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.headquartersAddress = headquartersAddress;
    this.lon = lon;
    this.lat = lat;
    this.createdAt = createdAt;
    this.organizationTypeName = organizationTypeName;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHeadquartersAddress() {
    return headquartersAddress;
  }

  public void setHeadquartersAddress(String headquartersAddress) {
    this.headquartersAddress = headquartersAddress;
  }

  public double getLon() {
    return lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public Long getOrganizationTypeId() {
    return organizationTypeId;
  }

  public void getOrganizationTypeId(Long organizationTypeId) {
    this.organizationTypeId = organizationTypeId;
  }

  public String getOrganizationTypeName() {
    return organizationTypeName;
  }

  public void setOrganizationTypeName(String organizationTypeName) {
    this.organizationTypeName = organizationTypeName;
  }

  @JsonProperty("coordinates")
  private void unpackNested(Map<String, Double> coordinates) {
    this.lon = coordinates.get("lon");
    this.lat = coordinates.get("lat");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationDto that = (OrganizationDto) o;
    return Double.compare(that.lon, lon) == 0 && Double.compare(that.lat, lat) == 0 && Objects.equals(id, that.id)
        && Objects.equals(code, that.code) && Objects.equals(name, that.name) && Objects.equals(headquartersAddress,
        that.headquartersAddress) && Objects.equals(createdAt, that.createdAt) && Objects.equals(organizationTypeId,
        that.organizationTypeId) && Objects.equals(organizationTypeName, that.organizationTypeName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, code, name, headquartersAddress, lon, lat, createdAt, organizationTypeId,
        organizationTypeName);
  }

  @Override
  public String toString() {
    return "OrganizationDto{" +
        "id=" + id +
        ", code='" + code + '\'' +
        ", name='" + name + '\'' +
        ", headquartersAddress='" + headquartersAddress + '\'' +
        ", lon=" + lon +
        ", lat=" + lat +
        ", createdAt=" + createdAt +
        ", organizationTypeId=" + organizationTypeId +
        ", organizationTypeName='" + organizationTypeName + '\'' +
        '}';
  }

  public interface AllValidations {

  }

  public interface UpdateValidations {

  }
}
