package es.udc.fireproject.backend.rest.dtos;

import java.io.Serializable;

public abstract class BaseDto implements Serializable {

    private static final long serialVersionUID = 293432302962143319L;

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

}
