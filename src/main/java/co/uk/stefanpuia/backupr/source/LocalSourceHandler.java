package co.uk.stefanpuia.backupr.source;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.source.LocalConfigSource;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LocalSourceHandler implements SourceHandler {
  private final LocalConfigSource source;
  private final BackuprConfig config;

  @Override
  public List<String> getFiles() {

    return List.of();
  }
}
