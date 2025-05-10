package es.udc.fireproject.backend.rest.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class IdDto extends BaseDto {

  private static final long serialVersionUID = -5638281097675204085L;

  private Long id;

  public IdDto() {
  }

  public IdDto(Long id) {
    this.id = id;
  }


}
