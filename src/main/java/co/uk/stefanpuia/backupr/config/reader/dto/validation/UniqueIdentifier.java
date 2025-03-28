package co.uk.stefanpuia.backupr.config.reader.dto.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = {UniqueIdentifierValidator.class})
public @interface UniqueIdentifier {
  String message() default "configuration names must be unique";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
