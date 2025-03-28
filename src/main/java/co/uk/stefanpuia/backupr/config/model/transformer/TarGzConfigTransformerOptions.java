package co.uk.stefanpuia.backupr.config.model.transformer;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public interface TarGzConfigTransformerOptions extends ConfigTransformerOptions {
  String getFilenamePattern();
}
