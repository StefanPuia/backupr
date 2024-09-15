package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.RemoteType;
import jakarta.validation.constraints.NotBlank;

public record AzureStorageConfigRemote(@NotBlank String name) implements ConfigRemote {

  public RemoteType type() {
    return RemoteType.AZURE_STORAGE;
  }

  public void checkValid(final BackuprConfig config) {}
}
