package es.udc.fireproject.backend.model.entities.team;

import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.user.User;
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
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Team {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column(name = "code")
  @NotBlank
  private String code;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @ManyToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "organization_id", nullable = false)
  private Organization organization;

  @OneToMany(
      mappedBy = "team",
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  private List<User> userList;

  @ManyToOne(
      optional = false,
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinColumn(name = "quadrant_gid", nullable = false)
  private Quadrant quadrant;

  @Column(name = "deploy_at")
  private LocalDateTime deployAt;

  @Column(name = "dismantle_at")
  private LocalDateTime dismantleAt;


  public Team() {

  }

  public Team(String code, Organization organization) {
    this.code = code;
    this.organization = organization;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }

}

