package es.udc.fireproject.backend.model.services;

import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeRepository;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import es.udc.fireproject.backend.model.entities.user.UserRepository;
import es.udc.fireproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.fireproject.backend.model.exceptions.NoticeCheckStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeDeleteStatusException;
import es.udc.fireproject.backend.model.exceptions.NoticeUpdateStatusException;
import es.udc.fireproject.backend.model.services.notice.NoticeServiceImpl;
import es.udc.fireproject.backend.utils.NoticeOm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Optional;

@SpringBootTest
@Transactional
class NoticeServiceImplTest {


  public final Notice defaultNotice = NoticeOm.withDefaultValues();
  @Mock
  NoticeRepository noticeRepository;

  @Mock
  UserRepository userRepository;

  @InjectMocks
  NoticeServiceImpl noticeService;

  @BeforeEach
  public void setUp() {

    Mockito.when(noticeRepository.save(Mockito.any())).thenReturn(NoticeOm.withDefaultValues());
    Mockito.when(noticeRepository.findById(Mockito.any())).thenReturn(Optional.of(NoticeOm.withDefaultValues()));
  }

  @Test
  void givenValid_whenCreateNotice_thenCreatedSuccessfully() {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation());

    Assertions.assertEquals(NoticeOm.withDefaultValues(), createdNotice);

    Assertions.assertEquals(NoticeStatus.PENDING, createdNotice.getStatus());

  }

  @Test
  void givenInvalid_whenCreateNotice_thenConstraintViolationException() {
    String body = defaultNotice.getBody();
    Assertions.assertThrows(ConstraintViolationException.class,
        () -> noticeService.create(body,
            null),
        "ConstraintViolationException must be thrown");

  }

  @Test
  void givenValid_whenUpdateNotice_thenCreatedSuccessfully()
      throws NoticeUpdateStatusException, InstanceNotFoundException {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation());
    Notice noticeUpdated = noticeService.update(createdNotice.getId(), "New body", createdNotice.getLocation());

    Assertions.assertEquals(createdNotice, noticeUpdated);

    Assertions.assertEquals(NoticeStatus.PENDING, noticeUpdated.getStatus());

  }

  @Test
  void givenAcceptedNotice_whenUpdateNotice_thenNoticeStatusException() {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation());
    createdNotice.setStatus(NoticeStatus.ACCEPTED);
    Mockito.when(noticeRepository.findById(Mockito.any())).thenReturn(Optional.of(createdNotice));

    Notice finalNotice = createdNotice;
    Assertions.assertThrows(NoticeUpdateStatusException.class,
        () -> noticeService.update(finalNotice.getId(), finalNotice.getBody(), finalNotice.getLocation()),
        "NoticeStatusException must be thrown");

  }

  @Test
  void givenNotCreated_whenUpdateNotice_thenInstanceNotFound() {

    Mockito.when(noticeRepository.findById(Mockito.any())).thenReturn(Optional.empty());
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.update(defaultNotice.getId(), defaultNotice.getBody(), defaultNotice.getLocation()),
        "InstanceNotFoundException must be thrown");

  }

  @Test
  void givenValid_whenDeleteNotice_thenDeletedSuccessfully()
      throws InstanceNotFoundException, NoticeDeleteStatusException, IOException {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation());
    noticeService.deleteById(createdNotice.getId());

    Mockito.when(noticeRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    Notice finalNotice = createdNotice;
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.findById(finalNotice.getId()),
        "InstanceNotFoundException must be thrown");

  }

  @Test
  void givenNotCreated_whenDeleteNotice_thenInstanceNotFoundException() {

    Mockito.when(noticeRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.deleteById(-1L),
        "InstanceNotFoundException must be thrown");

  }

  @Test
  void givenAcceptedNotice_whenDeleteNotice_thenNoticeDeleteStatusException() {

    defaultNotice.setStatus(NoticeStatus.ACCEPTED);
    Mockito.when(noticeRepository.findById(Mockito.any())).thenReturn(Optional.of(defaultNotice));

    Assertions.assertThrows(NoticeDeleteStatusException.class,
        () -> noticeService.deleteById(defaultNotice.getId()),
        "NoticeStatusException must be thrown");

  }

  @Test
  void givenValidNotice_whenCheckNotice_thenStatusChanged()
      throws NoticeCheckStatusException, InstanceNotFoundException {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation());

    noticeService.checkNotice(createdNotice.getId(), NoticeStatus.ACCEPTED);
    createdNotice.setStatus(NoticeStatus.ACCEPTED);
    Mockito.when(noticeRepository.findById(Mockito.any())).thenReturn(Optional.of(createdNotice));

    Assertions.assertEquals(NoticeStatus.ACCEPTED, noticeService.findById(createdNotice.getId()).getStatus(),
        "Status must be Accepted");


  }

  @Test
  void givenInvalidNotice_whenCheckNotice_thenInstanceNotFoundException() {
    Mockito.when(noticeRepository.findById(Mockito.any())).thenReturn(Optional.empty());

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.checkNotice(-1L, NoticeStatus.ACCEPTED),
        "InstanceNotFoundException must be thrown");


  }

  @Test
  void givenAcceptedNotice_whenCheckNotice_thenNoticeCheckStatusException() {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation());

    createdNotice.setStatus(NoticeStatus.ACCEPTED);

    Notice finalNotice = createdNotice;
    Mockito.when(noticeRepository.findById(Mockito.any())).thenReturn(Optional.of(finalNotice));
    Assertions.assertThrows(NoticeCheckStatusException.class,
        () -> noticeService.checkNotice(finalNotice.getId(), NoticeStatus.ACCEPTED),
        "NoticeStatusException must be thrown");

  }

}
