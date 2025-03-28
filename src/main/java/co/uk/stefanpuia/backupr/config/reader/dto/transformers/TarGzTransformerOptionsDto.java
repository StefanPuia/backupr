package co.uk.stefanpuia.backupr.config.reader.dto.transformers;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableTarGzTransformerOptionsDto.Builder.class)
public abstract class TarGzTransformerOptionsDto implements TransformerOptionsDto {
  @NotBlank
  @Value.Default
  public String getFilenamePattern() {
    return "<context.sourceName>-<context.now>.tar.gz";
  }
}
