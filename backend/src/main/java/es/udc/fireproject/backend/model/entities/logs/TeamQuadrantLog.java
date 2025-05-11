package es.udc.fireproject.backend.model.entities.logs;

import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.team.Team;
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
@Table(name = "team_quadrant_log", schema = "public")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class TeamQuadrantLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

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

}
