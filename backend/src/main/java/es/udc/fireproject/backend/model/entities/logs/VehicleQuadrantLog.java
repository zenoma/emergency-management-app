package es.udc.fireproject.backend.model.entities.logs;

import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "vehicle_quadrant_log", schema = "public")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class VehicleQuadrantLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;


  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "vehicle_id", nullable = false)
  private Vehicle vehicle;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "quadrant_gid", nullable = false)
  private Quadrant quadrant;


  @Column(name = "deploy_at", nullable = false)
  private LocalDateTime deployAt;
  @Column(name = "retract_at", nullable = false)
  private LocalDateTime retractAt;

  public VehicleQuadrantLog() {
  }

  public VehicleQuadrantLog(Vehicle fire, Quadrant quadrant, LocalDateTime deployAt, LocalDateTime retractAt) {
    this.vehicle = fire;
    this.quadrant = quadrant;
    this.deployAt = deployAt;
    this.retractAt = retractAt;
  }

}
