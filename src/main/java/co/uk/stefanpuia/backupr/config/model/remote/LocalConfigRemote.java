package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.RemoteType;
import jakarta.validation.constraints.NotBlank;

public record LocalConfigRemote(@NotBlank String name, @NotBlank String location)
    implements ConfigRemote {

  public RemoteType type() {
    return RemoteType.LOCAL;
  }

  public void checkValid(final BackuprConfig config) {}
}
