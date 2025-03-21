package co.uk.stefanpuia.backupr.config.reader.dto;

import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ConfigSourceDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@JsonIgnoreProperties({"$schema"})
public record BackuprConfigDto(
    @NotEmpty List<@Valid ConfigRemoteDto> remotes,
    @NotEmpty List<@Valid ConfigSourceDto> sources) {

  public static final String VALID_IDENTIFIER_REGEX = "^[0-9a-zA-Z\\-_.]+$";
}
