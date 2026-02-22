package es.udc.fireproject.backend.rest.dtos.mappers;

import es.udc.fireproject.backend.model.entities.image.Image;
import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.rest.dtos.CoordinatesDto;
import es.udc.fireproject.backend.rest.dtos.ImageDto;
import es.udc.fireproject.backend.rest.dtos.NoticeResponseDto;
import es.udc.fireproject.backend.rest.dtos.NoticeResponseDto.StatusEnum;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoticeMapper {

  public static NoticeResponseDto toNoticeDto(Notice notice, Quadrant quadrant) {
    List<ImageDto> imageDtoList = new ArrayList<>();
    if (notice.getImageList() != null && !notice.getImageList().isEmpty()) {
      for (Image image : notice.getImageList()) {
        imageDtoList.add(ImageMapper.toImageDto(image));
      }
    }

    CoordinatesDto coordinatesDto = new CoordinatesDto();
    coordinatesDto.setLon(notice.getLocation().getX());
    coordinatesDto.setLat(notice.getLocation().getY());

    NoticeResponseDto dto = new NoticeResponseDto(notice.getId(),
        notice.getBody(),
        StatusEnum.valueOf(String.valueOf(notice.getStatus())),
        notice.getCreatedAt(),
        notice.getUser() != null ? UserMapper.toUserDto(notice.getUser()) : null,
        coordinatesDto,
        imageDtoList);

    if (quadrant != null) {
      dto.setQuadrantName(quadrant.getNombre());
      dto.setQuadrantId(quadrant.getId());
    }

    return dto;
  }

  public static NoticeResponseDto toNoticeDto(Notice notice) {
    return toNoticeDto(notice, null);
  }
}
