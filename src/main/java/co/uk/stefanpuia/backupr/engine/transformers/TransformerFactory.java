package co.uk.stefanpuia.backupr.engine.transformers;

import co.uk.stefanpuia.backupr.config.model.SourceTransformer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransformerFactory {
  private final BackupHelper helper;

  public Transformer getInstance(final SourceTransformer type) {
    return switch (type) {
      case ZIP -> new ZipTransformer(helper);
    };
  }
}
