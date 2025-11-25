package es.udc.fireproject.backend.utils;

import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class NoticeOm {

  private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

  public static Notice withDefaultValues() {
    return new Notice(1L, "Default Body",
        NoticeStatus.PENDING,
        UserOM.withDefaultValues(),
        geometryFactory.createPoint(new Coordinate(-45, 45)),
        LocalDateTime.of(2021, 01, 01, 10, 00),
        Collections.emptyList());
  }

  public static Notice withDefaultValues(Long id) {
    return new Notice(id, "Default Body",
        NoticeStatus.PENDING,
        UserOM.withDefaultValues(),
        geometryFactory.createPoint(new Coordinate(-45, 45)),
        LocalDateTime.of(2021, 01, 01, 10, 00),
        Collections.emptyList());
  }

  public static Notice withDefaultValuesAndNoUser(Long id) {
    return new Notice(id, "Default Body",
        NoticeStatus.PENDING,
        null,
        geometryFactory.createPoint(new Coordinate(-45, 45)),
        LocalDateTime.of(2021, 01, 01, 10, 00),
        Collections.emptyList());
  }

  public static Notice withInvalidValues() {
    return new Notice(-1L, "",
        null,
        null,
        null,
        null,
        null);
  }

}
