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
import java.util.Objects;

@Entity
@Table(name = "fire_quadrant_log", schema = "public")
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

  public Fire getFire() {
    return fire;
  }

  public void setFire(Fire fire) {
    this.fire = fire;
  }

  public Quadrant getQuadrant() {
    return quadrant;
  }

  public void setQuadrant(Quadrant quadrant) {
    this.quadrant = quadrant;
  }

  public LocalDateTime getLinkedAt() {
    return linkedAt;
  }

  public void setLinkedAt(LocalDateTime linkedAt) {
    this.linkedAt = linkedAt;
  }

  public LocalDateTime getExtinguishedAt() {
    return extinguishedAt;
  }

  public void setExtinguishedAt(LocalDateTime extinguishedAt) {
    this.extinguishedAt = extinguishedAt;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FireQuadrantLog that = (FireQuadrantLog) o;
    return Objects.equals(fire, that.fire) && Objects.equals(quadrant, that.quadrant) && Objects.equals(linkedAt,
        that.linkedAt) && Objects.equals(extinguishedAt, that.extinguishedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fire, quadrant, linkedAt, extinguishedAt);
  }

  @Override
  public String toString() {
    return "FireQuadrantLog{" +
        "fire=" + fire +
        ", quadrant=" + quadrant +
        ", linkedAt=" + linkedAt +
        ", extinguishedAt=" + extinguishedAt +
        '}';
  }
}
