package co.uk.stefanpuia.backupr.config.reader.dto.source.transformers;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableZipTransformerOptionsDto.Builder.class)
public abstract class ZipTransformerOptionsDto implements TransformerOptionsDto {
  @NotBlank
  @Value.Default
  public String getFilenamePattern() {
    return "<context.sourceName>-<context.now>.zip";
  }
}
