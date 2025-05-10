package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.udc.fireproject.backend.model.entities.fire.FireIndex;
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
public class FireDto extends BaseDto {

  private static final long serialVersionUID = -91681558665988183L;

  private Long id;

  private String description;

  private String type;

  private FireIndex fireIndex;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime extinguishedAt;

  @JsonProperty("quadrants")
  private List<QuadrantInfoDto> quadrantDtoList;


  public FireDto() {
  }

  public FireDto(Long id, String description, String type, FireIndex fireIndex, List<QuadrantInfoDto> quadrantDtoList,
      LocalDateTime createdAt, LocalDateTime extinguishedAt) {
    this.id = id;
    this.description = description;
    this.type = type;
    this.fireIndex = fireIndex;
    this.quadrantDtoList = quadrantDtoList;
    this.createdAt = createdAt;
    this.extinguishedAt = extinguishedAt;
  }

  public FireDto(Long id, String description, String type, FireIndex fireIndex, LocalDateTime createdAt,
      LocalDateTime extinguishedAt) {
    this.id = id;
    this.description = description;
    this.type = type;
    this.fireIndex = fireIndex;
    this.createdAt = createdAt;
    this.extinguishedAt = extinguishedAt;
  }

}
