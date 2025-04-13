package co.uk.stefanpuia.backupr.config.reader.dto;

import static co.uk.stefanpuia.backupr.config.reader.dto.BackuprConfigDto.VALID_IDENTIFIER_REGEX;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;

public interface IdentifiableConfigDto {
  @Nullable
  @Pattern(regexp = VALID_IDENTIFIER_REGEX)
  String getName();
}
