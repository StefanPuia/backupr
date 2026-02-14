package co.uk.stefanpuia.backupr.config.reader.dto;

import co.uk.stefanpuia.backupr.config.reader.dto.cleanup.CleanupDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.CredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.state.StateDto;
import co.uk.stefanpuia.backupr.config.reader.dto.validation.UniqueIdentifier;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.LinkedHashMap;
import java.util.List;
import org.immutables.value.Value;

@JsonIgnoreProperties({"$schema"})
@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableBackuprConfigDto.Builder.class)
public abstract class BackuprConfigDto {
  public static final String VALID_IDENTIFIER_REGEX = "^[0-9a-zA-Z\\-_.]+$";

  @Value.Default
  public LinkedHashMap<@NotBlank String, @NotBlank String> getVariables() {
    return new LinkedHashMap<>();
  }

  @Nullable
  public abstract StateDto getState();

  @UniqueIdentifier
  public abstract List<@Valid CleanupDto> getCleanup();

  @UniqueIdentifier
  public abstract List<@Valid CredentialsDto> getCredentials();

  @UniqueIdentifier
  public abstract List<@Valid ConfigRemoteDto> getRemotes();

  @UniqueIdentifier
  public abstract List<@Valid ConfigSourceDto> getSources();
}
