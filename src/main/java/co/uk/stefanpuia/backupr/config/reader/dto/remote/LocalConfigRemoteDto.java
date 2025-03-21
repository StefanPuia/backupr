package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableLocalConfigRemoteDto.Builder.class)
public interface LocalConfigRemoteDto extends ConfigRemoteDto {
  @NotBlank
  String getLocation();
}
