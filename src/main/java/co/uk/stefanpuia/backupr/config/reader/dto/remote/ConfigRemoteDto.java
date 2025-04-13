package co.uk.stefanpuia.backupr.config.reader.dto.remote;

import co.uk.stefanpuia.backupr.config.model.remote.RemoteType;
import co.uk.stefanpuia.backupr.config.reader.dto.IdentifiableConfigDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
  @JsonSubTypes.Type(value = LocalConfigRemoteDto.class, name = "LOCAL"),
  @JsonSubTypes.Type(value = AzureStorageBlobConfigRemoteDto.class, name = "AZURE_STORAGE_BLOB"),
  @JsonSubTypes.Type(value = GitConfigRemoteDto.class, name = "GIT"),
})
public interface ConfigRemoteDto extends IdentifiableConfigDto {
  @Nullable
  Boolean isDisabled();

  @JsonIgnore
  RemoteType getType();
}
