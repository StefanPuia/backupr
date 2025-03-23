package co.uk.stefanpuia.backupr.core;

import co.uk.stefanpuia.backupr.config.reader.mapper.VariablesWrapper;
import jakarta.annotation.Nullable;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.stringtemplate.v4.ST;

@Component
public class StringTemplateRenderer {
  public String applyTemplate(final @Nullable String source, final VariablesWrapper variables) {
    return Optional.ofNullable(source)
        .map(ST::new)
        .map(
            template -> {
              variables.variables().forEach(template::add);
              template.add("env", variables.environment());
              template.add("context", variables.context());
              return template.render();
            })
        .orElse(null);
  }
}
