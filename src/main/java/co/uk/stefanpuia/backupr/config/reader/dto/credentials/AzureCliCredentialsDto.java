package co.uk.stefanpuia.backupr.config.reader.dto.credentials;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableAzureCliCredentialsDto.Builder.class)
public interface AzureCliCredentialsDto extends CredentialsDto {
  @Nullable
  @SupportsTemplate
  String getTenantId();

  @AssertTrue
  boolean isCli();
}
