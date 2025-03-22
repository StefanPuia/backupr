package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.GitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RemoteHandlerFactory {
  private final BackupHelper helper;

  public RemoteHandler getInstance(final ConfigRemote remote) {
    return switch (remote) {
      case LocalConfigRemote local -> new LocalRemoteHandler(local, helper);
      case GitConfigRemote git -> new GitRemoteHandler(git, helper);
      case AzureStorageConfigRemote azure -> null;
      default -> throw new IllegalArgumentException("Unsupported remote type: " + remote);
    };
  }
}
