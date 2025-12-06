package es.udc.fireproject.backend.model.services.notice;

import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import es.udc.fireproject.backend.model.exceptions.ImageAlreadyUploadedException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.NoticeCheckStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeDeleteStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeUpdateStatusException;
import java.util.List;
import org.locationtech.jts.geom.Point;

public interface NoticeService {


  Notice create(String body, Point location);

  Notice create(String body, Point location, Long userId) throws InstanceNotFoundException;

  Notice update(Long id, String body, Point location) throws NoticeUpdateStatusException, InstanceNotFoundException;

  void deleteById(Long noticeId) throws InstanceNotFoundException, NoticeDeleteStatusException;

  List<Notice> findByUserId(Long userId);

  Notice findById(Long id) throws InstanceNotFoundException;

  List<Notice> findAll();

  void checkNotice(Long id, NoticeStatus status) throws InstanceNotFoundException, NoticeCheckStatusException;

  Notice addImage(Long id, String name) throws InstanceNotFoundException, ImageAlreadyUploadedException;

}
