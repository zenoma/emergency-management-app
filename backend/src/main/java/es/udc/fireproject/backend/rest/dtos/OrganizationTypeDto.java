package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class OrganizationTypeDto extends BaseDto {

  private static final long serialVersionUID = 7299775820290018478L;

  @NotNull(groups = {OrganizationTypeDto.AllValidations.class})
  private Long id;

  @NotBlank
  @JsonProperty("name")
  private String name;

  public OrganizationTypeDto() {
  }

  public OrganizationTypeDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
    OrganizationTypeDto that = (OrganizationTypeDto) o;
    return Objects.equals(id, that.id) && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    return "OrganizationTypeDto{" +
        "id=" + id +
        ", name='" + name + '\'' +
        '}';
  }

  public interface AllValidations {

  }

  public interface UpdateValidations {

  }
}
