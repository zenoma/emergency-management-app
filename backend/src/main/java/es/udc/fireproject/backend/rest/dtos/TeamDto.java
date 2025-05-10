package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class TeamDto extends BaseDto {

  private static final long serialVersionUID = -9141625918440183253L;

  @Id
  @NotBlank(groups = {TeamDto.AllValidations.class})
  private Long id;

  @NotBlank
  private String code;

  @NotBlank(groups = {TeamDto.AllValidations.class})
  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonProperty("organization")
  private OrganizationDto organizationDto;

  @JsonProperty("users")
  private List<UserDto> userDtoList;


  private QuadrantInfoDto quadrantInfoDto;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime deployAt;


  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime dismantleAt;

  public TeamDto() {
  }

  public TeamDto(Long id, String code, LocalDateTime createdAt, OrganizationDto organizationDto,
      List<UserDto> userDtoList, QuadrantInfoDto quadrantInfoDto, LocalDateTime deployAt,
      LocalDateTime dismantleAt) {
    this.id = id;
    this.code = code;
    this.createdAt = createdAt;
    this.organizationDto = organizationDto;
    this.userDtoList = userDtoList;
    this.quadrantInfoDto = quadrantInfoDto;
    this.deployAt = deployAt;
    this.dismantleAt = dismantleAt;
  }

  public TeamDto(Long id, String code, LocalDateTime createdAt, OrganizationDto organizationDto,
      List<UserDto> userDtoList, LocalDateTime deployAt,
      LocalDateTime dismantleAt) {
    this.id = id;
    this.code = code;
    this.createdAt = createdAt;
    this.organizationDto = organizationDto;
    this.userDtoList = userDtoList;
    this.deployAt = deployAt;
    this.dismantleAt = dismantleAt;
  }

  public interface AllValidations {

  }

  public interface UpdateValidations {

  }
}
