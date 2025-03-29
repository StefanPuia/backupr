package co.uk.stefanpuia.backupr.engine.remote.mapper;

import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.credentials.NoneCredentials;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    config = MapstructConfig.class,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class AzureCredentialMapper {

  @Mapping(target = "tokenCredentials", ignore = true)
  @SubclassMapping(target = DefaultAzureCredential.class, source = NoneCredentials.class)
  public abstract TokenCredential convert(Credentials source);

  protected DefaultAzureCredential defaultAzureCredential(NoneCredentials source) {
    return new DefaultAzureCredentialBuilder().build();
  }
}
