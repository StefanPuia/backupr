package co.uk.stefanpuia.backupr.config.reader.dto;

import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ConfigSourceDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.List;
import org.immutables.value.Value;

@JsonIgnoreProperties({"$schema"})
@DtoStyle
@Value.Immutable
@JsonDeserialize(builder = ImmutableBackuprConfigDto.Builder.class)
public abstract class BackuprConfigDto {
  public static final String VALID_IDENTIFIER_REGEX = "^[0-9a-zA-Z\\-_.]+$";

  public abstract LinkedHashMap<@NotBlank String, @NotBlank String> getVariables();

  @NotEmpty
  public abstract List<@Valid ConfigRemoteDto> getRemotes();

  @NotEmpty
  public abstract List<@Valid ConfigSourceDto> getSources();
}
