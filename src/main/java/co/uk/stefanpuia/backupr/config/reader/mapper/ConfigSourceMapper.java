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

@Mapper(
    config = MapstructConfig.class,
    uses = CoreDtoMapper.class,
    subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)
public abstract class ConfigSourceMapper {
  @Mapping(target = "basePath", ignore = true)
  @Mapping(target = "remotes", ignore = true)
  @SubclassMapping(
      target = LocalConfigSource.class,
      source = LocalConfigSourceDto.class,
      qualifiedByName = "convertLocalConfigSource")
  protected abstract ConfigSource mapSource(
      ConfigSourceDto source, @Context List<ConfigRemote> remotes);

  @Named("convertLocalConfigSource")
  @Mapping(target = "basePath", ignore = true)
  @Mapping(
      target = "remotes",
      expression = "java(pickRemotes(source.getName(), source.getRemotes(), remotes))")
  @Mapping(target = "files", source = "files", qualifiedByName = "normalizeFilePaths")
  @Mapping(target = "enabled", source = "disabled", qualifiedByName = "mapDisabledToEnabled")
  protected abstract LocalConfigSource convertLocalConfigSource(
      LocalConfigSourceDto source, @Context List<ConfigRemote> remotes);

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

  @Named("normalizeFilePaths")
  protected List<String> normalizeFilePaths(final List<String> filePaths) {
    return filePaths.stream().map(pattern -> pattern.replaceAll("[\\\\/]", "/")).toList();
  }
}
