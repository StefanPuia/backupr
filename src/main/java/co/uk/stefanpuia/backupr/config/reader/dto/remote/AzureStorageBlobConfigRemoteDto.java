package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableAzureStorageBlobConfigRemoteDto.Builder.class)
public interface AzureStorageBlobConfigRemoteDto extends ConfigRemoteWithCredentialsDto {
  @NotBlank
  @SupportsTemplate
  String getEndpoint();

  @NotBlank
  @SupportsTemplate
  String getContainer();

  @Value.Default
  default boolean isOverwrite() {
    return false;
  }

  @NotBlank
  @Value.Default
  @SupportsTemplate
  default String getBlobPrefixPattern() {
    return "<context.sourceName>/<context.nowYear>/<context.nowMonth>/<context.nowDay>";
  }
}
