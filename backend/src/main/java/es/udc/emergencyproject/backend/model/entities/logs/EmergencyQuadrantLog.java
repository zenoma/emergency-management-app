package es.udc.emergencyproject.backend.model.entities.logs;

import es.udc.emergencyproject.backend.model.entities.emergency.Emergency;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
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
@Table(name = "emergency_quadrant_log", schema = "public")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EmergencyQuadrantLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "emergency_id", nullable = false)
  private Emergency emergency;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "quadrant_gid", nullable = false)
  private Quadrant quadrant;
  @Column(name = "linked_at", nullable = false)
  private LocalDateTime linkedAt;
  @Column(name = "resolved_at", nullable = false)
  private LocalDateTime resolvedAt;

  public EmergencyQuadrantLog() {
  }

  public EmergencyQuadrantLog(Emergency emergency, Quadrant quadrant, LocalDateTime linkedAt,
      LocalDateTime resolvedAt) {
    this.emergency = emergency;
    this.quadrant = quadrant;
    this.linkedAt = linkedAt;
    this.resolvedAt = resolvedAt;
  }
}
