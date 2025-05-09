package es.udc.fireproject.backend.rest.dtos;

import org.locationtech.jts.geom.Coordinates;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import java.util.Objects;

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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getEscala() {
    return escala;
  }

  public void setEscala(String escala) {
    this.escala = escala;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }


  public List<TeamDto> getTeamDtoList() {
    return teamDtoList;
  }

  public void setTeamDtoList(List<TeamDto> teamDtoList) {
    this.teamDtoList = teamDtoList;
  }

  public List<VehicleDto> getVehicleDtoList() {
    return vehicleDtoList;
  }

  public void setVehicleDtoList(List<VehicleDto> vehicleDtoList) {
    this.vehicleDtoList = vehicleDtoList;
  }

  public List<Coordinates> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<Coordinates> coordinates) {
    this.coordinates = coordinates;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuadrantInfoDto that = (QuadrantInfoDto) o;
    return Objects.equals(id, that.id) && Objects.equals(escala, that.escala) && Objects.equals(nombre, that.nombre)
        && Objects.equals(coordinates, that.coordinates) && Objects.equals(teamDtoList, that.teamDtoList)
        && Objects.equals(vehicleDtoList, that.vehicleDtoList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, escala, nombre, coordinates, teamDtoList, vehicleDtoList);
  }

  @Override
  public String toString() {
    return "QuadrantInfoDto{" +
        "id=" + id +
        ", escala='" + escala + '\'' +
        ", nombre='" + nombre + '\'' +
        ", coordinates=" + coordinates +
        ", teamDtoList=" + teamDtoList +
        ", vehicleDtoList=" + vehicleDtoList +
        '}';
  }
}
