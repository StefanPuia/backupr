package co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableBasicCredentialsDto.Builder.class)
public interface BasicCredentialsDto extends CredentialsDto {
  @NotBlank
  String getUsername();

  @Nullable
  String getPassword();
}
