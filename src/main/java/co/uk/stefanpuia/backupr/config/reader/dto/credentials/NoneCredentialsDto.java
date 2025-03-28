package co.uk.stefanpuia.backupr.config.reader.dto.credentials;

import co.uk.stefanpuia.backupr.config.reader.dto.DtoStyle;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableNoneCredentialsDto.Builder.class)
public interface NoneCredentialsDto extends CredentialsDto {}
