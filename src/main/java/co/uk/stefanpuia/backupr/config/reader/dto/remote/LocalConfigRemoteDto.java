package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import co.uk.stefanpuia.backupr.config.model.remote.RemoteType;
import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableLocalConfigRemoteDto.Builder.class)
public abstract class LocalConfigRemoteDto implements ConfigRemoteDto, ConfigRemoteWithCleanupDto {
  @NotBlank
  @SupportsTemplate
  public abstract String getLocation();

  @Override
  public RemoteType getType() {
    return RemoteType.LOCAL;
  }
}
