package es.udc.fireproject.backend.model.entities.organization;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import jakarta.persistence.Entity;
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
@EqualsAndHashCode(callSuper = false)
@ToString
public class OrganizationType extends BaseEntity {

  private static final long serialVersionUID = 3441744938370182772L;

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
