package co.uk.stefanpuia.backupr.config.model.credentials;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public interface AzureClientSecretCredentials extends Credentials {
  String getTenantId();

  String getClientId();

  String getClientSecret();

  @Override
  default CredentialsType getType() {
    return CredentialsType.AZURE_CLIENT_SECRET;
  }
}
