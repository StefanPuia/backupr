package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.RemoteType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;

public record LocalConfigRemote(@NotBlank String name, @Nullable Boolean enabled, @NotBlank String location)
    implements ConfigRemote {

  public Boolean enabled() {
    return Optional.ofNullable(enabled).orElse(false);
  }

  public RemoteType type() {
    return RemoteType.LOCAL;
  }

  @Override
  public void checkValid(final BackuprConfig config) {
    ConfigRemote.super.checkValid(config);
  }
}
