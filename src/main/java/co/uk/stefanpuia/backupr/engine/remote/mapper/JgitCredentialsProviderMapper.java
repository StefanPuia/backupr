package co.uk.stefanpuia.backupr.engine.remote.mapper;

import static java.util.Optional.ofNullable;

import co.uk.stefanpuia.backupr.config.model.credentials.BasicCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.credentials.NoneCredentials;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    config = MapstructConfig.class,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class JgitCredentialsProviderMapper {

  @SubclassMapping(
      target = UsernamePasswordCredentialsProvider.class,
      source = BasicCredentials.class)
  @SubclassMapping(target = CredentialsProvider.class, source = NoneCredentials.class)
  public abstract CredentialsProvider convert(Credentials source);

  protected UsernamePasswordCredentialsProvider convert(BasicCredentials source) {
    return new UsernamePasswordCredentialsProvider(
        source.getUsername(), ofNullable(source.getPassword()).orElse(""));
  }

  protected CredentialsProvider convert(NoneCredentials source) {
    return CredentialsProvider.getDefault();
  }
}
