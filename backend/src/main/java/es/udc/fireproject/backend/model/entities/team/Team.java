package es.udc.fireproject.backend.model.entities.team;

import es.udc.fireproject.backend.model.entities.resource.Resource;
import es.udc.fireproject.backend.model.entities.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@EqualsAndHashCode
@ToString
public class Team extends Resource {

  @Column(name = "code")
  @NotBlank
  private String code;

  @OneToMany(
      mappedBy = "team",
      fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<User> userList;

  public Team() {

  }

  public Team(String code) {
    this.code = code;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }

  public Team(String code, es.udc.fireproject.backend.model.entities.organization.Organization organization) {
    this.code = code;
    this.organization = organization;
    this.createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
  }

}
