package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.Coordinates;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class QuadrantDto extends BaseDto {

  private static final long serialVersionUID = 4848346612436497001L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String escala;

  private String nombre;

  private String folla50;

  private String folla25;

  private String folla5;

  private Long fireId;

  @JsonFormat(pattern = "dd-MM-yyy HH:mm:ss")
  private LocalDateTime linkedAt;


  private List<Coordinates> coordinates;


  public QuadrantDto(Integer id, String escala, String nombre, String folla50, String folla25, String folla5,
      List<Coordinates> coordinates, Long fireId,
      LocalDateTime linkedAt) {
    this.id = id;
    this.escala = escala;
    this.nombre = nombre;
    this.folla50 = folla50;
    this.folla25 = folla25;
    this.folla5 = folla5;
    this.coordinates = coordinates;
    this.fireId = fireId;
    this.linkedAt = linkedAt;
  }

}
