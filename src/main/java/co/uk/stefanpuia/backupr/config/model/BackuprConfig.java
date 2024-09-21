package co.uk.stefanpuia.backupr.config.model;

import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@JsonIgnoreProperties({"$schema"})
public record BackuprConfig(
    @NotEmpty List<@Valid ConfigRemote> remotes, @NotEmpty List<@Valid ConfigSource> sources) {

  public static final String VALID_IDENTIFIER_REGEX = "^[0-9a-zA-Z\\-_.]+$";

  public void checkValid() {
    remotes.forEach(remote -> remote.checkValid(this));
    sources.forEach(source -> source.checkValid(this));
  }
}
