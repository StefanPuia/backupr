package co.uk.stefanpuia.backupr.config.reader.dto.source;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableLocalConfigSourceDto.Builder.class)
public interface LocalConfigSourceDto extends ConfigSourceDto {
  @NotBlank
  String getDirectory();

  List<@NotBlank String> getFiles();
}
