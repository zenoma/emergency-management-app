package es.udc.fireproject.backend.model.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

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
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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
    lenient().when(noticeRepository.save(any())).thenReturn(NoticeOm.withDefaultValues());
    lenient().when(noticeRepository.findById(any())).thenReturn(Optional.of(NoticeOm.withDefaultValues()));
  }

  @Test
  void givenValid_whenCreateNotice_thenCreatedSuccessfully() {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation().getX(),
        defaultNotice.getLocation().getY());

    Assertions.assertEquals(NoticeOm.withDefaultValues(), createdNotice);

    Assertions.assertEquals(NoticeStatus.PENDING, createdNotice.getStatus());

  }

  @Test
  void givenValid_whenUpdateNotice_thenCreatedSuccessfully()
      throws NoticeUpdateStatusException, InstanceNotFoundException {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation().getX(),
        defaultNotice.getLocation().getY());
    Notice noticeUpdated = noticeService.update(createdNotice.getId(), "New body", createdNotice.getLocation().getX(),
        createdNotice.getLocation().getY());

    Assertions.assertEquals(createdNotice, noticeUpdated);

    Assertions.assertEquals(NoticeStatus.PENDING, noticeUpdated.getStatus());

  }

  @Test
  void givenAcceptedNotice_whenUpdateNotice_thenNoticeStatusException() {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation().getX(),
        defaultNotice.getLocation().getY());
    createdNotice.setStatus(NoticeStatus.ACCEPTED);
    when(noticeRepository.findById(any())).thenReturn(Optional.of(createdNotice));

    Notice finalNotice = createdNotice;
    Assertions.assertThrows(NoticeUpdateStatusException.class,
        () -> noticeService.update(finalNotice.getId(), finalNotice.getBody(), finalNotice.getLocation().getX(),
            finalNotice.getLocation().getY()),
        "NoticeStatusException must be thrown");

  }

  @Test
  void givenNotCreated_whenUpdateNotice_thenInstanceNotFound() {

    when(noticeRepository.findById(any())).thenReturn(Optional.empty());
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.update(defaultNotice.getId(), defaultNotice.getBody(), defaultNotice.getLocation().getX(),
            defaultNotice.getLocation().getY()),
        "InstanceNotFoundException must be thrown");

  }

  @Test
  void givenValid_whenDeleteNotice_thenDeletedSuccessfully()
      throws InstanceNotFoundException, NoticeDeleteStatusException, IOException {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation().getX(),
        defaultNotice.getLocation().getY());
    noticeService.deleteById(createdNotice.getId());

    when(noticeRepository.findById(any())).thenReturn(Optional.empty());

    Notice finalNotice = createdNotice;
    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.findById(finalNotice.getId()),
        "InstanceNotFoundException must be thrown");

  }

  @Test
  void givenNotCreated_whenDeleteNotice_thenInstanceNotFoundException() {

    when(noticeRepository.findById(any())).thenReturn(Optional.empty());

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.deleteById(-1L),
        "InstanceNotFoundException must be thrown");

  }

  @Test
  void givenAcceptedNotice_whenDeleteNotice_thenNoticeDeleteStatusException() {

    defaultNotice.setStatus(NoticeStatus.ACCEPTED);
    when(noticeRepository.findById(any())).thenReturn(Optional.of(defaultNotice));

    Assertions.assertThrows(NoticeDeleteStatusException.class,
        () -> noticeService.deleteById(defaultNotice.getId()),
        "NoticeStatusException must be thrown");

  }

  @Test
  void givenValidNotice_whenCheckNotice_thenStatusChanged()
      throws NoticeCheckStatusException, InstanceNotFoundException {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation().getX(),
        defaultNotice.getLocation().getY());

    noticeService.checkNotice(createdNotice.getId(), NoticeStatus.ACCEPTED);
    createdNotice.setStatus(NoticeStatus.ACCEPTED);
    when(noticeRepository.findById(any())).thenReturn(Optional.of(createdNotice));

    Assertions.assertEquals(NoticeStatus.ACCEPTED, noticeService.findById(createdNotice.getId()).getStatus(),
        "Status must be Accepted");


  }

  @Test
  void givenInvalidNotice_whenCheckNotice_thenInstanceNotFoundException() {
    when(noticeRepository.findById(any())).thenReturn(Optional.empty());

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.checkNotice(-1L, NoticeStatus.ACCEPTED),
        "InstanceNotFoundException must be thrown");


  }

  @Test
  void givenAcceptedNotice_whenCheckNotice_thenNoticeCheckStatusException() {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(), defaultNotice.getLocation().getX(),
        defaultNotice.getLocation().getY());

    createdNotice.setStatus(NoticeStatus.ACCEPTED);

    Notice finalNotice = createdNotice;
    when(noticeRepository.findById(any())).thenReturn(Optional.of(finalNotice));
    Assertions.assertThrows(NoticeCheckStatusException.class,
        () -> noticeService.checkNotice(finalNotice.getId(), NoticeStatus.ACCEPTED),
        "NoticeStatusException must be thrown");

  }

}
