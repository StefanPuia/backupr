package co.uk.stefanpuia.backupr.config.model.credentials;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import jakarta.annotation.Nullable;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public interface AzureCliCredentials extends Credentials {
  @Nullable
  String getTenantId();

  @Override
  default CredentialsType getType() {
    return CredentialsType.AZURE_CLI;
  }
}
