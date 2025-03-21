package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.model.remote.credentials.BasicCredentials;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials.BasicCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials.CredentialsDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    config = MapstructConfig.class,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigCredentialsMapper {

  @SubclassMapping(target = BasicCredentials.class, source = BasicCredentialsDto.class)
  protected abstract Credentials mapRemote(CredentialsDto source);
}
