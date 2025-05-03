package co.uk.stefanpuia.backupr.engine.remote;

import static co.uk.stefanpuia.backupr.test.utils.TestObjects.azureStorageBlobConfigRemote;
import static co.uk.stefanpuia.backupr.test.utils.TestObjects.configSource;
import static org.assertj.core.api.BDDAssertions.then;

import co.uk.stefanpuia.backupr.config.model.remote.AzureStorageBlobConfigRemote;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.adapters.AzureBlobClientProvider;
import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;
import java.io.File;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class AzureStorageBlobRemoteHandlerTest {
  private static final TestLogger LOGGER =
      (TestLogger) LoggerFactory.getLogger(AzureStorageBlobRemoteHandler.class);

  private final AzureStorageBlobConfigRemote remote = azureStorageBlobConfigRemote();
  @Mock private AzureBlobClientProvider azureBlobClientProvider;
  @InjectMocks private BackupHelper backupHelper;
  @InjectMocks private StringTemplateRenderer stringTemplateRenderer;
  private AzureStorageBlobRemoteHandler handler;

  @BeforeEach
  void setUp() {
    handler =
        new AzureStorageBlobRemoteHandler(
            remote, azureBlobClientProvider, backupHelper, stringTemplateRenderer);
    handler.setDry(true);
  }

  @Test
  void shouldHonorOriginalFileStructure() {
    // Given
    final var source = configSource();
    final var names =
        Stream.of("", "fizz/", "fizz/baz/", "baz/")
            .map(parent -> "%s%s".formatted(parent, "a.txt"))
            .toList();
    final var remotePath =
        "%s/%s/%s"
            .formatted(remote.getEndpoint(), remote.getContainer(), remote.getBlobPrefixPattern());

    // When
    handler.upload(
        source,
        names.stream()
            .map(name -> "%s/%s".formatted(source.getDirectory(), name))
            .map(File::new)
            .collect(Collectors.toSet()));

    // Then
    then(LOGGER.getLoggingEvents())
        .isNotEmpty()
        .extracting(LoggingEvent::getFormattedMessage)
        .containsAll(
            names.stream()
                .map(
                    name ->
                        "Backing up '%s' to '%s/%s'"
                            .formatted(Path.of(source.getDirectory(), name), remotePath, name))
                .toList());
  }
}
