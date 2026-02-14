package co.uk.stefanpuia.backupr.engine.state.dto;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.core.InstantSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.util.Map;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableBackupStateDto.Builder.class)
public interface BackupStateDto {
  @Value.Default
  default int getVersion() {
    return 1;
  }

  @Value.Default
  @JsonSerialize(using = InstantSerializer.class)
  default Instant getLastBackup() {
    return Instant.now();
  }

  Map<String, BackupStateRemoteDto> getRemotes();
}
