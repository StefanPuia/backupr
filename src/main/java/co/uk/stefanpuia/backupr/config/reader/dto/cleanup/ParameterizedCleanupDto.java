package co.uk.stefanpuia.backupr.config.reader.dto.cleanup;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableParameterizedCleanupDto.Builder.class)
public interface ParameterizedCleanupDto extends CleanupDto {
  Integer getKeepCount();

  Integer getKeepDays();
}
