package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableGitConfigRemoteDto.Builder.class)
public abstract class GitConfigRemoteDto implements ConfigRemoteWithCredentialsDto {
  @NotBlank
  @SupportsTemplate
  public abstract String getUrl();

  @NotBlank
  @SupportsTemplate
  public abstract String getBranch();
}
