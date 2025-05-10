package es.udc.fireproject.backend.model.entities.logs;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.team.Team;
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
@Table(name = "team_quadrant_log", schema = "public")
public class TeamQuadrantLog extends BaseEntity {

  private static final long serialVersionUID = 7276431595599483372L;


  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "quadrant_gid", nullable = false)
  private Quadrant quadrant;


  @Column(name = "deploy_at", nullable = false)
  private LocalDateTime deployAt;
  @Column(name = "retract_at", nullable = false)
  private LocalDateTime retractAt;

  public TeamQuadrantLog() {
  }

  public TeamQuadrantLog(Team team, Quadrant quadrant, LocalDateTime deployAt, LocalDateTime retractAt) {
    this.team = team;
    this.quadrant = quadrant;
    this.deployAt = deployAt;
    this.retractAt = retractAt;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public Quadrant getQuadrant() {
    return quadrant;
  }

  public void setQuadrant(Quadrant quadrant) {
    this.quadrant = quadrant;
  }

  public LocalDateTime getDeployAt() {
    return deployAt;
  }

  public void setDeployAt(LocalDateTime deployAt) {
    this.deployAt = deployAt;
  }

  public LocalDateTime getRetractAt() {
    return retractAt;
  }

  public void setRetractAt(LocalDateTime retractAt) {
    this.retractAt = retractAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TeamQuadrantLog that = (TeamQuadrantLog) o;
    return Objects.equals(team, that.team) && Objects.equals(quadrant, that.quadrant) && Objects.equals(deployAt,
        that.deployAt) && Objects.equals(retractAt, that.retractAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(team, quadrant, deployAt, retractAt);
  }

  @Override
  public String toString() {
    return "TeamQuadrantLog{" +
        "team=" + team +
        ", quadrant=" + quadrant +
        ", deployAt=" + deployAt +
        ", retractAt=" + retractAt +
        '}';
  }
}
