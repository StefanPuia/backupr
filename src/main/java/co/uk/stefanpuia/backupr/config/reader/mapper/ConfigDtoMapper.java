package co.uk.stefanpuia.backupr.config.reader.mapper;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.ImmutableBackuprConfig;
import co.uk.stefanpuia.backupr.config.model.cleanup.Cleanup;
import co.uk.stefanpuia.backupr.config.model.credentials.Credentials;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.reader.dto.BackuprConfigDto;
import co.uk.stefanpuia.backupr.config.reader.dto.cleanup.CleanupDto;
import co.uk.stefanpuia.backupr.config.reader.dto.credentials.CredentialsDto;
import co.uk.stefanpuia.backupr.config.reader.dto.remote.ConfigRemoteDto;
import co.uk.stefanpuia.backupr.config.reader.dto.source.ConfigSourceDto;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(config = MapstructConfig.class)
public abstract class ConfigDtoMapper {
  @Autowired private CoreDtoMapper coreDtoMapper;
  @Autowired private ConfigCredentialsMapper credentialsMapper;
  @Autowired private ConfigCleanupMapper cleanupMapper;
  @Autowired private ConfigSourceMapper sourceMapper;
  @Autowired private ConfigRemoteMapper remoteMapper;
  @Autowired private ConfigStateMapper stateMapper;

  public BackuprConfig convert(BackuprConfigDto source) {
    final var variables = mapVariables(source.getVariables());
    final var credentials = mapCredentials(source.getCredentials(), variables);
    final var cleanup = mapCleanup(source.getCleanup(), variables);
    final var remotes = mapRemotes(source.getRemotes(), credentials, cleanup, variables);
    final var sources = mapSources(source.getSources(), remotes, credentials, cleanup, variables);
    final var state = stateMapper.mapState(source.getState(), credentials, remotes, variables);
    return ImmutableBackuprConfig.of(state, remotes, sources);
  }

  protected VariablesWrapper mapVariables(final LinkedHashMap<String, String> source) {
    final var environment = System.getenv();
    final var variables = new HashMap<String, String>();
    source.forEach(
        (key, value) ->
            variables.put(
                key,
                coreDtoMapper.applyTemplate(value, new VariablesWrapper(variables, environment))));
    return new VariablesWrapper(variables, environment);
  }

  protected List<ConfigRemote> mapRemotes(
      final List<ConfigRemoteDto> remotes,
      final List<Credentials> credentials,
      final List<Cleanup> cleanup,
      final VariablesWrapper variables) {
    return remotes.stream()
        .map(remote -> remoteMapper.mapRemote(remote, null, credentials, cleanup, variables))
        .peek(
            remote -> {
              if (Objects.isNull(remote.getName())) {
                throw new ConfigValidationException("Global remote name cannot be empty");
              }
            })
        .toList();
  }

  protected List<ConfigSource> mapSources(
      final List<ConfigSourceDto> sources,
      final List<ConfigRemote> remotes,
      final List<Credentials> credentials,
      final List<Cleanup> cleanup,
      final VariablesWrapper variables) {
    return sources.stream()
        .map(source -> sourceMapper.mapSource(source, remotes, credentials, cleanup, variables))
        .toList();
  }

  protected List<Credentials> mapCredentials(
      final List<CredentialsDto> credentials, final VariablesWrapper variables) {
    return credentials.stream()
        .map(credential -> credentialsMapper.mapCredential(credential, null, variables))
        .peek(
            cred -> {
              if (Objects.isNull(cred.getName())) {
                throw new ConfigValidationException("Global credential name cannot be empty");
              }
            })
        .toList();
  }

  protected List<Cleanup> mapCleanup(
      final List<CleanupDto> cleanups, final VariablesWrapper variables) {
    return cleanups.stream()
        .map(cleanup -> cleanupMapper.mapCleanup(cleanup, variables, null))
        .peek(
            cleanup -> {
              if (Objects.isNull(cleanup.getName())) {
                throw new ConfigValidationException("Global cleanup name cannot be empty");
              }
            })
        .toList();
  }
}
