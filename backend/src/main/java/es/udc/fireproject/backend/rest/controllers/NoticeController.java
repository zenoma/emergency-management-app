package es.udc.fireproject.backend.rest.controllers;

import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.services.notice.NoticeService;
import es.udc.fireproject.backend.rest.common.FileUploadUtil;
import es.udc.fireproject.backend.rest.config.JwtInfo;
import es.udc.fireproject.backend.rest.config.JwtUtils;
import es.udc.fireproject.backend.rest.dtos.NoticeRequestDto;
import es.udc.fireproject.backend.rest.dtos.NoticeResponseDto;
import es.udc.fireproject.backend.rest.dtos.NoticeStatusRequestDto;
import es.udc.fireproject.backend.rest.dtos.mappers.NoticeMapper;
import es.udc.fireproject.backend.model.exceptions.FileUploadException;
import es.udc.fireproject.backend.rest.exceptions.ImageRequiredException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
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
    //FIXME: Este tratamiento debería ir en el caso de uso, no en el controlador
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
    Coordinate coordinate = new Coordinate(noticeRequestDto.getCoordinates().getLon(),
        noticeRequestDto.getCoordinates().getLat());

    //FIXME: Solo hace falta un método
    if (jwtInfo.isPresent()) {
      notice = noticeService.create(noticeRequestDto.getBody(), geometryFactory.createPoint(coordinate),
          jwtInfo.get().userId());
    } else {
      notice = noticeService.create(noticeRequestDto.getBody(), geometryFactory.createPoint(coordinate));
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

    //FIXME: Esta lógica debería ir en el caso de uso, no en el controlador
    List<NoticeResponseDto> noticeRequestDtos = new ArrayList<>();
    List<Notice> notices = userId != null ? noticeService.findByUserIdWithImages(userId) : noticeService.findAllWithImages();
    // fetch quadrant ids in batch to avoid N+1
    for (Notice notice : notices) {
      Quadrant quadrant = noticeService.findQuadrantByLocation(notice.getLocation()).orElse(null);
      noticeRequestDtos.add(NoticeMapper.toNoticeDto(notice, quadrant));
    }

    return ResponseEntity.ok(noticeRequestDtos);
  }

  @Override
  public ResponseEntity<Void> putNotice(Long id, NoticeRequestDto noticeRequestDto) {

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
    Coordinate coordinate = new Coordinate(noticeRequestDto.getCoordinates().getLon(),
        noticeRequestDto.getCoordinates().getLat());

    noticeService.update(id, noticeRequestDto.getBody(), geometryFactory.createPoint(coordinate));

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
