package co.uk.stefanpuia.backupr.config.reader.dto.validation;

import co.uk.stefanpuia.backupr.config.reader.dto.IdentifiableConfigDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class UniqueIdentifierValidator
    implements ConstraintValidator<UniqueIdentifier, List<? extends IdentifiableConfigDto>> {
  @Override
  public boolean isValid(
      final List<? extends IdentifiableConfigDto> identifiableConfigDtos,
      final ConstraintValidatorContext constraintValidatorContext) {
    return identifiableConfigDtos.stream().map(IdentifiableConfigDto::getName).distinct().count()
        == identifiableConfigDtos.size();
  }
}
