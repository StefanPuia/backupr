package co.uk.stefanpuia.backupr.config.reader.dto.cleanup;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableKeepAllCleanupDto.Builder.class)
public interface KeepAllCleanupDto extends CleanupDto {}
