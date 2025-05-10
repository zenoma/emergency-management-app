package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
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


  @JsonProperty("coordinates")
  private void unpackNested(Map<String, Double> coordinates) {
    this.lon = coordinates.get("lon");
    this.lat = coordinates.get("lat");
  }

  public interface AllValidations {

  }

  public interface UpdateValidations {

  }
}
