package co.uk.stefanpuia.backupr.engine.transformers;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.transformer.TarGzConfigTransformerOptions;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

@Slf4j
@AllArgsConstructor
public class TarGzTransformer extends AbstractTransformer {
  private final TarGzConfigTransformerOptions options;
  private final BackupHelper backupHelper;
  private final StringTemplateRenderer stringTemplateRenderer;

  @Override
  public TransformerType getType() {
    return TransformerType.TARGZ;
  }

  @Override
  public Set<File> transform(final ConfigSource source, final Set<File> files) {
    try {
      final var archive = createArchive(source);
      archive.deleteOnExit();
      createArchiveContents(source, files, archive);
      return Set.of(archive);
    } catch (final IOException e) {
      throw new TransformerException(e);
    }
  }

  private File createArchive(final ConfigSource source) throws IOException {
    log.debug("Creating temporary tar.gz file");
    final var tempDir = Files.createTempDirectory("targz-temp");
    return Path.of(tempDir.toString(), getFilename(source)).toFile();
  }

  private void createArchiveContents(
      final ConfigSource source, final Set<File> files, final File archive) throws IOException {

    try (final var fos = Files.newOutputStream(archive.toPath());
        final var bufferedOutputStream = new BufferedOutputStream(fos);
        final var gzipOutputStream = new GzipCompressorOutputStream(bufferedOutputStream);
        final var tarArchiveOutputStream = new TarArchiveOutputStream(gzipOutputStream)) {
      for (final var file : files) {
        log.debug("Appending file '{}' to archive", file);
        if (isDryRun()) continue;
        addFilesToTarGz(
            file.toPath(),
            backupHelper.getRelativePath(source.getBasePath(), file),
            tarArchiveOutputStream);
      }
    }
  }

  private void addFilesToTarGz(
      final Path path, final String entryName, final TarArchiveOutputStream outputStream)
      throws IOException {
    final var entry = new TarArchiveEntry(path.toFile(), entryName);
    outputStream.putArchiveEntry(entry);

    if (Files.isRegularFile(path)) {
      // add file
      try (final var inputStream = Files.newInputStream(path)) {
        final var buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, len);
        }
      }
      outputStream.closeArchiveEntry();
    } else {
      outputStream.closeArchiveEntry();
      // walk directory
      try (final var stream = Files.newDirectoryStream(path)) {
        for (Path child : stream) {
          addFilesToTarGz(child, entryName + "/", outputStream);
        }
      }
    }
  }

  private String getFilename(final ConfigSource source) {
    return stringTemplateRenderer.applyTemplate(
        options.getFilenamePattern(),
        options.getVariables().withContext(Map.of("sourceName", source.getName())));
  }
}
