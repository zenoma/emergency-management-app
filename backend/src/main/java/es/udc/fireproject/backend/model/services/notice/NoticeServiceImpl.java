package es.udc.fireproject.backend.model.services.notice;

import es.udc.fireproject.backend.model.entities.image.Image;
import es.udc.fireproject.backend.model.entities.image.ImageRepository;
import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeRepository;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import es.udc.fireproject.backend.model.entities.quadrant.Quadrant;
import es.udc.fireproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRepository;
import es.udc.fireproject.backend.model.exceptions.FileUploadException;
import es.udc.fireproject.backend.model.exceptions.ImageAlreadyUploadedException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.NoticeCheckStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeDeleteStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeUpdateStatusException;
import es.udc.fireproject.backend.model.services.utils.ConstraintValidator;
import es.udc.fireproject.backend.util.FileUploadUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

  private final NoticeRepository noticeRepository;
  private final ImageRepository imageRepository;
  private final UserRepository userRepository;
  private final QuadrantRepository quadrantRepository;

  @Override
  public Notice create(String body, double lon, double lat) {

    Notice notice = new Notice();
    notice.setStatus(NoticeStatus.PENDING);
    notice.setBody(body);

    Point location = locationFromCoordinates(lon, lat);
    notice.setLocation(location);

    notice.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    notice.setImageList(Collections.emptyList());

    ConstraintValidator.validate(notice);

    return noticeRepository.save(notice);
  }

  @Override
  public Notice create(String body, double lon, double lat, Long userId) throws InstanceNotFoundException {

    Notice notice = new Notice();
    notice.setStatus(NoticeStatus.PENDING);
    notice.setBody(body);

    Point location = locationFromCoordinates(lon, lat);
    notice.setLocation(location);

    notice.setImageList(Collections.emptyList());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new InstanceNotFoundException(User.class.getSimpleName(), userId));

    notice.setUser(user);
    notice.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

    ConstraintValidator.validate(notice);

    return noticeRepository.save(notice);
  }

  @Override
  public Notice update(Long id, String body, double lon, double lat)
      throws NoticeUpdateStatusException, InstanceNotFoundException {

    Notice notice = noticeRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));

    if (notice.getStatus() != NoticeStatus.PENDING) {
      throw new NoticeUpdateStatusException(notice.getId(), notice.getStatus().toString());
    }
    notice.setBody(body);

    Point location = locationFromCoordinates(lon, lat);
    notice.setLocation(location);

    ConstraintValidator.validate(notice);
    return noticeRepository.save(notice);
  }

  @Override
  @Transactional
  public void deleteById(Long id) throws InstanceNotFoundException, NoticeDeleteStatusException {

    Notice notice = noticeRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));

    if (notice.getStatus() != NoticeStatus.PENDING) {
      throw new NoticeDeleteStatusException(notice.getId(), notice.getStatus().toString());
    }

    List<Image> imageList = notice.getImageList();
    if (imageList != null && !imageList.isEmpty()) {
      Image image = imageList.getFirst();
      if (image != null) {
        String uploadDir = "public/images/" + notice.getId();
        try {
          FileUploadUtil.deleteFile(uploadDir, image.getName());
        } catch (IOException e) {
          throw new FileUploadException(e);
        }
        imageRepository.delete(image);
      }
    }

    noticeRepository.deleteById(id);
  }

  @Override
  public List<Notice> findByUserId(Long userId) {
    return noticeRepository.findByUserId(userId);
  }

  @Override
  public List<Notice> findByUserIdWithImages(Long userId) {
    return noticeRepository.findByUserIdWithImages(userId);
  }

  @Override
  public List<Notice> findAllWithImages() {
    return noticeRepository.findAllWithImages();
  }

  @Override
  public Notice findById(Long id) throws InstanceNotFoundException {

    return noticeRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));
  }

  @Override
  public List<Notice> findAll() {

    return noticeRepository.findAll();
  }

  @Override
  public void checkNotice(Long id, NoticeStatus status) throws InstanceNotFoundException, NoticeCheckStatusException {

    Notice notice = noticeRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));

    if (notice.getStatus() == NoticeStatus.REJECTED || notice.getStatus() == NoticeStatus.ACCEPTED) {
      throw new NoticeCheckStatusException(notice.getId(), notice.getStatus().toString());
    }
    notice.setStatus(status);
    noticeRepository.save(notice);
  }

  @Override
  public Notice addImage(Long id, String name) throws InstanceNotFoundException, ImageAlreadyUploadedException {
    Notice notice = noticeRepository.findById(id)
        .orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));

    Image image = imageRepository.findByName(name);
    if (image != null) {
      throw new ImageAlreadyUploadedException(Image.class.getSimpleName(), image.getId().toString());
    }

    image = new Image(notice, name);
    imageRepository.save(image);

    return notice;

  }

  @Override
  public Optional<Quadrant> findQuadrantByLocation(Point location) {
    return quadrantRepository.findByContainingPoint(location);
  }

  private Point locationFromCoordinates(double lon, double lat) {
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
    Coordinate coordinate = new Coordinate(lon, lat);
    return geometryFactory.createPoint(coordinate);
  }
}
