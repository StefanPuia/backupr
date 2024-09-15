package co.uk.stefanpuia.backupr.config.model.source;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = LocalConfigSource.class)
public interface ConfigSource {
  String name();

  void checkValid(BackuprConfig config);
}
