package co.uk.stefanpuia.backupr.config.reader.dto.cleanup;

import co.uk.stefanpuia.backupr.config.reader.dto.IdentifiableConfigDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = KeepAllCleanupDto.class)
@JsonSubTypes({@JsonSubTypes.Type(value = ParameterizedCleanupDto.class)})
public interface CleanupDto extends IdentifiableConfigDto {
  @Override
  @Nullable
  String getName();

  @Nullable
  Boolean isDisabled();
}
