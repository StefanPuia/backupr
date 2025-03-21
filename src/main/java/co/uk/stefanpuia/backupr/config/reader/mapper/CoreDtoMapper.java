package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.core.MapstructConfig;
import jakarta.annotation.Nullable;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(config = MapstructConfig.class)
public abstract class CoreDtoMapper {

  @Named("mapDisabledToEnabled")
  protected boolean mapDisabledToEnabled(final @Nullable Boolean disabled) {
    final var isDisabled = Optional.ofNullable(disabled).orElse(false);
    return !isDisabled;
  }
}
