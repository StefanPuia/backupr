package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials.CredentialsDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableGitConfigRemoteDto.Builder.class)
public interface GitConfigRemoteDto extends ConfigRemoteDto {
  @NotBlank
  @SupportsTemplate
  String getUrl();

  @NotBlank
  @SupportsTemplate
  String getBranch();

  @Valid
  @Nullable
  CredentialsDto getCredentials();
}
