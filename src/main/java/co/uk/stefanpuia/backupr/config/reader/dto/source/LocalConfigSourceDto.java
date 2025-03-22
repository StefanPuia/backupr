package co.uk.stefanpuia.backupr.config.reader.dto.source;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableLocalConfigSourceDto.Builder.class)
public interface LocalConfigSourceDto extends ConfigSourceDto {
  @NotBlank
  @SupportsTemplate
  String getDirectory();

  List<@NotBlank @SupportsTemplate String> getFiles();
}
