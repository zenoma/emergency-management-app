package es.udc.fireproject.backend.model.services.utils;

import jakarta.validation.*;
import java.util.HashSet;
import java.util.Set;

public class ConstraintValidator {

  private ConstraintValidator() {
  }

  public static <T> void validate(T input) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();

    Set<ConstraintViolation<T>> violations = validator.validate(input);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(new HashSet<>(violations));
    }
  }
}
