package es.udc.fireproject.backend.model.services.notice;

import es.udc.fireproject.backend.model.entities.image.Image;
import es.udc.fireproject.backend.model.entities.image.ImageRepository;
import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeRepository;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.entities.user.UserRepository;
import es.udc.fireproject.backend.model.exceptions.*;
import es.udc.fireproject.backend.model.services.utils.ConstraintValidator;
import es.udc.fireproject.backend.rest.common.FileUploadUtil;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public Notice create(String body, Point location) {
        Notice notice = new Notice(body, NoticeStatus.PENDING, location);
        notice.setCreatedAt(LocalDateTime.now());

        ConstraintValidator.validate(notice);

        return noticeRepository.save(notice);
    }

    @Override
    public Notice create(String body, Point location, Long userId) throws InstanceNotFoundException {
        Notice notice = new Notice(body, NoticeStatus.PENDING, location);

        User user = userRepository.findById(userId).orElseThrow(() -> new InstanceNotFoundException(User.class.getSimpleName(), userId));

        notice.setUser(user);
        notice.setCreatedAt(LocalDateTime.now());

        ConstraintValidator.validate(notice);

        return noticeRepository.save(notice);
    }

    @Override
    public Notice update(Long id, String body, Point location) throws NoticeUpdateStatusException, InstanceNotFoundException {

        Notice notice = noticeRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));

        if (notice.getStatus() != NoticeStatus.PENDING) {
            throw new NoticeUpdateStatusException(notice.getId(), notice.getStatus().toString());
        }
        notice.setBody(body);
        notice.setLocation(location);

        ConstraintValidator.validate(notice);
        return noticeRepository.save(notice);
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws InstanceNotFoundException, NoticeDeleteStatusException, IOException {


        Notice notice = noticeRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));

        if (notice.getStatus() != NoticeStatus.PENDING) {
            throw new NoticeDeleteStatusException(notice.getId(), notice.getStatus().toString());
        }

        List<Image> imageList = notice.getImageList();
        if (imageList != null && !imageList.isEmpty()) {
            Image image = imageList.get(0);
            if (image != null) {
                String uploadDir = "public/images/" + notice.getId();
                FileUploadUtil.deleteFile(uploadDir, image.getName());
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
    public Notice findById(Long id) throws InstanceNotFoundException {

        return noticeRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));
    }

    @Override
    public List<Notice> findAll() {

        return noticeRepository.findAll();
    }

    @Override
    public void checkNotice(Long id, NoticeStatus status) throws InstanceNotFoundException, NoticeCheckStatusException {

        Notice notice = noticeRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));


        if (notice.getStatus() == NoticeStatus.REJECTED || notice.getStatus() == NoticeStatus.ACCEPTED) {
            throw new NoticeCheckStatusException(notice.getId(), notice.getStatus().toString());
        }
        notice.setStatus(status);
        noticeRepository.save(notice);
    }

    @Override
    public Notice addImage(Long id, String name) throws InstanceNotFoundException, ImageAlreadyUploadedException {
        Notice notice = noticeRepository.findById(id).orElseThrow(() -> new InstanceNotFoundException(Notice.class.getSimpleName(), id));


        Image image = imageRepository.findByName(name);
        if (image != null) {
            throw new ImageAlreadyUploadedException(Image.class.getSimpleName(), image.getId().toString());
        }

        image = new Image(notice, name);
        imageRepository.save(image);

        return notice;

    }

}
