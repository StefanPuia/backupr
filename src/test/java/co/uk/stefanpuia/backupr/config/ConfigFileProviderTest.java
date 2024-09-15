package co.uk.stefanpuia.backupr.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.Mockito.doReturn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.io.IOUtil;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ConfigFileProviderTest {
  @Mock EnvironmentReader environmentReader;
  @InjectMocks @Spy ConfigFileProvider configFileProvider;

  @Test
  void shouldReadConfigFile() throws IOException {
    // Given
    final var contents = RandomString.make();
    final var tempFile = File.createTempFile("backupr-", ".json");
    tempFile.deleteOnExit();
    IOUtil.writeText(contents, tempFile);
    doReturn(Optional.of(tempFile.getAbsolutePath())).when(environmentReader).getConfigLocation();

    // When
    final var stream = configFileProvider.getConfigInputStream();

    // Then
    then(stream).asString(UTF_8).isEqualTo(contents);
  }

  @Test
  void shouldThrowIfFileDoesNotExist() {
    // Given
    doReturn(Optional.of("foo.json")).when(environmentReader).getConfigLocation();

    // When - Then
    thenThrownBy(() -> configFileProvider.getConfigInputStream())
        .isInstanceOf(FileNotFoundException.class);
  }

  @Test
  void shouldUseDefaultWhenEnvironmentNotProvided() throws IOException {
    // Given
    final var contents = RandomString.make();
    final var tempFile = File.createTempFile("backupr-", ".json");
    tempFile.deleteOnExit();
    IOUtil.writeText(contents, tempFile);
    doReturn(Optional.empty()).when(environmentReader).getConfigLocation();
    doReturn(tempFile.getAbsolutePath()).when(configFileProvider).getDefaultLocation();

    // When
    final var stream = configFileProvider.getConfigInputStream();

    // Then
    then(stream).asString(UTF_8).isEqualTo(contents);
  }

  @Test
  void shouldGetDefaultLocation() {
    // When
    final var location = configFileProvider.getDefaultLocation();

    // Then
    then(location).isEqualTo(Path.of(System.getProperty("user.home"), ".backupr.json").toString());
  }
}
