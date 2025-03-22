package co.uk.stefanpuia.backupr.source;

import static org.assertj.core.api.BDDAssertions.then;

import co.uk.stefanpuia.backupr.config.model.source.ImmutableLocalConfigSource;
import co.uk.stefanpuia.backupr.engine.source.LocalSourceHandler;
import java.nio.file.Path;
import java.util.List;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LocalSourceHandlerTest {
  @Test
  void shouldReturnDirectoryWhenNoFilePatternsSupplied() {
    // Given
    final var tempDir = Files.newTemporaryFolder();
    final var handler =
        new LocalSourceHandler(
            ImmutableLocalConfigSource.builder()
                .setName("foo")
                .setEnabled(true)
                .setDirectory(tempDir.getAbsolutePath())
                .build());

    // When
    final var files = handler.getFiles();

    // Then
    then(files).hasSize(1).containsExactly(tempDir);
  }

  @Test
  void shouldReturnFilesWhenPatternsSupplied() {
    // Given
    final var tempDir = Files.newTemporaryFolder();
    final var file1 = Files.newFile(Path.of(tempDir.getAbsolutePath(), "a.foo").toString());
    final var handler =
        new LocalSourceHandler(
            ImmutableLocalConfigSource.builder()
                .setName("foo")
                .setEnabled(true)
                .setDirectory(tempDir.getAbsolutePath())
                .setFiles(List.of("a.foo"))
                .build());

    // When
    final var files = handler.getFiles();

    // Then
    then(files).hasSize(1).containsExactly(file1);
  }

  @Test
  void shouldReturnFilteredFilesWhenPatternsSupplied() {
    // Given
    final var tempDir = Files.newTemporaryFolder();
    final var file1 = Files.newFile(Path.of(tempDir.getAbsolutePath(), "a.foo").toString());
    final var file2 = Files.newFile(Path.of(tempDir.getAbsolutePath(), "b.foo").toString());
    Files.newFile(Path.of(tempDir.getAbsolutePath(), "a.bar").toString());
    final var handler =
        new LocalSourceHandler(
            ImmutableLocalConfigSource.builder()
                .setName("foo")
                .setEnabled(true)
                .setDirectory(tempDir.getAbsolutePath())
                .setFiles(List.of("*.foo"))
                .build());

    // When
    final var files = handler.getFiles();

    // Then
    then(files).hasSize(2).containsExactlyInAnyOrder(file1, file2);
  }

  @Test
  void shouldReturnFilteredFilesWithinDirectoriesWhenPatternsSupplied() {
    // Given
    final var tempDir = Files.newTemporaryFolder();
    Files.newFile(Path.of(tempDir.getAbsolutePath(), "a.bar").toString());
    final var tempSubDir = Files.newFolder(Path.of(tempDir.getAbsolutePath(), "subdir").toString());
    final var file1 = Files.newFile(Path.of(tempSubDir.getAbsolutePath(), "a.foo").toString());
    final var file2 = Files.newFile(Path.of(tempSubDir.getAbsolutePath(), "b.foo").toString());
    Files.newFile(Path.of(tempDir.getAbsolutePath(), "q.bar").toString());
    final var handler =
        new LocalSourceHandler(
            ImmutableLocalConfigSource.builder()
                .setName("foo")
                .setEnabled(true)
                .setDirectory(tempDir.getAbsolutePath())
                .setFiles(List.of("subdir/*.foo"))
                .build());

    // When
    final var files = handler.getFiles();

    // Then
    then(files).hasSize(2).containsExactlyInAnyOrder(file1, file2);
  }

  @Test
  void shouldReturnFilteredFilesWithinMultipleLayerDirectoriesWhenPatternsSupplied() {
    // Given
    final var tempDir = Files.newTemporaryFolder();
    Files.newFile(Path.of(tempDir.getAbsolutePath(), "a.bar").toString());
    final var tempSubDir1 =
        Files.newFolder(Path.of(tempDir.getAbsolutePath(), "subdir1").toString());
    final var file1 = Files.newFile(Path.of(tempSubDir1.getAbsolutePath(), "a.foo").toString());
    final var tempSubDir2 =
        Files.newFolder(Path.of(tempSubDir1.getAbsolutePath(), "subdir2").toString());
    final var file2 = Files.newFile(Path.of(tempSubDir2.getAbsolutePath(), "b.foo").toString());
    final var file3 = Files.newFile(Path.of(tempSubDir2.getAbsolutePath(), "c.foo").toString());
    Files.newFile(Path.of(tempDir.getAbsolutePath(), "q.bar").toString());
    final var handler =
        new LocalSourceHandler(
            ImmutableLocalConfigSource.builder()
                .setName("foo")
                .setEnabled(true)
                .setDirectory(tempDir.getAbsolutePath())
                .setFiles(List.of("**/*.foo"))
                .build());

    // When
    final var files = handler.getFiles();

    // Then
    then(files).hasSize(3).containsExactlyInAnyOrder(file1, file2, file3);
  }
}
