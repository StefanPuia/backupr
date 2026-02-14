package co.uk.stefanpuia.backupr.engine.transformers;

import co.uk.stefanpuia.backupr.config.model.transformer.ConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.transformer.TarGzConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.transformer.ZipConfigTransformerOptions;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.BackupSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransformerFactory {
  private final BackupHelper helper;
  private final StringTemplateRenderer stringTemplateRenderer;
  private final BackupSession backupSession;

  public Transformer getInstance(final ConfigTransformerOptions transformerOptions) {
    return switch (transformerOptions) {
      case ZipConfigTransformerOptions zipOptions ->
          new ZipTransformer(zipOptions, helper, stringTemplateRenderer, backupSession);
      case TarGzConfigTransformerOptions targzOptions ->
          new TarGzTransformer(targzOptions, helper, stringTemplateRenderer, backupSession);
      default -> throw new IllegalStateException("Unexpected value: " + transformerOptions);
    };
  }
}
