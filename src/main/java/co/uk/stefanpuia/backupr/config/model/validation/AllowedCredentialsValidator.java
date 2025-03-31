package co.uk.stefanpuia.backupr.config.model.validation;

import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.credentials.CredentialsType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class AllowedCredentialsValidator
    implements ConstraintValidator<AllowedCredentials, Credentials> {
  private CredentialsType[] allowedCredentialTypes = new CredentialsType[] {};

  @Override
  public void initialize(final AllowedCredentials constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    allowedCredentialTypes = constraintAnnotation.value();
  }

  @Override
  public boolean isValid(final Credentials value, final ConstraintValidatorContext context) {
    return Arrays.asList(allowedCredentialTypes).contains(value.getType());
  }
}
