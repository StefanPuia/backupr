package co.uk.stefanpuia.backupr.config.reader.mapper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.DockerCpConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.DockerExecConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.LocalConfigSource;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.MixedRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.DockerCpConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.DockerExecConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.LocalConfigSourceDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    config = MapstructConfig.class,
    uses = {CoreDtoMapper.class, ConfigTransformerMapper.class},
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigSourceMapper {
  private static final String REMOTES_EXPRESSION =
      "java(mapMixedRemotes(source.getName(), source.getRemotes(), remotes, credentials,"
          + " variables))";
  @Autowired private CoreDtoMapper coreMapper;
  @Autowired private ConfigRemoteMapper remoteMapper;

  @SubclassMapping(
      target = LocalConfigSource.class,
      source = LocalConfigSourceDto.class,
      qualifiedByName = "convertLocalConfigSource")
  @SubclassMapping(
      target = DockerCpConfigSource.class,
      source = DockerCpConfigSourceDto.class,
      qualifiedByName = "convertDockerCpConfigSource")
  @SubclassMapping(
      target = DockerExecConfigSource.class,
      source = DockerExecConfigSourceDto.class,
      qualifiedByName = "convertDockerExecConfigSource")
  protected abstract ConfigSource mapSource(
      ConfigSourceDto source,
      @Context List<ConfigRemote> remotes,
      @Context List<Credentials> credentials,
      @Context VariablesWrapper variables);

  @Named("convertLocalConfigSource")
  @Mapping(target = "remotes", expression = REMOTES_EXPRESSION)
  @Mapping(target = "files", source = "files", qualifiedByName = "mapFilePaths")
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @Mapping(target = "directory", source = "directory", qualifiedByName = "applyTemplateToString")
  protected abstract LocalConfigSource convertLocalConfigSource(
      LocalConfigSourceDto source,
      @Context List<ConfigRemote> remotes,
      @Context List<Credentials> credentials,
      @Context VariablesWrapper variables);

  @Named("convertDockerCpConfigSource")
  @Mapping(target = "remotes", expression = REMOTES_EXPRESSION)
  @Mapping(target = "paths", source = "paths", qualifiedByName = "mapFilePaths")
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @Mapping(target = "container", source = "container", qualifiedByName = "applyTemplateToString")
  protected abstract DockerCpConfigSource convertDockerCpConfigSource(
      DockerCpConfigSourceDto source,
      @Context List<ConfigRemote> remotes,
      @Context List<Credentials> credentials,
      @Context VariablesWrapper variables);

  @Named("convertDockerExecConfigSource")
  @Mapping(target = "remotes", expression = REMOTES_EXPRESSION)
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @Mapping(target = "container", source = "container", qualifiedByName = "applyTemplateToString")
  protected abstract DockerExecConfigSource convertDockerExecConfigSource(
      DockerExecConfigSourceDto source,
      @Context List<ConfigRemote> remotes,
      @Context List<Credentials> credentials,
      @Context VariablesWrapper variables);

  @Mapping(target = "outputFile", source = "file", qualifiedByName = "mapFilePath")
  @Mapping(target = "command", source = "command", qualifiedByName = "mapCommandArgs")
  protected abstract DockerExecConfigSource.Command convert(
      DockerExecConfigSourceDto.CommandDto source, @Context VariablesWrapper variables);

  protected List<ConfigRemote> mapMixedRemotes(
      final String sourceName,
      final @Context List<MixedRemoteDto> mixedRemotes,
      final @Context List<ConfigRemote> remotes,
      final @Context List<Credentials> credentials,
      final @Context VariablesWrapper variables) {
    return mixedRemotes.stream()
        .map(
            remote -> {
              if (isNull(remote.name()) && isNull(remote.remote())) {
                throw new ConfigValidationException(
                    "in source '%s': either a name or inline remote must be provided");
              }

              if (nonNull(remote.name())) {
                return pickRemotes(sourceName, remotes, remote.name());
              }

              return remoteMapper.mapRemote(remote.remote(), sourceName, credentials, variables);
            })
        .toList();
  }

  private ConfigRemote pickRemotes(
      final String sourceName, final List<ConfigRemote> remotes, final @NotNull String remoteName) {
    return remotes.stream()
        .filter(remote -> remoteName.equals(remote.getName()))
        .findFirst()
        .orElseThrow(
            () ->
                new ConfigValidationException(
                    "in source '%s': no remote named '%s' defined"
                        .formatted(sourceName, remoteName)));
  }

  @Named("mapCommandArgs")
  @IterableMapping(qualifiedByName = "applyTemplateToString")
  protected abstract String[] mapCommandArgs(
      List<String> source, @Context VariablesWrapper variables);
}
