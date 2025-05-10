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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "quadrants", schema = "public")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
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

  @Column(name = "location", columnDefinition = "geometry(Point, 25829)")
  private MultiPolygon geom;

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
}
