package es.udc.fireproject.backend.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class OrganizationTypeDto extends BaseDto {

  private static final long serialVersionUID = 7299775820290018478L;

  @NotNull
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

}
