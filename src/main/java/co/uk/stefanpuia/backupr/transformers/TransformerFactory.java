package co.uk.stefanpuia.backupr.transformers;

import co.uk.stefanpuia.backupr.config.model.SourceTransformer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransformerFactory {

  public Transformer getInstance(final SourceTransformer type) {
    return switch (type) {
      case ZIP -> new ZipTransformer();
    };
  }
}
