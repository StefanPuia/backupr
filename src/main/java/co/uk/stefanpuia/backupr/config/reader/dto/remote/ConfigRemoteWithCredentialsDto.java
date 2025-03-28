package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import co.uk.stefanpuia.backupr.config.reader.dto.credentials.MixedCredentialsDeserializer;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.MixedCredentialsDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

public interface ConfigRemoteWithCredentialsDto extends ConfigRemoteDto {
  @Valid
  @Nullable
  @JsonDeserialize(using = MixedCredentialsDeserializer.class)
  MixedCredentialsDto getCredentials();
}
