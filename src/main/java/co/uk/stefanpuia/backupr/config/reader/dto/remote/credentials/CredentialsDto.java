package co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = NoneCredentialsDto.class)
@JsonSubTypes({
  @JsonSubTypes.Type(value = NoneCredentialsDto.class, name = "NONE"),
  @JsonSubTypes.Type(value = BasicCredentialsDto.class, name = "BASIC")
})
public interface CredentialsDto {
  @Nullable
  String getName();
}
