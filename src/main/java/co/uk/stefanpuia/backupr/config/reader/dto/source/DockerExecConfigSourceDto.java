package co.uk.stefanpuia.backupr.config.reader.dto.source;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import co.uk.stefanpuia.backupr.config.reader.dto.SupportsTemplate;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableDockerExecConfigSourceDto.Builder.class)
public interface DockerExecConfigSourceDto extends ConfigSourceDto {
  @NotBlank
  @SupportsTemplate
  String getContainer();

  @Size(min = 1)
  List<@Valid CommandDto> getCommands();

  @Value.Default
  default Long getTimeoutInSeconds() {
    return 60L;
  }

  @Value.Default
  default Boolean isAllowNotFoundPaths() {
    return false;
  }

  record CommandDto(
      @NotBlank @SupportsTemplate String file,
      @Size(min = 1) List<@NotBlank @SupportsTemplate String> command) {}
}
