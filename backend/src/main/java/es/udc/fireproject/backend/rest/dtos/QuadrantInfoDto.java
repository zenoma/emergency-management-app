package es.udc.fireproject.backend.rest.dtos;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class QuadrantInfoDto extends BaseDto {

  private static final long serialVersionUID = 4848346612436497001L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String escala;

  private String nombre;

  private List<Coordinates> coordinates;

  private List<TeamDto> teamDtoList;
  private List<VehicleDto> vehicleDtoList;


  public QuadrantInfoDto() {
  }

  public QuadrantInfoDto(Integer id, String escala, String nombre, List<TeamDto> teamDtoList,
      List<VehicleDto> vehicleDtoList, List<Coordinates> coordinates) {
    this.id = id;
    this.escala = escala;
    this.nombre = nombre;
    this.teamDtoList = teamDtoList;
    this.vehicleDtoList = vehicleDtoList;
    this.coordinates = coordinates;
  }

  public QuadrantInfoDto(Integer id, String escala, String nombre, List<Coordinates> coordinates) {
    this.id = id;
    this.escala = escala;
    this.nombre = nombre;
    this.coordinates = coordinates;
  }


}
