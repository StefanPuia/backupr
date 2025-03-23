package co.uk.stefanpuia.backupr.config.reader.dto.source.transformers;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableZipTransformerOptionsDto.Builder.class)
public interface ZipTransformerOptionsDto extends TransformerOptionsDto {}
