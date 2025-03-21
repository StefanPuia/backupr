package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import co.uk.stefanpuia.backupr.config.model.RemoteType;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class AzureStorageConfigRemote implements ConfigRemote {

  @Override
  public RemoteType getType() {
    return RemoteType.AZURE_STORAGE;
  }
}
