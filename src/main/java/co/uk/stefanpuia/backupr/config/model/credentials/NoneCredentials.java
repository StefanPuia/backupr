package co.uk.stefanpuia.backupr.config.model.credentials;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class NoneCredentials implements Credentials {
  public static NoneCredentials create() {
    return ImmutableNoneCredentials.builder().build();
  }

  @Override
  public CredentialsType getType() {
    return CredentialsType.NONE;
  }
}
