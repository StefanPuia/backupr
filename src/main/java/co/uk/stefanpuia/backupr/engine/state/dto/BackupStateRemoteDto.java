package co.uk.stefanpuia.backupr.engine.state.dto;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableBackupStateRemoteDto.Builder.class)
public interface BackupStateRemoteDto {
  String getName();

  List<BackupStateBackupDto> getBackups();
}
