package es.udc.fireproject.backend.model.entities.quadrant;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.team.Team;
import es.udc.fireproject.backend.model.entities.vehicle.Vehicle;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "quadrants", schema = "public")
public class Quadrant implements Serializable {

  private static final long serialVersionUID = 4848346612436497001L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "gid", nullable = false)
  private Integer id;

  @Column(name = "escala", length = 50)
  private String escala;

  @Column(name = "nombre", length = 50)
  private String nombre;

  @Column(name = "folla50", length = 50)
  private String folla50;

  @Column(name = "folla25", length = 50)
  private String folla25;

  @Column(name = "folla5", length = 50)
  private String folla5;

  @Column(columnDefinition = "geom")
  private Geometry geom;

  @OneToMany(
      mappedBy = "quadrant",
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  private List<Team> teamList;

  @OneToMany(
      mappedBy = "quadrant",
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  private List<Vehicle> vehicleList;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "fire_id", nullable = false)
  private Fire fire;

  @Column(name = "fire_linked_at")
  private LocalDateTime linkedAt;

  public Quadrant() {
  }

  public Fire getFire() {
    return fire;
  }

  public void setFire(Fire fire) {
    this.fire = fire;
  }

  public Geometry getGeom() {
    return geom;
  }

  public void setGeom(MultiPolygon geom) {
    this.geom = geom;
  }

  public String getFolla5() {
    return folla5;
  }

  public void setFolla5(String folla5) {
    this.folla5 = folla5;
  }

  public String getFolla25() {
    return folla25;
  }

  public void setFolla25(String folla25) {
    this.folla25 = folla25;
  }

  public String getFolla50() {
    return folla50;
  }

  public void setFolla50(String folla50) {
    this.folla50 = folla50;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getEscala() {
    return escala;
  }

  public void setEscala(String escala) {
    this.escala = escala;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public List<Team> getTeamList() {
    return teamList;
  }

  public void setTeamList(List<Team> teamList) {
    this.teamList = teamList;
  }

  public List<Vehicle> getVehicleList() {
    return vehicleList;
  }

  public void setVehicleList(List<Vehicle> vehicleList) {
    this.vehicleList = vehicleList;
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
    Quadrant quadrant = (Quadrant) o;
    return Objects.equals(id, quadrant.id) && Objects.equals(escala, quadrant.escala) && Objects.equals(nombre,
        quadrant.nombre) && Objects.equals(folla50, quadrant.folla50) && Objects.equals(folla25, quadrant.folla25)
        && Objects.equals(folla5, quadrant.folla5) && Objects.equals(geom, quadrant.geom) && Objects.equals(teamList,
        quadrant.teamList) && Objects.equals(vehicleList, quadrant.vehicleList) && Objects.equals(fire, quadrant.fire)
        && Objects.equals(linkedAt, quadrant.linkedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, escala, nombre, folla50, folla25, folla5, geom, teamList, vehicleList, fire, linkedAt);
  }

  @Override
  public String toString() {
    return "Quadrant{" +
        "id=" + id +
        ", escala='" + escala + '\'' +
        ", nombre='" + nombre + '\'' +
        ", folla50='" + folla50 + '\'' +
        ", folla25='" + folla25 + '\'' +
        ", folla5='" + folla5 + '\'' +
        ", geom=" + geom +
        ", teamList=" + teamList +
        ", vehicleList=" + vehicleList +
        ", fire=" + fire +
        ", linkedAt=" + linkedAt +
        '}';
  }
}
