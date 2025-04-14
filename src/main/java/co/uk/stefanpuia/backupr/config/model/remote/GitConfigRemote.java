package co.uk.stefanpuia.backupr.config.model.remote;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.credentials.CredentialsType;
import co.uk.stefanpuia.backupr.config.model.validation.AllowedCredentials;
import java.net.URISyntaxException;
import org.eclipse.jgit.transport.URIish;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class GitConfigRemote implements ConfigRemote {

  public abstract String getUrl();

  public abstract String getBranch();

  public abstract String getCommitMessagePattern();

  @AllowedCredentials({CredentialsType.NONE, CredentialsType.BASIC})
  public abstract Credentials getCredentials();

  @Override
  public RemoteType getType() {
    return RemoteType.GIT;
  }

  public URIish originUri() throws URISyntaxException {
    return new URIish(getUrl());
  }
}
