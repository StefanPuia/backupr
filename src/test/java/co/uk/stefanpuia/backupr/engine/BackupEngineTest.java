package co.uk.stefanpuia.backupr.engine;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.remote.RemoteHandler;
import co.uk.stefanpuia.backupr.remote.RemoteHandlerFactory;
import co.uk.stefanpuia.backupr.source.SourceHandler;
import co.uk.stefanpuia.backupr.source.SourceHandlerFactory;
import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;
import java.io.File;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
  @InjectMocks private BackupEngine backupEngine;
  private BackuprConfig backuprConfig;

  @BeforeEach
  void setUp() {
    backuprConfig = new BackuprConfig(List.of(configRemote), List.of(configSource));
  }

  @Test
  void shouldNotUploadToRemoteWhenNoFilesFound() {
    // Given
    doReturn(sourceHandler).when(sourceHandlerFactory).getInstance(configSource);
    doReturn(List.of()).when(sourceHandler).getFiles();
    LOGGER.clear();

    // When
    backupEngine.execute(false, backuprConfig);

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
  void shouldUploadWhenFilesFound() {
    // Given
    doReturn(sourceHandler).when(sourceHandlerFactory).getInstance(configSource);
    doReturn(List.of(configRemote)).when(configSource).remotes(backuprConfig);
    doReturn(remoteHandler).when(remoteHandlerFactory).getInstance(configRemote);
    doReturn(List.of(new File("aa"))).when(sourceHandler).getFiles();
    LOGGER.clear();

    // When
    backupEngine.execute(false, backuprConfig);

    // Then
    verify(remoteHandler).upload(List.of(new File("aa")));
    then(LOGGER.getLoggingEvents())
        .isNotEmpty()
        .extracting(LoggingEvent::getMessage)
        .containsExactly(
            "Beginning backup process",
            "Backing up source '{}'",
            "Backing up files '{}'",
            "Backing up to remote '{}'",
            "Backup process completed");
  }
}
