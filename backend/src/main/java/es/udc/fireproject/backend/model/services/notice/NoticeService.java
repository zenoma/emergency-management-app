package es.udc.fireproject.backend.model.services.notice;

import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import es.udc.fireproject.backend.model.exceptions.*;
import org.locationtech.jts.geom.Point;

import java.io.IOException;
import java.util.List;

public interface NoticeService {


    Notice create(String body, Point location);

    Notice create(String body, Point location, Long userId) throws InstanceNotFoundException;

    Notice update(Long id, String body, Point location) throws NoticeUpdateStatusException, InstanceNotFoundException;

    void deleteById(Long noticeId) throws InstanceNotFoundException, NoticeDeleteStatusException, IOException;

    List<Notice> findByUserId(Long userId);

    Notice findById(Long id) throws InstanceNotFoundException;

    List<Notice> findAll();

    void checkNotice(Long id, NoticeStatus status) throws InstanceNotFoundException, NoticeCheckStatusException;

    Notice addImage(Long id, String name) throws InstanceNotFoundException, ImageAlreadyUploadedException;

}
