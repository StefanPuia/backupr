package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import co.uk.stefanpuia.backupr.config.model.RemoteType;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.Credentials;
import java.net.URISyntaxException;
import java.util.Optional;
import org.eclipse.jgit.transport.URIish;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class GitConfigRemote implements ConfigRemote {

  public abstract String getUrl();

  public abstract String getBranch();

  public abstract Optional<Credentials> getCredentials();

  @Override
  public RemoteType getType() {
    return RemoteType.GIT;
  }

  public URIish originUri() throws URISyntaxException {
    return new URIish(getUrl());
  }
}
