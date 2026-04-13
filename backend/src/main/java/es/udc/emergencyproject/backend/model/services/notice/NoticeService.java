package es.udc.emergencyproject.backend.model.services.notice;

import es.udc.emergencyproject.backend.model.entities.notice.Notice;
import es.udc.emergencyproject.backend.model.entities.notice.NoticeStatus;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.exceptions.ImageAlreadyUploadedException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.NoticeCheckStatusException;
import es.udc.emergencyproject.backend.model.exceptions.NoticeDeleteStatusException;
import es.udc.emergencyproject.backend.model.exceptions.NoticeUpdateStatusException;
import java.util.List;
import java.util.Optional;
import org.locationtech.jts.geom.Point;

public interface NoticeService {

  Notice create(String body, double lon, double lat);

  Notice create(String body, double lon, double lat, Long userId) throws InstanceNotFoundException;

  Notice update(Long id, String body, double lon, double lat)
      throws NoticeUpdateStatusException, InstanceNotFoundException;

  void deleteById(Long noticeId) throws InstanceNotFoundException, NoticeDeleteStatusException;

  List<Notice> findByUserId(Long userId);

  List<Notice> findByUserIdWithImages(Long userId);

  List<Notice> findAllWithImages();

  Notice findById(Long id) throws InstanceNotFoundException;

  List<Notice> findAll();

  void checkNotice(Long id, NoticeStatus status) throws InstanceNotFoundException, NoticeCheckStatusException;

  Notice addImage(Long id, String name) throws InstanceNotFoundException, ImageAlreadyUploadedException;

  Optional<Quadrant> findQuadrantByLocation(Point location);

}
