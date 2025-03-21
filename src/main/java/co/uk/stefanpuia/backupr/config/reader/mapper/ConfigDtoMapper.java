package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.reader.dto.BackuprConfigDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ConfigSourceDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    config = MapstructConfig.class,
    uses = {ConfigSourceMapper.class, ConfigRemoteMapper.class})
public abstract class ConfigDtoMapper {
  @Autowired protected ConfigSourceMapper sourceMapper;

  public BackuprConfig convert(BackuprConfigDto source) {
    final var remotes = mapRemotes(source.remotes());
    final var sources = mapSources(source.sources(), remotes);
    return new BackuprConfig(remotes, sources);
  }

  @IterableMapping(qualifiedByName = "mapRemote")
  protected abstract List<ConfigRemote> mapRemotes(List<ConfigRemoteDto> source);

  protected List<ConfigSource> mapSources(
      List<ConfigSourceDto> sources, List<ConfigRemote> remotes) {
    return sources.stream().map(source -> sourceMapper.mapSource(source, remotes)).toList();
  }
}
