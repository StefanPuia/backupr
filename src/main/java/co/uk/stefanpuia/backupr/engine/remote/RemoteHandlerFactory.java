package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.GitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.BackupSession;
import co.uk.stefanpuia.backupr.engine.adapters.AzureBlobClientProvider;
import co.uk.stefanpuia.backupr.engine.remote.mapper.JgitCredentialsProviderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RemoteHandlerFactory {
  private final BackupHelper backupHelper;
  private final JgitCredentialsProviderMapper jgitCredentialsProviderMapper;
  private final AzureBlobClientProvider azureBlobClientProvider;
  private final StringTemplateRenderer stringTemplateRenderer;
  private final BackupSession backupSession;
  private final ObjectMapper jsonMapper;

  public RemoteHandler getInstance(final ConfigRemote remote) {
    return switch (remote) {
      case LocalConfigRemote local ->
          new LocalRemoteHandler(local, backupHelper, backupSession, jsonMapper);
      case GitConfigRemote git ->
          new GitRemoteHandler(
              git,
              backupHelper,
              jgitCredentialsProviderMapper,
              stringTemplateRenderer,
              backupSession,
              jsonMapper);
      case AzureStorageBlobConfigRemote azure ->
          new AzureStorageBlobRemoteHandler(
              azure,
              azureBlobClientProvider,
              backupHelper,
              stringTemplateRenderer,
              backupSession,
              jsonMapper);
      default -> throw new IllegalArgumentException("Unsupported remote type: " + remote);
    };
  }
}
