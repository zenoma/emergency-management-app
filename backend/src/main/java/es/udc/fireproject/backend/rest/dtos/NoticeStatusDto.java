package es.udc.fireproject.backend.rest.dtos;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class NoticeStatusDto extends BaseDto {

  private static final long serialVersionUID = 1641437225518108070L;

  private String status;

  NoticeStatusDto() {

  }
}
