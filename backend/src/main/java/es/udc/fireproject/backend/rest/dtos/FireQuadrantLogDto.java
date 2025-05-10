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
public class FireQuadrantLogDto extends BaseDto {

  private static final long serialVersionUID = -5703692848678170759L;

  private FireDto fireDto;

  private QuadrantInfoDto quadrantInfoDto;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime linkedAt;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime extinguishedAt;

  public FireQuadrantLogDto() {
  }

  public FireQuadrantLogDto(FireDto fireDto, QuadrantInfoDto quadrantInfoDto, LocalDateTime linkedAt,
      LocalDateTime extinguishedAt) {
    this.fireDto = fireDto;
    this.quadrantInfoDto = quadrantInfoDto;
    this.linkedAt = linkedAt;
    this.extinguishedAt = extinguishedAt;
  }


}
