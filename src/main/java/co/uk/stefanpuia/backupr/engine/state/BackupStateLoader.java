package co.uk.stefanpuia.backupr.engine.state;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.state.BackupState;
import co.uk.stefanpuia.backupr.engine.BackupSession;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerException;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerFactory;
import co.uk.stefanpuia.backupr.engine.state.mapper.BackupStateMapper;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackupStateLoader {
  private final RemoteHandlerFactory remoteHandlerFactory;
  private final BackupStateMapper stateMapper;
  private final BackupSession backupSession;

  public BackuprConfig initialiseState(final BackuprConfig config) {
    log.debug("Loading state from remote");
    if (config.getState() == null) {
      log.debug("State not configured in config, initialising with defaults.");
      return config;
    }
    return readFromRemote(config, config.getState());
  }

  private BackuprConfig readFromRemote(final BackuprConfig config, final BackupState state) {
    try {
      final var readStateConfig =
          stateMapper.read(
              remoteHandlerFactory
                  .getInstance(state.getRemote())
                  .readStateFile(BackupState.STATE_FILE_NAME),
              config);
      if (readStateConfig == null) {
        log.debug("No state found in remote, initialising with defaults.");
        return config;
      }
      log.debug(
          "State loaded from remote: lastRun: {}",
          Objects.requireNonNull(readStateConfig.getState()).getLastBackup());
      return readStateConfig;
    } catch (RemoteHandlerException e) {
      log.error(
          "Failed to read state from remote. Please ensure the remote is accessible and existing"
              + " file is valid.",
          e);
      throw e;
    }
  }

  public void writeState(final BackuprConfig config) {
    log.debug("Writing state to remote");
    if (config.getState() == null) {
      log.debug("State not configured in config, not writing state.");
      return;
    }
    try {
      final var stateDto =
          stateMapper.write(backupSession.getBackups(), backupSession.getCleanups(), config);
      if (!backupSession.isDry()) {
        remoteHandlerFactory
            .getInstance(config.getState().getRemote())
            .writeStateFile(BackupState.STATE_FILE_NAME, stateDto);
      }
    } catch (RemoteHandlerException e) {
      log.error("Failed to write state to remote. Please ensure the remote is accessible.", e);
      throw e;
    }
  }
}
