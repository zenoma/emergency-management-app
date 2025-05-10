package es.udc.fireproject.backend.model.entities.logs;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "fire_quadrant_log", schema = "public")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class FireQuadrantLog extends BaseEntity {

  private static final long serialVersionUID = -7339295013066556565L;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "fire_id", nullable = false)
  private Fire fire;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "quadrant_gid", nullable = false)
  private Quadrant quadrant;
  @Column(name = "linked_at", nullable = false)
  private LocalDateTime linkedAt;
  @Column(name = "extinguished_at", nullable = false)
  private LocalDateTime extinguishedAt;

  public FireQuadrantLog() {
  }

  public FireQuadrantLog(Fire fire, Quadrant quadrant, LocalDateTime linkedAt, LocalDateTime extinguishedAt) {
    this.fire = fire;
    this.quadrant = quadrant;
    this.linkedAt = linkedAt;
    this.extinguishedAt = extinguishedAt;
  }
}
