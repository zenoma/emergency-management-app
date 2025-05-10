package es.udc.fireproject.backend.model.entities.organization;

import es.udc.fireproject.backend.model.entities.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "organization_type")
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


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationType that = (OrganizationType) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "OrganizationType{" +
        "id=" + getId() +
        ", name='" + name + '\'' +
        '}';
  }
}
