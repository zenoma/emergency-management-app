package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.locationtech.jts.geom.Coordinates;

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

  public String getFolla50() {
    return folla50;
  }

  public void setFolla50(String folla50) {
    this.folla50 = folla50;
  }

  public String getFolla25() {
    return folla25;
  }

  public void setFolla25(String folla25) {
    this.folla25 = folla25;
  }

  public String getFolla5() {
    return folla5;
  }

  public void setFolla5(String folla5) {
    this.folla5 = folla5;
  }

  public List<Coordinates> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<Coordinates> coordinates) {
    this.coordinates = coordinates;
  }

  public Long getFireId() {
    return fireId;
  }

  public void setFireId(Long fireId) {
    this.fireId = fireId;
  }

  public LocalDateTime getLinkedAt() {
    return linkedAt;
  }

  public void setLinkedAt(LocalDateTime linkedAt) {
    this.linkedAt = linkedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuadrantDto that = (QuadrantDto) o;
    return Objects.equals(id, that.id) && Objects.equals(escala, that.escala) && Objects.equals(nombre, that.nombre)
        && Objects.equals(folla50, that.folla50) && Objects.equals(folla25, that.folla25) && Objects.equals(folla5,
        that.folla5) && Objects.equals(fireId, that.fireId) && Objects.equals(linkedAt, that.linkedAt)
        && Objects.equals(coordinates, that.coordinates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, escala, nombre, folla50, folla25, folla5, fireId, linkedAt, coordinates);
  }

  @Override
  public String toString() {
    return "QuadrantDto{" +
        "id=" + id +
        ", escala='" + escala + '\'' +
        ", nombre='" + nombre + '\'' +
        ", folla50='" + folla50 + '\'' +
        ", folla25='" + folla25 + '\'' +
        ", folla5='" + folla5 + '\'' +
        ", fireId=" + fireId +
        ", linkedAt=" + linkedAt +
        ", coordinates=" + coordinates +
        '}';
  }
}
