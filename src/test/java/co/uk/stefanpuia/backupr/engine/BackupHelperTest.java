package co.uk.stefanpuia.backupr.engine;

import static org.assertj.core.api.BDDAssertions.then;

import java.nio.file.Path;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BackupHelperTest {
  @Mock BackupSession backupSession;
  @InjectMocks private BackupHelper helper;

  @Nested
  class RelativePath {
    @Test
    void shouldReturnRelativePathWithCommonParent() {
      // Given
      final var basePath = Path.of("/foo/bar");
      final var file = Path.of("/foo/bar/fooz/baz.txt").toFile();

      // When
      final var path = helper.getRelativePathIncludingFilename(basePath, file);

      // Then
      then(path).isRelative().isEqualByComparingTo(Path.of("fooz/baz.txt"));
    }

    @Test
    void shouldReturnFileNameWithoutCommonParent() {
      // Given
      final var basePath = Path.of("/foo/bar");
      final var file = Path.of("/fooz/baz.txt").toFile();

      // When
      final var path = helper.getRelativePathIncludingFilename(basePath, file);

      // Then
      then(path).isRelative().isEqualByComparingTo(Path.of("baz.txt"));
    }
  }
}
