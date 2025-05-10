package es.udc.fireproject.backend.rest.dtos;

import java.util.Objects;

public class IdDto extends BaseDto {

  private static final long serialVersionUID = -5638281097675204085L;

  private Long id;

  public IdDto() {
  }

  public IdDto(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IdDto idDto = (IdDto) o;
    return Objects.equals(id, idDto.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "IdDto{" +
        "id=" + id +
        '}';
  }
}
