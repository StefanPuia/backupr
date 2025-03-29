package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import java.util.Optional;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class AzureStorageBlobConfigRemote implements ConfigRemote {

  @Override
  public RemoteType getType() {
    return RemoteType.AZURE_STORAGE_BLOB;
  }

  public abstract Optional<Credentials> getCredentials();

  public abstract String getBlobPrefixPattern();

  public abstract String getEndpoint();

  public abstract String getContainer();

  public abstract boolean isOverwrite();
}
