package co.uk.stefanpuia.backupr.engine.source;

import static org.apache.commons.io.IOCase.INSENSITIVE;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.source.LocalConfigSource;
import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

@Slf4j
@AllArgsConstructor
public class LocalSourceHandler implements SourceHandler {
  private static final Pattern PATH_PATTERN = Pattern.compile("^(?:(?<dir>.*)/)?(?<file>.+)$");
  private final LocalConfigSource source;

  @Override
  public Set<File> getFiles() {
    final File directory = source.getBasePath().toFile();
    if (!directory.exists()) {
      log.warn("'{}' does not exist", directory);
      return Set.of();
    }
    if (!directory.isDirectory()) {
      throw new SourceHandlerException("'%s' is not a directory".formatted(directory));
    }

    if (source.getFiles().isEmpty()) {
      return Set.of(directory);
    }

    final Set<File> foundFiles = new HashSet<>();

    source
        .getFiles()
        .forEach(
            pattern -> {
              final var matcher = PATH_PATTERN.matcher(pattern);
              if (!matcher.matches()) {
                throw new ConfigValidationException(
                    "Pattern '%s' cannot be used to match a file".formatted(pattern));
              }

              final var dirFilter =
                  Optional.ofNullable(matcher.group("dir"))
                      .<IOFileFilter>map(dir -> new WildcardFileFilter(dir, INSENSITIVE))
                      .orElse(TrueFileFilter.INSTANCE);
              final var fileFilter = new WildcardFileFilter(matcher.group("file"), INSENSITIVE);

              FileUtils.iterateFiles(directory, fileFilter, dirFilter)
                  .forEachRemaining(foundFiles::add);
            });

    return foundFiles;
  }
}
