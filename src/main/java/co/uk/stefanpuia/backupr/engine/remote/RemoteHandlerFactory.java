package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.GitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.remote.mapper.AzureCredentialMapper;
import co.uk.stefanpuia.backupr.engine.remote.mapper.JgitCredentialsProviderMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RemoteHandlerFactory {
  private final BackupHelper backupHelper;
  private final JgitCredentialsProviderMapper jgitCredentialsProviderMapper;
  private final AzureCredentialMapper azureCredentialMapper;
  private final StringTemplateRenderer stringTemplateRenderer;

  public RemoteHandler getInstance(final ConfigRemote remote) {
    return switch (remote) {
      case LocalConfigRemote local -> new LocalRemoteHandler(local, backupHelper);
      case GitConfigRemote git ->
          new GitRemoteHandler(
              git, backupHelper, jgitCredentialsProviderMapper, stringTemplateRenderer);
      case AzureStorageBlobConfigRemote azure ->
          new AzureStorageBlobRemoteHandler(
              azure, azureCredentialMapper, backupHelper, stringTemplateRenderer);
      default -> throw new IllegalArgumentException("Unsupported remote type: " + remote);
    };
  }
}
