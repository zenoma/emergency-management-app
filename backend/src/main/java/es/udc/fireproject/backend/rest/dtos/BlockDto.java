package es.udc.fireproject.backend.rest.dtos;

import java.util.List;
import java.util.Objects;

public class BlockDto<T> {

  private List<T> items;
  private boolean existMoreItems;

  public BlockDto() {
  }

  public BlockDto(List<T> items, boolean existMoreItems) {

    this.items = items;
    this.existMoreItems = existMoreItems;

  }

  public List<T> getItems() {
    return items;
  }

  public void setItems(List<T> items) {
    this.items = items;
  }

  public boolean getExistMoreItems() {
    return existMoreItems;
  }

  public void setExistMoreItems(boolean existMoreItems) {
    this.existMoreItems = existMoreItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BlockDto<?> blockDto = (BlockDto<?>) o;
    return existMoreItems == blockDto.existMoreItems && Objects.equals(items, blockDto.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items, existMoreItems);
  }

  @Override
  public String toString() {
    return "BlockDto{" +
        "items=" + items +
        ", existMoreItems=" + existMoreItems +
        '}';
  }
}
