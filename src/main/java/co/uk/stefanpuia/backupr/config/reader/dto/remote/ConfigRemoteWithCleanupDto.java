package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import co.uk.stefanpuia.backupr.config.reader.dto.cleanup.MixedCleanupDeserializer;
import co.uk.stefanpuia.backupr.config.reader.dto.cleanup.MixedCleanupDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

public interface ConfigRemoteWithCleanupDto extends ConfigRemoteDto {
  @Valid
  @Nullable
  @JsonDeserialize(using = MixedCleanupDeserializer.class)
  MixedCleanupDto getCleanup();
}
