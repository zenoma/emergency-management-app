package es.udc.fireproject.backend.utils;

import es.udc.fireproject.backend.model.entities.notice.Notice;
import es.udc.fireproject.backend.model.entities.notice.NoticeStatus;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

public class NoticeOm {
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 25829);

    public static Notice withDefaultValues() {
        return new Notice("Default Body", NoticeStatus.PENDING, geometryFactory.createPoint(new Coordinate(-45, 45)));
    }

    public static Notice withInvalidValues() {
        return new Notice("", NoticeStatus.ACCEPTED, null);
    }

}
