package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.exceptions.FileUploadException;
import es.udc.fireproject.backend.model.services.notice.NoticeService;
import es.udc.fireproject.backend.rest.config.JwtInfo;
import es.udc.fireproject.backend.rest.config.JwtUtils;
import es.udc.fireproject.backend.rest.dtos.NoticeRequestDto;
import es.udc.fireproject.backend.rest.dtos.NoticeResponseDto;
import es.udc.fireproject.backend.rest.dtos.NoticeStatusRequestDto;
import es.udc.fireproject.backend.rest.dtos.mappers.NoticeMapper;
import es.udc.fireproject.backend.rest.exceptions.ImageRequiredException;
import es.udc.fireproject.backend.util.FileUploadUtil;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class NoticeController implements NoticesApi {

  private final NoticeService noticeService;

  @Override
  public ResponseEntity<NoticeResponseDto> postNotice(NoticeRequestDto noticeRequestDto) {

    final Optional<JwtInfo> jwtInfo = JwtUtils.getJwtInfo();

    Notice notice;

    if (jwtInfo.isPresent()) {
      notice = noticeService.create(noticeRequestDto.getBody(), noticeRequestDto.getCoordinates().getLon(),
          noticeRequestDto.getCoordinates().getLat(), jwtInfo.get().userId());
    } else {
      notice = noticeService.create(noticeRequestDto.getBody(), noticeRequestDto.getCoordinates().getLon(),
          noticeRequestDto.getCoordinates().getLat());
    }

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest().path("/{id}")
        .buildAndExpand(notice.getId()).toUri();

    return ResponseEntity.created(location).body(
        NoticeMapper.toNoticeDto(notice, noticeService.findQuadrantByLocation(notice.getLocation()).orElse(null)));

  }

  @Override
  public ResponseEntity<NoticeResponseDto> getNoticeById(Long id) {
    Notice notice = noticeService.findById(id);
    final NoticeResponseDto noticeResponseDto = NoticeMapper.toNoticeDto(notice,
        noticeService.findQuadrantByLocation(notice.getLocation()).orElse(null));

    return ResponseEntity.ok(noticeResponseDto);
  }

  @Override
  public ResponseEntity<List<NoticeResponseDto>> getNotices(Long userId) {

    List<NoticeResponseDto> noticeResponseDtoList = new ArrayList<>();
    List<Notice> notices =
        userId != null ? noticeService.findByUserIdWithImages(userId) : noticeService.findAllWithImages();
    for (Notice notice : notices) {
      Quadrant quadrant = noticeService.findQuadrantByLocation(notice.getLocation()).orElse(null);
      noticeResponseDtoList.add(NoticeMapper.toNoticeDto(notice, quadrant));
    }

    return ResponseEntity.ok(noticeResponseDtoList);
  }

  @Override
  public ResponseEntity<Void> putNotice(Long id, NoticeRequestDto noticeRequestDto) {

    noticeService.update(id, noticeRequestDto.getBody(), noticeRequestDto.getCoordinates().getLon(),
        noticeRequestDto.getCoordinates().getLat());

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> deleteNotice(Long id) {

    noticeService.deleteById(id);

    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> putNoticeStatus(Long id, NoticeStatusRequestDto noticeStatusRequestDto) {

    final NoticeStatus noticeStatus = NoticeStatus.valueOf(noticeStatusRequestDto.getStatus().getValue());

    noticeService.checkNotice(id, noticeStatus);

    return ResponseEntity.noContent().build();

  }

  @Override
  public ResponseEntity<NoticeResponseDto> postNoticeImage(Long id, MultipartFile multipartFile) {

    try {
      if (multipartFile.isEmpty()) {
        throw new ImageRequiredException();
      }
      String fileName = id + "-" + StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

      NoticeResponseDto noticeResponseDto = NoticeMapper.toNoticeDto(
          noticeService.addImage(id, fileName),
          noticeService.findQuadrantByLocation(
              noticeService.findById(id).getLocation()).orElse(null));

      String uploadDir = "public/images/" + id;
      FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

      return ResponseEntity.ok(noticeResponseDto);

    } catch (IOException e) {
      throw new FileUploadException(e);
    }
  }
}
