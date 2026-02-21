package es.udc.fireproject.backend.model.entities.organization;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "organization_type")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class OrganizationType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @NotBlank
  private String name;

  public OrganizationType() {
  }

  public OrganizationType(String name) {
    this.name = name;
  }

  public OrganizationType(Long id, String name) {
    setId(id);
    this.name = name;
  }

}
