package co.uk.stefanpuia.backupr.config.reader.dto.credentials;

import co.uk.stefanpuia.backupr.config.reader.dto.IdentifiableConfigDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.DEDUCTION,
    defaultImpl = NoneCredentialsDto.class,
    property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = NoneCredentialsDto.class, name = "NONE"),
  @JsonSubTypes.Type(value = BasicCredentialsDto.class, name = "BASIC"),
  @JsonSubTypes.Type(value = AzureCliCredentialsDto.class, name = "AZURE_CLI"),
  @JsonSubTypes.Type(value = AzureClientSecretCredentialsDto.class, name = "AZURE_CLIENT_SECRET")
})
public interface CredentialsDto extends IdentifiableConfigDto {
  @Override
  @Nullable
  String getName();
}
