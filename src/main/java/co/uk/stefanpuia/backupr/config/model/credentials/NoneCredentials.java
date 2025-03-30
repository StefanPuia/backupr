package co.uk.stefanpuia.backupr.config.model.credentials;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public interface NoneCredentials extends Credentials {
  @Override
  default CredentialsType getType() {
    return CredentialsType.NONE;
  }
}
