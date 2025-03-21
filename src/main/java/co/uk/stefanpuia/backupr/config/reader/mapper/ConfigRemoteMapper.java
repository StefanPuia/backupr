package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.GitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.AzureStorageConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.GitConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.LocalConfigRemoteDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(
    config = MapstructConfig.class,
    uses = {CoreDtoMapper.class, ConfigCredentialsMapper.class},
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigRemoteMapper {

  @Named("mapRemote")
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @SubclassMapping(target = LocalConfigRemote.class, source = LocalConfigRemoteDto.class)
  @SubclassMapping(target = GitConfigRemote.class, source = GitConfigRemoteDto.class)
  @SubclassMapping(
      target = AzureStorageConfigRemote.class,
      source = AzureStorageConfigRemoteDto.class)
  protected abstract ConfigRemote mapRemote(ConfigRemoteDto source);

  protected Optional<Credentials> wrapOptional(final Credentials source) {
    return Optional.ofNullable(source);
  }
}
