package co.uk.stefanpuia.backupr.config.model.remote.credentials;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import jakarta.annotation.Nullable;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public interface BasicCredentials extends Credentials {
  String getUsername();

  @Nullable
  String getPassword();
}
