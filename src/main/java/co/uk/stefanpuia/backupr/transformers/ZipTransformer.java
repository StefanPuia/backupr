package co.uk.stefanpuia.backupr.transformers;

import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ZipTransformer implements Transformer {

  @Override
  public Set<File> transform(final ConfigSource source, final Set<File> files) {
    try {
      final var zip = createZipFile(source);
      createZipContents(files, zip);
      return Set.of(zip);
    } catch (final IOException e) {
      throw new TransformerException(e);
    }
  }

  private File createZipFile(final ConfigSource source) throws IOException {
    log.debug("Creating temporary zip file");
    final var tempDir = Files.createTempDirectory("zip-temp");
    return Path.of(tempDir.toString(), source.name() + ".zip").toFile();
  }

  private void createZipContents(final Set<File> files, final File zip) throws IOException {
    try (final var out = new ZipOutputStream(new FileOutputStream(zip))) {
      for (final var file : files) {
        log.debug("Appending file to archive '{}'", file);
        final var zipEntry = new ZipEntry(file.getName());
        out.putNextEntry(zipEntry);

        try (final var fileInputStream = new FileInputStream(file)) {
          final var data = fileInputStream.readAllBytes();
          out.write(data, 0, data.length);
          out.closeEntry();
        }
      }
    }
  }
}
