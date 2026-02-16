package co.uk.stefanpuia.backupr.engine.source;

import co.uk.stefanpuia.backupr.config.model.source.DockerExecConfigSource;
import co.uk.stefanpuia.backupr.engine.adapters.DockerAdapter;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.util.StreamUtils;

@Slf4j
@AllArgsConstructor
public class DockerExecSourceHandler implements SourceHandler {
  private final DockerExecConfigSource source;
  private final DockerAdapter dockerAdapter;

  @Override
  public Set<File> getFiles() {
    final Set<File> sourcedFiles = new HashSet<>();
    try {
      final var containerId = dockerAdapter.getContainerId(source.getContainer());

      final var directory = source.getBasePath();
      for (final var command : source.getCommands()) {
        execToFile(containerId, command, directory);
      }

      FileUtils.iterateFiles(directory.toFile(), TrueFileFilter.TRUE, TrueFileFilter.TRUE)
          .forEachRemaining(sourcedFiles::add);
      return sourcedFiles;
    } catch (NotFoundException ignored) {
      log.warn("Container not found: {}", source.getContainer());
      return Set.of();
    } catch (DockerException | IOException | InterruptedException e) {
      throw new SourceHandlerException(e);
    }
  }

  private void execToFile(
      final String containerId,
      final DockerExecConfigSource.Command command,
      final Path targetDirectory)
      throws InterruptedException, IOException {
    final var dockerClient = dockerAdapter.getDockerClient();
    final var execCreateCmdResponse =
        dockerClient
            .execCreateCmd(containerId)
            .withAttachStderr(true)
            .withAttachStdout(true)
            .withCmd(command.command())
            .exec();
    final var execId = execCreateCmdResponse.getId();

    final var outputFile = targetDirectory.resolve(command.outputFile()).toFile();
    Files.createDirectories(outputFile.getParentFile().toPath());

    try (final var outputStream = new FileOutputStream(outputFile)) {
      final var execStartResultCallback =
          new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(final Frame object) {
              try {
                StreamUtils.copy(object.getPayload(), outputStream);
              } catch (IOException e) {
                throw new SourceHandlerException(e);
              }
            }
          };

      final var execStartCmd =
          dockerClient
              .execStartCmd(execId)
              .withDetach(false) // Keep attached to the output
              .withTty(false); // No pseudo-TTY

      execStartCmd
          .exec(execStartResultCallback)
          .awaitCompletion(source.getTimeoutInSeconds(), TimeUnit.SECONDS);
    }
  }
}
