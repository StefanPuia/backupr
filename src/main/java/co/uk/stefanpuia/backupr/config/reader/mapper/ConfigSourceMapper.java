package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.source.LocalConfigSource;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ConfigSourceDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.LocalConfigSourceDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    config = MapstructConfig.class,
    uses = CoreDtoMapper.class,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigSourceMapper {
  @Autowired private CoreDtoMapper coreMapper;

  @SubclassMapping(
      target = LocalConfigSource.class,
      source = LocalConfigSourceDto.class,
      qualifiedByName = "convertLocalConfigSource")
  protected abstract ConfigSource mapSource(
      ConfigSourceDto source,
      @Context List<ConfigRemote> remotes,
      @Context VariablesWrapper variables);

  @Named("convertLocalConfigSource")
  @Mapping(
      target = "remotes",
      expression = "java(pickRemotes(source.getName(), source.getRemotes(), remotes))")
  @Mapping(target = "files", source = "files", qualifiedByName = "mapFilePaths")
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  @Mapping(target = "directory", source = "directory", qualifiedByName = "applyTemplateToString")
  protected abstract LocalConfigSource convertLocalConfigSource(
      LocalConfigSourceDto source,
      @Context List<ConfigRemote> remotes,
      @Context VariablesWrapper variables);

  protected List<ConfigRemote> pickRemotes(
      final String sourceName, final List<String> remoteNames, final List<ConfigRemote> remotes) {
    return remoteNames.stream()
        .map(
            remoteName ->
                remotes.stream()
                    .filter(remote -> remote.getName().equals(remoteName))
                    .findFirst()
                    .orElseThrow(
                        () ->
                            new ConfigValidationException(
                                "in source '%s': no remote named '%s' defined"
                                    .formatted(sourceName, remoteName))))
        .toList();
  }

  @Named("mapFilePaths")
  protected List<String> mapFilePaths(
      final List<String> filePaths, final @Context VariablesWrapper variables) {
    return filePaths.stream()
        .map(pattern -> coreMapper.applyTemplate(pattern, variables))
        .map(pattern -> pattern.replaceAll("[\\\\/]", "/"))
        .toList();
  }
}
