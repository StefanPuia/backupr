package co.uk.stefanpuia.backupr.engine;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.ImmutableBackuprConfig;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandler;
import co.uk.stefanpuia.backupr.engine.remote.RemoteHandlerFactory;
import co.uk.stefanpuia.backupr.engine.source.SourceHandler;
import co.uk.stefanpuia.backupr.engine.source.SourceHandlerFactory;
import co.uk.stefanpuia.backupr.engine.state.BackupStateLoader;
import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;
import java.io.File;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class BackupEngineTest {
  private static final TestLogger LOGGER = (TestLogger) LoggerFactory.getLogger(BackupEngine.class);

  @Mock private SourceHandlerFactory sourceHandlerFactory;
  @Mock private RemoteHandlerFactory remoteHandlerFactory;
  @Mock private SourceHandler sourceHandler;
  @Mock private RemoteHandler remoteHandler;
  @Mock private ConfigSource configSource;
  @Mock private ConfigRemote configRemote;
  @Mock private BackupSession backupSession;
  @Mock private BackupStateLoader backupStateLoader;
  @InjectMocks private BackupEngine backupEngine;
  private BackuprConfig backuprConfig;

  @BeforeEach
  void setUp() {
    backuprConfig = ImmutableBackuprConfig.of(null, List.of(configRemote), List.of(configSource));
    LOGGER.clear();
  }

  @Test
  void shouldNotUploadToRemoteWhenNoFilesFound() {
    // Given
    doReturn(true).when(configSource).isEnabled();
    doReturn(List.of(configRemote)).when(configSource).getRemotes();
    doReturn(sourceHandler).when(sourceHandlerFactory).getInstance(configSource);
    doReturn(Set.of()).when(sourceHandler).getFiles();

    // When
    backupEngine.execute(backuprConfig);

    // Then
    verifyNoInteractions(remoteHandlerFactory);
    then(LOGGER.getLoggingEvents())
        .isNotEmpty()
        .extracting(LoggingEvent::getMessage)
        .containsExactly(
            "Beginning backup process",
            "Backing up source '{}'",
            "No files found for source '{}'",
            "Backup process completed");
  }

  @Test
  void shouldIgnoreWhenNoRemotesDefined() {
    // Given
    doReturn(true).when(configSource).isEnabled();
    doReturn(List.of()).when(configSource).getRemotes();

    // When
    backupEngine.execute(backuprConfig);

    // Then
    verifyNoInteractions(remoteHandlerFactory);
    then(LOGGER.getLoggingEvents())
        .isNotEmpty()
        .extracting(LoggingEvent::getMessage)
        .containsExactly(
            "Beginning backup process",
            "Ignoring source '{}' because it does not have any remotes",
            "Backup process completed");
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void shouldUploadWhenFilesFound(final boolean dryRun) {
    // Given
    final var sourceFilePath = "foo-bar-123.json";
    doReturn(true).when(configSource).isEnabled();
    doReturn(true).when(configRemote).isEnabled();
    doReturn(sourceHandler).when(sourceHandlerFactory).getInstance(configSource);
    doReturn(List.of(configRemote)).when(configSource).getRemotes();
    doReturn(remoteHandler).when(remoteHandlerFactory).getInstance(configRemote);
    doReturn(Set.of(new File(sourceFilePath))).when(sourceHandler).getFiles();
    doReturn(dryRun).when(backupSession).isDry();

    // When
    backupEngine.execute(backuprConfig);

    // Then
    verify(remoteHandler).upload(configSource, Set.of(new File(sourceFilePath)));
    then(LOGGER.getLoggingEvents())
        .isNotEmpty()
        .extracting(LoggingEvent::getMessage)
        .containsExactly(
            dryRun ? "Beginning backup process (dry)" : "Beginning backup process",
            "Backing up source '{}'",
            "Found {} source files:",
            sourceFilePath,
            "Executing transformations",
            "Backing up {} files to remotes:",
            sourceFilePath,
            "Backing up to {} remote '{}'",
            "Finished backup to {} remote '{}'",
            "Cleaning up remotes for source: '{}'",
            "Backup process completed");
  }

  @Test
  void shouldNotUploadWhenRemoteDisabled() {
    // Given
    final var sourceFilePath = "foo-bar-123.json";
    doReturn(true).when(configSource).isEnabled();
    doReturn(false).when(configRemote).isEnabled();
    doReturn(sourceHandler).when(sourceHandlerFactory).getInstance(configSource);
    doReturn(List.of(configRemote)).when(configSource).getRemotes();
    doReturn(Set.of(new File(sourceFilePath))).when(sourceHandler).getFiles();

    // When
    backupEngine.execute(backuprConfig);

    // Then
    then(LOGGER.getLoggingEvents())
        .isNotEmpty()
        .extracting(LoggingEvent::getMessage)
        .containsExactly(
            "Beginning backup process",
            "Backing up source '{}'",
            "Found {} source files:",
            sourceFilePath,
            "Executing transformations",
            "Backing up {} files to remotes:",
            sourceFilePath,
            "Ignoring remote '{}' because it is disabled",
            "Cleaning up remotes for source: '{}'",
            "Backup process completed");
  }

  @Test
  void shouldNotReadWhenSourceDisabled() {
    // Given
    doReturn(false).when(configSource).isEnabled();

    // When
    backupEngine.execute(backuprConfig);

    // Then
    then(LOGGER.getLoggingEvents())
        .isNotEmpty()
        .extracting(LoggingEvent::getMessage)
        .containsExactly(
            "Beginning backup process",
            "Ignoring source '{}' because it is disabled",
            "Backup process completed");
  }
}
