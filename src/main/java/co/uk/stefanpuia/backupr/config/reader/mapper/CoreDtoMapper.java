package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.core.MapstructConfig;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = MapstructConfig.class)
public abstract class CoreDtoMapper {
  @Autowired private StringTemplateRenderer stringTemplateRenderer;

  @Named("mapDisabledToEnabled")
  protected boolean mapDisabledToEnabled(final @Nullable Boolean disabled) {
    final var isDisabled = Optional.ofNullable(disabled).orElse(false);
    return !isDisabled;
  }

  @Named("applyTemplateToString")
  protected String applyTemplate(
      final @Nullable String source, final @Context VariablesWrapper variables) {
    return stringTemplateRenderer.applyTemplate(source, variables);
  }

  @Named("mapFilePaths")
  @IterableMapping(qualifiedByName = "mapFilePath")
  protected abstract List<String> mapFilePaths(
      final List<String> filePaths, final @Context VariablesWrapper variables);

  @Named("mapFilePath")
  protected String mapFilePath(final String filePath, final @Context VariablesWrapper variables) {
    return applyTemplate(filePath, variables).replaceAll("[\\\\/]", "/");
  }
}
