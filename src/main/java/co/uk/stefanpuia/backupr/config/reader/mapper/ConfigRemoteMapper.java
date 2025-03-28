package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.GitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.LocalConfigRemote;
import co.uk.stefanpuia.backupr.config.model.remote.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.AzureStorageConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteWithCredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.GitConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.LocalConfigRemoteDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    config = MapstructConfig.class,
    uses = {CoreDtoMapper.class, ConfigCredentialsMapper.class},
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigRemoteMapper {
  @Autowired protected ConfigCredentialsMapper credentialsMapper;

  @Named("mapRemote")
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @SubclassMapping(
      target = LocalConfigRemote.class,
      source = LocalConfigRemoteDto.class,
      qualifiedByName = "convertLocalConfigRemote")
  @SubclassMapping(
      target = GitConfigRemote.class,
      source = GitConfigRemoteDto.class,
      qualifiedByName = "convertGitConfigRemote")
  @SubclassMapping(
      target = AzureStorageConfigRemote.class,
      source = AzureStorageConfigRemoteDto.class)
  protected abstract ConfigRemote mapRemote(
      ConfigRemoteDto source,
      @Context List<Credentials> credentials,
      @Context VariablesWrapper variables);

  @Named("convertLocalConfigRemote")
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @Mapping(target = "location", source = "location", qualifiedByName = "applyTemplateToString")
  protected abstract LocalConfigRemote convertLocalConfigRemote(
      LocalConfigRemoteDto source, @Context VariablesWrapper variables);

  @Named("convertGitConfigRemote")
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @Mapping(target = "url", source = "url", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "branch", source = "branch", qualifiedByName = "applyTemplateToString")
  @Mapping(target = "credentials", source = "source")
  protected abstract GitConfigRemote convertGitConfigRemote(
      GitConfigRemoteDto source,
      @Context List<Credentials> credentials,
      @Context VariablesWrapper variables);

  protected Optional<Credentials> mapMixedCredentials(
      final ConfigRemoteWithCredentialsDto remote,
      final @Context List<Credentials> credentials,
      final @Context VariablesWrapper variables) {
    final var source = remote.getCredentials();

    if (Objects.isNull(source)) {
      return Optional.empty();
    }

    if (Objects.isNull(source.credentials()) && Objects.isNull(source.name())) {
      return Optional.empty();
    }

    if (Objects.nonNull(source.credentials())) {
      return Optional.ofNullable(credentialsMapper.mapCredential(source.credentials(), variables));
    }

    return Optional.of(
        credentials.stream()
            .filter(cred -> source.name().equals(cred.getName()))
            .findFirst()
            .orElseThrow(
                () ->
                    new ConfigValidationException(
                        "in remote '%s': no credentials named '%s' defined"
                            .formatted(remote.getName(), source.name()))));
  }
}
