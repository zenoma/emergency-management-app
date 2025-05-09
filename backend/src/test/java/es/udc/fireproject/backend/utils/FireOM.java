package es.udc.fireproject.backend.utils;

import es.udc.fireproject.backend.model.entities.fire.Fire;
import es.udc.fireproject.backend.model.entities.fire.FireIndex;

public class FireOM {

    public static Fire withDefaultValues() {

        return new Fire("Description", "Type 1", FireIndex.UNO);
    }
}
