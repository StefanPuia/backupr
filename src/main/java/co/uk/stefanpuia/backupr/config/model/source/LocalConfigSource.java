package co.uk.stefanpuia.backupr.config.model.source;

import co.uk.stefanpuia.backupr.config.exception.ConfigValidationException;
import co.uk.stefanpuia.backupr.config.model.BackuprConfig;
import co.uk.stefanpuia.backupr.config.model.SourceTransformer;
import co.uk.stefanpuia.backupr.config.model.remote.ConfigRemote;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.List;

public record LocalConfigSource(
    @NotBlank String name,
    @NotBlank String directory,
    @Nullable List<@NotBlank String> files,
    @Nullable List<@NotNull SourceTransformer> transformers,
    @Nullable List<@NotBlank String> remotes)
    implements ConfigSource {

  public List<ConfigRemote> remotes(final BackuprConfig config) {
    return config.remotes().stream().filter(remote -> remotes().contains(remote.name())).toList();
  }

  public void checkValid(final BackuprConfig config) {
    ConfigSource.super.checkValid(config);
    checkRemotesValid(config);
  }

  private void checkRemotesValid(final BackuprConfig config) {
    remotes()
        .forEach(
            remote -> {
              if (config.remotes().stream()
                  .noneMatch(definedRemote -> definedRemote.name().equals(remote))) {
                throw new ConfigValidationException(
                    "in source '%s': no remote named '%s' defined".formatted(name, remote));
              }
            });
  }

  public List<String> files() {
    return files == null
        ? List.of()
        : files.stream().map(pattern -> pattern.replaceAll("[\\\\/]", "/")).toList();
  }

  public List<SourceTransformer> transformers() {
    return transformers == null ? List.of() : transformers;
  }

  public List<String> remotes() {
    return remotes == null ? List.of() : remotes;
  }

  public Path getBasePath() {
    return Path.of(directory);
  }
}
