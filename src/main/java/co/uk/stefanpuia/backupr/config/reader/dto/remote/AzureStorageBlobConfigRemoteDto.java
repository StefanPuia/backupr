package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import co.uk.stefanpuia.backupr.config.model.remote.RemoteType;
import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableAzureStorageBlobConfigRemoteDto.Builder.class)
public abstract class AzureStorageBlobConfigRemoteDto
    implements ConfigRemoteWithCredentialsDto, ConfigRemoteWithCleanupDto {
  @NotBlank
  @SupportsTemplate
  public abstract String getEndpoint();

  @NotBlank
  @SupportsTemplate
  public abstract String getContainer();

  @Value.Default
  public boolean isOverwrite() {
    return false;
  }

  @NotBlank
  @Value.Default
  @SupportsTemplate
  public String getBlobPrefixPattern() {
    return "<context.sourceName>/<context.nowYear>/<context.nowMonth>/<context.nowDay>";
  }

  @Override
  public RemoteType getType() {
    return RemoteType.AZURE_STORAGE_BLOB;
  }
}
