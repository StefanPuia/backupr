package co.uk.stefanpuia.backupr.config.model.source.transformer;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import co.uk.stefanpuia.backupr.config.reader.mapper.VariablesWrapper;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public interface ZipConfigTransformerOptions extends ConfigTransformerOptions {
  VariablesWrapper getVariables();

  String getFilenamePattern();
}
