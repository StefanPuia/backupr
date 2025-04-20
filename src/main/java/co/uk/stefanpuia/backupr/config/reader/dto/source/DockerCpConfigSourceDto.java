package co.uk.stefanpuia.backupr.config.reader.dto.source;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableDockerCpConfigSourceDto.Builder.class)
public interface DockerCpConfigSourceDto extends ConfigSourceDto {
  @NotBlank
  @SupportsTemplate
  String getContainer();

  @Size(min = 1)
  List<@NotBlank @SupportsTemplate String> getPaths();

  @Value.Default
  default Boolean isAllowNotFoundPaths() {
    return false;
  }
}
