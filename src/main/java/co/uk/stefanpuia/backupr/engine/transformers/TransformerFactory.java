package co.uk.stefanpuia.backupr.engine.transformers;

import co.uk.stefanpuia.backupr.config.model.source.transformer.ConfigTransformerOptions;
import co.uk.stefanpuia.backupr.config.model.source.transformer.ZipConfigTransformerOptions;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransformerFactory {
  private final BackupHelper helper;

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  public Transformer getInstance(final ConfigTransformerOptions transformerOptions) {
    return switch (transformerOptions) {
      case ZipConfigTransformerOptions zipOptions -> new ZipTransformer(zipOptions, helper);
      default -> throw new IllegalStateException("Unexpected value: " + transformerOptions);
    };
  }
}
