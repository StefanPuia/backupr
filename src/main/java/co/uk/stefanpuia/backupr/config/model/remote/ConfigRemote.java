package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.RemoteType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @Type(value = LocalConfigRemote.class, name = "LOCAL"),
  @Type(value = AzureStorageConfigRemote.class, name = "AZURE_STORAGE"),
})
public interface ConfigRemote {
  String name();

  RemoteType type();

  void checkValid(BackuprConfig config);
}
