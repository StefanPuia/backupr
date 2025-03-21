package co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({@JsonSubTypes.Type(value = BasicCredentialsDto.class, name = "basic")})
public interface CredentialsDto {}
