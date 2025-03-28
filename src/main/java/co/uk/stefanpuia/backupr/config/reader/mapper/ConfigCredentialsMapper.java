package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.model.credentials.BasicCredentials;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.credentials.NoneCredentials;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.BasicCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.CredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.NoneCredentialsDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    config = MapstructConfig.class,
    uses = CoreDtoMapper.class,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigCredentialsMapper {

  @SubclassMapping(target = NoneCredentials.class, source = NoneCredentialsDto.class)
  @SubclassMapping(target = BasicCredentials.class, source = BasicCredentialsDto.class)
  protected abstract Credentials mapCredential(
      CredentialsDto source, @Context VariablesWrapper variables);

  protected abstract NoneCredentials convert(
      NoneCredentialsDto source, @Context VariablesWrapper variables);

  @Mapping(target = "username", source = "username", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "password", source = "password", qualifiedByName = "applyTemplateToString")
  protected abstract BasicCredentials convert(
      BasicCredentialsDto source, @Context VariablesWrapper variables);
}
