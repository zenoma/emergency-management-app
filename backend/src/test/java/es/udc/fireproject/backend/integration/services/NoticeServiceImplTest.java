package es.udc.fireproject.backend.integration.services;

import es.udc.fireproject.backend.IntegrationTest;
import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import es.udc.fireproject.backend.model.entities.user.User;
import es.udc.fireproject.backend.model.exceptions.DuplicateInstanceException;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.NoticeCheckStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeDeleteStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeUpdateStatusException;
import es.udc.fireproject.backend.model.services.notice.NoticeService;
import es.udc.fireproject.backend.model.services.personalmanagement.PersonalManagementService;
import es.udc.fireproject.backend.utils.NoticeOm;
import es.udc.fireproject.backend.utils.UserOM;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;

class NoticeServiceImplTest extends IntegrationTest {


  @Autowired
  NoticeService noticeService;

  @Autowired
  PersonalManagementService personalManagementService;

  @Test
  void givenValid_whenCreateNotice_thenCreatedSuccessfully() throws InstanceNotFoundException {

    Notice notice = NoticeOm.withDefaultValues();
    notice = noticeService.create(notice.getBody(), notice.getLocation());

    Assertions.assertEquals(NoticeOm.withDefaultValues(notice.getId()), notice);

    Assertions.assertEquals(noticeService.findById(notice.getId()), notice);
  }

  @Test
  void givenInvalid_whenCreateNotice_thenConstraintViolationException() {
    Notice notice = NoticeOm.withInvalidValues();
    String body = notice.getBody();
    Point location = notice.getLocation();
    Assertions.assertThrows(ConstraintViolationException.class,
        () -> noticeService.create(body,
            location),
        "ConstraintViolationException must be thrown");

  }

  @Test
  void givenValid_whenUpdateNotice_thenCreatedSuccessfully()
      throws NoticeUpdateStatusException, InstanceNotFoundException {

    Notice notice = NoticeOm.withDefaultValues();
    notice = noticeService.create(notice.getBody(), notice.getLocation());
    Notice noticeUpdated = noticeService.update(notice.getId(), "New body", notice.getLocation());

    Assertions.assertEquals(notice, noticeUpdated);

    Assertions.assertEquals(NoticeStatus.PENDING, notice.getStatus());

  }

  @Test
  void givenAcceptedNotice_whenUpdateNotice_thenNoticeStatusException() {

    Notice notice = NoticeOm.withDefaultValues();
    notice = noticeService.create(notice.getBody(), notice.getLocation());
    notice.setStatus(NoticeStatus.ACCEPTED);

    Notice finalNotice = notice;
    Assertions.assertThrows(NoticeUpdateStatusException.class,
        () -> noticeService.update(finalNotice.getId(), finalNotice.getBody(), finalNotice.getLocation()),
        "NoticeStatusException must be thrown");

  }

  @Test
  void givenNotCreated_whenUpdateNotice_thenInstanceNotFound() {
    Notice notice = NoticeOm.withDefaultValues();

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.update(-1L, "", null),
        "InstanceNotFoundException must be thrown");

  }

  @Test
  void givenValid_whenDeleteNotice_thenDeletedSuccessfully()
      throws NoticeDeleteStatusException, InstanceNotFoundException, IOException {

    Notice notice = NoticeOm.withDefaultValues();
    notice = noticeService.create(notice.getBody(), notice.getLocation());
    noticeService.deleteById(notice.getId());

    Notice finalNotice = notice;
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.findById(finalNotice.getId()),
        "InstanceNotFoundException must be thrown");

  }

  @Test
  void givenNotCreated_whenDeleteNotice_thenInstanceNotFoundException() {

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.deleteById(-1L),
        "InstanceNotFoundException must be thrown");

  }

  @Test
  void givenAcceptedNotice_whenDeleteNotice_thenNoticeDeleteStatusException()
      throws InstanceNotFoundException, NoticeCheckStatusException {

    Notice notice = NoticeOm.withDefaultValues();
    notice = noticeService.create(notice.getBody(), notice.getLocation());
    noticeService.checkNotice(notice.getId(), NoticeStatus.ACCEPTED);

    Notice finalNotice = notice;
    Assertions.assertThrows(NoticeDeleteStatusException.class,
        () -> noticeService.deleteById(finalNotice.getId()),
        "NoticeStatusException must be thrown");

  }

  @Test
  void givenValidNotice_whenCheckNotice_thenStatusChanged()
      throws NoticeCheckStatusException, InstanceNotFoundException {

    Notice notice = NoticeOm.withDefaultValues();

    notice = noticeService.create(notice.getBody(), notice.getLocation());

    noticeService.checkNotice(notice.getId(), NoticeStatus.ACCEPTED);
    notice.setStatus(NoticeStatus.ACCEPTED);

    Assertions.assertEquals(NoticeStatus.ACCEPTED, noticeService.findById(notice.getId()).getStatus(),
        "Status must be Accepted");


  }

  @Test
  void givenInvalidNotice_whenCheckNotice_thenInstanceNotFoundException() {

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.checkNotice(-1L, NoticeStatus.ACCEPTED),
        "InstanceNotFoundException must be thrown");


  }

  @Test
  void givenAcceptedNotice_whenCheckNotice_thenNoticeCheckStatusException() {

    Notice notice = NoticeOm.withDefaultValues();

    notice = noticeService.create(notice.getBody(), notice.getLocation());

    Notice finalNotice = notice;
    notice.setStatus(NoticeStatus.ACCEPTED);
    Assertions.assertThrows(NoticeCheckStatusException.class,
        () -> noticeService.checkNotice(finalNotice.getId(), NoticeStatus.ACCEPTED),
        "NoticeStatusException must be thrown");

  }

  @Test
  void givenValidData_whenFindByUserId_thenNoticesFound() throws InstanceNotFoundException, DuplicateInstanceException {
    Notice notice = NoticeOm.withDefaultValues();
    User user = UserOM.withDefaultValues();
    personalManagementService.signUp(user);

    List<Notice> noticeList = new ArrayList<>();
    noticeList.add(noticeService.create(notice.getBody(), notice.getLocation(), user.getId()));
    notice.setBody("Body2");
    noticeList.add(noticeService.create(notice.getBody(), notice.getLocation(), user.getId()));
    notice.setBody("Body3");
    noticeList.add(noticeService.create(notice.getBody(), notice.getLocation(), user.getId()));

    Assertions.assertTrue(noticeService.findByUserId(user.getId()).containsAll(noticeList), "List must be the same");
  }

  @Test
  void givenValidData_whenFindAll_thenNoticesFound() throws DuplicateInstanceException, InstanceNotFoundException {

    Notice notice = NoticeOm.withDefaultValues();
    User user = UserOM.withDefaultValues();
    personalManagementService.signUp(user);

    List<Notice> noticeList = new ArrayList<>();
    noticeList.add(noticeService.create(notice.getBody(), notice.getLocation(), user.getId()));
    notice.setBody("Body2");
    noticeList.add(noticeService.create(notice.getBody(), notice.getLocation(), user.getId()));
    notice.setBody("Body3");
    noticeList.add(noticeService.create(notice.getBody(), notice.getLocation(), user.getId()));

    Assertions.assertTrue(noticeService.findAll().containsAll(noticeList), "List must be the same");

  }

}