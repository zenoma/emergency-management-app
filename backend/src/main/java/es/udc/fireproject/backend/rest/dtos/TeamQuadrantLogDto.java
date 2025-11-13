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
public class TeamQuadrantLogDto extends BaseDto {

  private static final long serialVersionUID = -5703692848678170759L;

  private TeamResponseDto teamResponseDto;

  private QuadrantInfoDto quadrantInfoDto;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime deployAt;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime retractAt;

  public TeamQuadrantLogDto() {
  }

  public TeamQuadrantLogDto(TeamResponseDto teamResponseDto, QuadrantInfoDto quadrantInfoDto, LocalDateTime deployAt,
      LocalDateTime retractAt) {
    this.teamResponseDto = teamResponseDto;
    this.quadrantInfoDto = quadrantInfoDto;
    this.deployAt = deployAt;
    this.retractAt = retractAt;
  }

}
