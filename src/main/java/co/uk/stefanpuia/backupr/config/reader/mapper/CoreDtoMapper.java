package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.core.MapstructConfig;
import jakarta.annotation.Nullable;
import java.util.Optional;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.stringtemplate.v4.ST;

@Mapper(config = MapstructConfig.class)
public abstract class CoreDtoMapper {

  @Named("mapDisabledToEnabled")
  protected boolean mapDisabledToEnabled(final @Nullable Boolean disabled) {
    final var isDisabled = Optional.ofNullable(disabled).orElse(false);
    return !isDisabled;
  }

  @Named("applyTemplateToString")
  protected String applyTemplate(
      final @Nullable String source, final @Context VariablesWrapper variables) {
    final var template = new ST(source);
    variables.variables().forEach(template::add);
    template.add("env", variables.environment());
    return template.render();
  }
}
