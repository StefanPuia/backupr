package co.uk.stefanpuia.backupr.engine.state.dto;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.List;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableBackupStateBackupDto.Builder.class)
public interface BackupStateBackupDto {
  String getSourceName();

  Instant getTimestamp();

  List<String> getFiles();

  boolean isOverwrites();
}
