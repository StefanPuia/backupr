package co.uk.stefanpuia.backupr.config.reader.dto.credentials;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableAzureClientSecretCredentialsDto.Builder.class)
public interface AzureClientSecretCredentialsDto extends CredentialsDto {
  @NotBlank
  @SupportsTemplate
  String getTenantId();

  @NotBlank
  @SupportsTemplate
  String getClientId();

  @NotBlank
  @SupportsTemplate
  String getClientSecret();
}
