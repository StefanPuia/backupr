package co.uk.stefanpuia.backupr.config.model.source.transformer;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class ZipConfigTransformerOptions implements ConfigTransformerOptions {
  public static ZipConfigTransformerOptions getDefault() {
    return ImmutableZipConfigTransformerOptions.builder().build();
  }
}
