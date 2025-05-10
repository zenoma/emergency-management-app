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
public class ImageDto extends BaseDto {

  private static final long serialVersionUID = 6239816404285860032L;


  private Long id;
  private String name;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime createdAt;

  public ImageDto() {
  }

  public ImageDto(Long id, String name, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
  }

}
