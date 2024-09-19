package co.uk.stefanpuia.backupr.remote;

import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import org.springframework.stereotype.Component;

@Component
public class RemoteHandlerFactory {

  public RemoteHandler getInstance(final ConfigRemote remote) {
    return switch (remote) {
      case LocalConfigRemote local -> new LocalRemoteHandler(local);
      case AzureStorageConfigRemote azure -> null;
      default -> throw new IllegalArgumentException("Unsupported remote type: " + remote);
    };
  }
}
