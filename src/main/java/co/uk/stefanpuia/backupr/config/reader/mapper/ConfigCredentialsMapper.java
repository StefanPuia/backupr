package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.model.remote.credentials.BasicCredentials;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials.BasicCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.credentials.CredentialsDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    config = MapstructConfig.class,
    uses = CoreDtoMapper.class,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigCredentialsMapper {

  @SubclassMapping(
      target = BasicCredentials.class,
      source = BasicCredentialsDto.class,
      qualifiedByName = "convertBasicCredentials")
  protected abstract Credentials mapRemote(
      CredentialsDto source, @Context VariablesWrapper variables);

  @Named("convertBasicCredentials")
  @Mapping(target = "username", source = "username", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "password", source = "password", qualifiedByName = "applyTemplateToString")
  protected abstract BasicCredentials convertBasicCredentials(
      BasicCredentialsDto source, @Context VariablesWrapper variables);
}
