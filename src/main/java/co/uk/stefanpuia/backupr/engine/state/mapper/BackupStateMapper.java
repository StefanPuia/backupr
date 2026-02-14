package co.uk.stefanpuia.backupr.engine.state.mapper;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.ImmutableBackuprConfig;
import co.uk.stefanpuia.backupr.config.model.state.BackupState;
import co.uk.stefanpuia.backupr.config.model.state.BackupStateBackup;
import co.uk.stefanpuia.backupr.config.model.state.BackupStateRemote;
import co.uk.stefanpuia.backupr.config.model.state.ImmutableBackupState;
import co.uk.stefanpuia.backupr.config.model.state.ImmutableBackupStateRemote;
import co.uk.stefanpuia.backupr.core.MapstructConfig;
import co.uk.stefanpuia.backupr.engine.state.BackupStateRecord;
import co.uk.stefanpuia.backupr.engine.state.dto.BackupStateBackupDto;
import co.uk.stefanpuia.backupr.engine.state.dto.BackupStateDto;
import co.uk.stefanpuia.backupr.engine.state.dto.BackupStateRemoteDto;
import co.uk.stefanpuia.backupr.engine.state.dto.ImmutableBackupStateDto;
import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapstructConfig.class)
public abstract class BackupStateMapper {

  @Nullable
  @Mapping(target = "remotes", ignore = true)
  @Mapping(target = "sources", ignore = true)
  @Mapping(target = "state", source = "source")
  public abstract BackuprConfig read(
      final BackupStateDto source, @Context final BackuprConfig initialValue);

  @Mapping(target = "remote", ignore = true)
  @Mapping(target = "lastBackup", source = "lastBackup")
  @Mapping(target = "remotes", source = "remotes")
  protected abstract BackupState convertState(
      final BackupStateDto source, @Context final BackuprConfig initialValue);

  @Mapping(target = "remoteRelativeFilePaths", source = "files")
  protected abstract BackupStateBackup convertBackup(BackupStateBackupDto backup);

  @AfterMapping
  protected void setRemoteFromInitialState(
      @MappingTarget final ImmutableBackupState.Builder target,
      @Context final BackuprConfig initialValue) {
    target.setRemote(Objects.requireNonNull(initialValue.getState()).getRemote());
  }

  @AfterMapping
  protected void setOriginalConfig(
      @MappingTarget final ImmutableBackuprConfig.Builder target,
      @Context final BackuprConfig initialValue) {
    target.setRemotes(initialValue.getRemotes()).setSources(initialValue.getSources());
  }

  public BackupStateDto write(
      final List<BackupStateRecord> currentBackups,
      final List<BackupStateBackup> currentCleanups,
      final BackuprConfig initialState) {
    return ImmutableBackupStateDto.builder()
        .setVersion(1)
        .setLastBackup(Instant.now())
        .setRemotes(buildRemotesState(currentBackups, currentCleanups, initialState))
        .build();
  }

  private Map<String, BackupStateRemoteDto> buildRemotesState(
      final List<BackupStateRecord> currentBackups,
      final List<BackupStateBackup> currentCleanups,
      final BackuprConfig initialState) {
    final var map = new HashMap<String, List<BackupStateBackup>>();

    if (initialState.getState() != null) {
      Objects.requireNonNull(initialState.getState().getRemotes())
          .forEach(
              (remote, backups) ->
                  map.put(
                      remote,
                      new ArrayList<>(
                          backups.getBackups().stream()
                              .filter(backup -> !currentCleanups.contains(backup))
                              .toList())));
    }

    for (final var backup : currentBackups) {
      final var record = convertRecord(backup);
      map.computeIfAbsent(backup.remoteName(), k -> new ArrayList<>()).add(record);
    }

    return map.entrySet().stream()
        .map(
            entry ->
                ImmutableBackupStateRemote.builder()
                    .setName(entry.getKey())
                    .setBackups(entry.getValue())
                    .build())
        .collect(Collectors.toMap(BackupStateRemote::getName, this::convertRemoteToDto));
  }

  protected abstract BackupStateBackup convertRecord(BackupStateRecord backup);

  protected abstract BackupStateRemoteDto convertRemoteToDto(BackupStateRemote backup);

  @Mapping(target = "files", source = "remoteRelativeFilePaths")
  protected abstract BackupStateBackupDto convertBackup(BackupStateBackup backup);
}
