package es.udc.fireproject.backend.rest.dtos.conversors;

import es.udc.fireproject.backend.model.entities.image.Image;
import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.rest.dtos.CoordinatesDto;
import es.udc.fireproject.backend.rest.dtos.ImageDto;
import es.udc.fireproject.backend.rest.dtos.NoticeResponseDto;
import es.udc.fireproject.backend.rest.dtos.NoticeResponseDto.StatusEnum;
import java.util.ArrayList;
import java.util.List;

public class NoticeConversor {

  private NoticeConversor() {
  }


  public static NoticeResponseDto toNoticeDto(Notice notice) {
    List<ImageDto> imageDtoList = new ArrayList<>();
    if (notice.getImageList() != null && !notice.getImageList().isEmpty()) {
      for (Image image : notice.getImageList()) {
        imageDtoList.add(ImageConversor.toImageDto(image));
      }
    }

    CoordinatesDto coordinatesDto = new CoordinatesDto();
    coordinatesDto.setLon(notice.getLocation().getX());
    coordinatesDto.setLat(notice.getLocation().getY());

    return new NoticeResponseDto(notice.getId(),
        notice.getBody(),
        StatusEnum.valueOf(String.valueOf(notice.getStatus())),
        notice.getCreatedAt(),
        notice.getUser() != null ? UserConversor.toUserDto(notice.getUser()) : null,
        coordinatesDto,
        imageDtoList);

  }
}
