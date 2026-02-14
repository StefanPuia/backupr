package co.uk.stefanpuia.backupr.config.reader.dto.state;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.MixedRemoteDeserializer;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.MixedRemoteDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableStateDto.Builder.class)
public interface StateDto {
  @JsonDeserialize(using = MixedRemoteDeserializer.class)
  MixedRemoteDto getRemote();
}
