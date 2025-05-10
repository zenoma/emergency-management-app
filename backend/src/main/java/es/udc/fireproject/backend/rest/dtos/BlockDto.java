package es.udc.fireproject.backend.rest.dtos;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BlockDto<T> {

  private List<T> items;
  private boolean existMoreItems;

  public BlockDto() {
  }

  public BlockDto(List<T> items, boolean existMoreItems) {

    this.items = items;
    this.existMoreItems = existMoreItems;

  }

}
