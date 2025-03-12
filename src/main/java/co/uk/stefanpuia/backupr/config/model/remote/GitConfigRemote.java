package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.RemoteType;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.Credentials;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URISyntaxException;
import java.util.Optional;
import org.eclipse.jgit.transport.URIish;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitConfigRemote(
    @NotBlank String name,
    @Nullable Boolean enabled,
    @NotBlank String url,
    @NotBlank String branch,
    @Valid @Nullable Credentials credentials)
    implements ConfigRemote {

  public Boolean enabled() {
    return Optional.ofNullable(enabled).orElse(false);
  }

  public RemoteType type() {
    return RemoteType.GIT;
  }

  @Override
  public void checkValid(final BackuprConfig config) {
    ConfigRemote.super.checkValid(config);
  }

  public URIish originUri() throws URISyntaxException {
    return new URIish(url);
  }
}
