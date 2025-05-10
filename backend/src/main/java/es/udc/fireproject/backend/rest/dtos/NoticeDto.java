package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class NoticeDto extends BaseDto {

  private static final long serialVersionUID = 965287827989720585L;

  @Id
  private Long id;

  private String body;

  private NoticeStatus status;


  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserDto user;

  @NotNull(groups = {NoticeDto.AllValidations.class})
  private double lon;

  @NotNull(groups = {NoticeDto.AllValidations.class})
  private double lat;


  @JsonProperty("images")
  private List<ImageDto> imageList;

  public NoticeDto() {
  }

  public NoticeDto(Long id, String body, NoticeStatus status, LocalDateTime createdAt, UserDto user, double lon,
      double lat, List<ImageDto> imageList) {
    this.id = id;
    this.body = body;
    this.status = status;
    this.createdAt = createdAt;
    this.user = user;
    this.lon = lon;
    this.lat = lat;
    this.imageList = imageList;
  }

  public NoticeDto(Long id, String body, NoticeStatus status, LocalDateTime createdAt, double lon, double lat,
      List<ImageDto> imageList) {
    this.id = id;
    this.body = body;
    this.status = status;
    this.createdAt = createdAt;
    this.lon = lon;
    this.lat = lat;
    this.imageList = imageList;
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
