package es.udc.fireproject.backend.model.entities.team;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import es.udc.fireproject.backend.model.entities.organization.Organization;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Team extends BaseEntity {

  private static final long serialVersionUID = -6662567578161123656L;

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
    this.createdAt = LocalDateTime.now();
  }

  public List<User> getUserList() {
    return userList;
  }

  public void setUserList(List<User> userList) {
    this.userList = userList;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
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

  public LocalDateTime getDismantleAt() {
    return dismantleAt;
  }

  public void setDismantleAt(LocalDateTime dismantleAt) {
    this.dismantleAt = dismantleAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Team team = (Team) o;
    return Objects.equals(code, team.code) && Objects.equals(organization, team.organization) && Objects.equals(
        userList, team.userList) && Objects.equals(quadrant, team.quadrant) && Objects.equals(deployAt, team.deployAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, createdAt, organization, userList, quadrant, deployAt, dismantleAt);
  }

  @Override
  public String toString() {
    return "Team{" +
        "code='" + code + '\'' +
        ", createdAt=" + createdAt +
        ", organization=" + organization +
        ", userList=" + userList +
        ", quadrant=" + quadrant +
        ", deployAt=" + deployAt +
        ", dismantleAt=" + dismantleAt +
        '}';
  }
}

