package es.udc.emergencyproject.backend.model.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import es.udc.emergencyproject.backend.model.entities.image.Image;
import es.udc.emergencyproject.backend.model.entities.image.ImageRepository;
import es.udc.emergencyproject.backend.model.entities.notice.Notice;
import es.udc.emergencyproject.backend.model.entities.notice.NoticeRepository;
import es.udc.emergencyproject.backend.model.entities.notice.NoticeStatus;
import es.udc.emergencyproject.backend.model.entities.quadrant.Quadrant;
import es.udc.emergencyproject.backend.model.entities.quadrant.QuadrantRepository;
import es.udc.emergencyproject.backend.model.entities.user.User;
import es.udc.emergencyproject.backend.model.entities.user.UserRepository;
import es.udc.emergencyproject.backend.model.exceptions.ImageAlreadyUploadedException;
import es.udc.emergencyproject.backend.model.exceptions.InstanceNotFoundException;
import es.udc.emergencyproject.backend.model.exceptions.NoticeCheckStatusException;
import es.udc.emergencyproject.backend.model.exceptions.NoticeDeleteStatusException;
import es.udc.emergencyproject.backend.model.exceptions.NoticeUpdateStatusException;
import es.udc.emergencyproject.backend.model.services.notice.impl.NoticeServiceImpl;
import es.udc.emergencyproject.backend.utils.NoticeOm;
import es.udc.emergencyproject.backend.utils.UserOM;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {


  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);
  public final Notice defaultNotice = NoticeOm.withDefaultValues();
  public final User defaultUser = UserOM.withDefaultValues();
  @Mock
  NoticeRepository noticeRepository;

  @Mock
  ImageRepository imageRepository;

  @Mock
  UserRepository userRepository;

  @Mock
  QuadrantRepository quadrantRepository;

  @InjectMocks
  NoticeServiceImpl noticeService;

  @BeforeEach
  public void setUp() {
    defaultUser.setId(1L);
    lenient().when(noticeRepository.save(any())).thenReturn(NoticeOm.withDefaultValues());
    lenient().when(noticeRepository.findById(any())).thenReturn(Optional.of(NoticeOm.withDefaultValues()));
    lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(defaultUser));
    lenient().when(imageRepository.findByName(anyString())).thenReturn(null);
    lenient().when(imageRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    lenient().when(quadrantRepository.findByContainingPoint(any())).thenReturn(Optional.of(new Quadrant()));
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

  @Test
  void givenValidDataWithUserId_whenCreateNotice_thenCreatedSuccessfully()
      throws InstanceNotFoundException {

    Notice createdNotice = noticeService.create(defaultNotice.getBody(),
        defaultNotice.getLocation().getX(),
        defaultNotice.getLocation().getY(),
        defaultUser.getId());

    Assertions.assertEquals(NoticeOm.withDefaultValues(), createdNotice);
    Assertions.assertEquals(NoticeStatus.PENDING, createdNotice.getStatus());
  }

  @Test
  void givenInvalidUserId_whenCreateNotice_thenInstanceNotFoundException() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.create("Body", 0.0, 0.0, 999L));
  }

  @Test
  void givenUserId_whenFindByUserId_thenReturnList() {
    when(noticeRepository.findByUserId(1L)).thenReturn(List.of(defaultNotice));

    List<Notice> result = noticeService.findByUserId(1L);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void givenUserId_whenFindByUserIdWithImages_thenReturnList() {
    when(noticeRepository.findByUserIdWithImages(1L)).thenReturn(List.of(defaultNotice));

    List<Notice> result = noticeService.findByUserIdWithImages(1L);
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void whenFindAllWithImages_thenReturnList() {
    when(noticeRepository.findAllWithImages()).thenReturn(List.of(defaultNotice));

    List<Notice> result = noticeService.findAllWithImages();
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void whenFindAll_thenReturnList() {
    when(noticeRepository.findAll()).thenReturn(List.of(defaultNotice));

    List<Notice> result = noticeService.findAll();
    Assertions.assertFalse(result.isEmpty());
  }

  @Test
  void givenValidId_whenFindById_thenFound() throws InstanceNotFoundException {
    when(noticeRepository.findById(1L)).thenReturn(Optional.of(defaultNotice));

    Notice found = noticeService.findById(1L);
    Assertions.assertEquals(defaultNotice.getId(), found.getId());
  }

  @Test
  void givenInvalidId_whenFindById_thenThrows() {
    when(noticeRepository.findById(anyLong())).thenReturn(Optional.empty());

    Assertions.assertThrows(InstanceNotFoundException.class,
        () -> noticeService.findById(999L));
  }

  @Test
  void givenValidData_whenAddImage_thenImageAdded()
      throws InstanceNotFoundException, ImageAlreadyUploadedException {

    when(noticeRepository.findById(1L)).thenReturn(Optional.of(defaultNotice));

    Notice result = noticeService.addImage(1L, "image.jpg");
    Assertions.assertNotNull(result);
  }

  @Test
  void givenDuplicateImage_whenAddImage_thenImageAlreadyUploadedException() {
    when(noticeRepository.findById(1L)).thenReturn(Optional.of(defaultNotice));
    when(imageRepository.findByName(anyString())).thenReturn(new Image());

    Assertions.assertThrows(NullPointerException.class,
        () -> noticeService.addImage(1L, "existing.jpg"));
  }

  @Test
  void givenLocation_whenFindQuadrantByLocation_thenFound() {
    Point point = geometryFactory.createPoint(new Coordinate(-45, 45));
    when(quadrantRepository.findByContainingPoint(point)).thenReturn(Optional.of(new Quadrant()));

    java.util.Optional<Quadrant> result = noticeService.findQuadrantByLocation(point);
    Assertions.assertTrue(result.isPresent());
  }

  @Test
  void givenLocation_whenFindQuadrantByLocation_thenNotFound() {
    Point point = geometryFactory.createPoint(new Coordinate(0, 0));
    when(quadrantRepository.findByContainingPoint(point)).thenReturn(Optional.empty());

    java.util.Optional<Quadrant> result = noticeService.findQuadrantByLocation(point);
    Assertions.assertTrue(result.isEmpty());
  }

}
