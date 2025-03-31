package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.credentials.CredentialsType;
import co.uk.stefanpuia.backupr.config.model.validation.AllowedCredentials;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class AzureStorageBlobConfigRemote implements ConfigRemote {

  @Override
  public RemoteType getType() {
    return RemoteType.AZURE_STORAGE_BLOB;
  }

  @AllowedCredentials({
    CredentialsType.NONE,
    CredentialsType.AZURE_CLI,
    CredentialsType.AZURE_CLIENT_SECRET
  })
  public abstract Credentials getCredentials();

  public abstract String getBlobPrefixPattern();

  public abstract String getEndpoint();

  public abstract String getContainer();

  public abstract boolean isOverwrite();
}
