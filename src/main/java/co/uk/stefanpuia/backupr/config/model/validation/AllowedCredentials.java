package co.uk.stefanpuia.backupr.config.model.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import co.uk.stefanpuia.backupr.config.model.credentials.CredentialsType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = {AllowedCredentialsValidator.class})
public @interface AllowedCredentials {
  String message() default "provided credentials are not allowed for this remote";

  CredentialsType[] value();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
