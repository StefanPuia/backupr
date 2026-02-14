package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.GitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.config.model.state.BackupState;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.BackupSession;
import co.uk.stefanpuia.backupr.engine.remote.mapper.JgitCredentialsProviderMapper;
import co.uk.stefanpuia.backupr.engine.state.BackupStateRecord;
import co.uk.stefanpuia.backupr.engine.state.dto.BackupStateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.SubmoduleConfig.FetchRecurseSubmodulesMode;
import org.eclipse.jgit.transport.RefSpec;
import org.springframework.beans.factory.DisposableBean;

@Slf4j
public class GitRemoteHandler implements RemoteHandler, DisposableBean {
  private static final String REMOTE_NAME = "origin";
  private final GitConfigRemote remote;
  private final BackupHelper backupHelper;
  private final JgitCredentialsProviderMapper credentialsProviderMapper;
  private final StringTemplateRenderer stringTemplateRenderer;
  private final BackupSession backupSession;
  private final ObjectMapper jsonMapper;
  private final Git repository;

  public GitRemoteHandler(
      final GitConfigRemote remote,
      final BackupHelper backupHelper,
      final JgitCredentialsProviderMapper credentialsProviderMapper,
      final StringTemplateRenderer stringTemplateRenderer,
      final BackupSession backupSession,
      final ObjectMapper jsonMapper) {
    this.remote = remote;
    this.backupHelper = backupHelper;
    this.credentialsProviderMapper = credentialsProviderMapper;
    this.stringTemplateRenderer = stringTemplateRenderer;
    this.backupSession = backupSession;
    this.jsonMapper = jsonMapper;
    try {
      repository = initRepository();
    } catch (GitAPIException | IOException | URISyntaxException e) {
      throw new RemoteHandlerException(e);
    }
  }

  @Override
  public BackupStateRecord upload(final ConfigSource source, final Set<File> files) {
    try {
      final var paths = appendFiles(source, files);
      commitAndPush(source);
      return new BackupStateRecord(source.getName(), remote.getName(), paths);
    } catch (GitAPIException | IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  @Override
  public void cleanup(final ConfigSource source, @Nullable final BackupState backupState) {
    throw new UnsupportedOperationException("Git remote handler does not support cleanup");
  }

  @Override
  public boolean exists(final String filePath) {
    return Path.of(repository.getRepository().getDirectory().getParent())
        .resolve(filePath)
        .toFile()
        .exists();
  }

  @Override
  public void delete(final String filePath) {}

  @Override
  public BackupStateDto readStateFile(final String stateFilePath) {
    if (!exists(stateFilePath)) {
      return null;
    }
    try {
      return jsonMapper.readValue(
          Path.of(repository.getRepository().getDirectory().getParent())
              .resolve(stateFilePath)
              .toFile(),
          BackupStateDto.class);
    } catch (IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  @Override
  public void writeStateFile(final String stateFilePath, final BackupStateDto state) {
    try {
      final var targetPath =
          Path.of(repository.getRepository().getDirectory().getParent()).resolve(stateFilePath);
      backupHelper.mkdirp(
          targetPath.getParent(),
          "Could not create parent directory: '%s'".formatted(targetPath.getParent()));
      final var tempFile = Files.createTempFile(targetPath.getParent(), "backupr", ".tmp");
      Files.writeString(tempFile, jsonMapper.writeValueAsString(state), StandardCharsets.UTF_8);
      Files.move(
          tempFile,
          targetPath,
          StandardCopyOption.ATOMIC_MOVE,
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RemoteHandlerException(e);
    }
  }

  private Git initRepository() throws IOException, URISyntaxException, GitAPIException {
    final var repoDir = backupHelper.createTempDirectory("git-remote").toFile();
    repoDir.deleteOnExit();
    log.debug("Initializing empty repository at '{}'", repoDir);
    try (final var git = Git.init().setDirectory(repoDir).call()) {

      log.debug("Setting remote to '{}'", remote.originUri());
      git.remoteAdd().setName(REMOTE_NAME).setUri(remote.originUri()).call();

      log.debug("Fetching '{}' shallowly", remote.getBranch());
      git.fetch()
          .setForceUpdate(true)
          .setRemote(REMOTE_NAME)
          .setRecurseSubmodules(FetchRecurseSubmodulesMode.NO)
          .setInitialBranch(remote.getBranch())
          .setRefSpecs(
              new RefSpec("refs/heads/" + remote.getBranch() + ":refs/heads/" + remote.getBranch()))
          .setDepth(1)
          .setCredentialsProvider(credentialsProviderMapper.convert(remote.getCredentials()))
          .call();

      log.debug("Checking out branch '{}'", remote.getBranch());
      git.checkout()
          .setForced(true)
          .setName(remote.getBranch())
          .setUpstreamMode(SetupUpstreamMode.TRACK)
          .call();

      return git;
    }
  }

  private List<String> appendFiles(final ConfigSource source, final Set<File> files)
      throws IOException {
    final var targetPaths = new ArrayList<String>();
    for (final var file : files) {
      final var targetPath =
          Path.of(repository.getRepository().getDirectory().getParent())
              .resolve(backupHelper.getRelativePathIncludingFilename(source.getBasePath(), file));
      log.debug("Backing up '{}' to '{}'", file, targetPath);
      backupHelper.mkdirp(
          targetPath.getParent(),
          "Could not create parent directory: '%s'".formatted(targetPath.getParent()));
      Files.copy(file.toPath(), targetPath);
      targetPaths.add(backupHelper.toString(targetPath));
    }
    return targetPaths;
  }

  private void commitAndPush(final ConfigSource source) throws GitAPIException {
    log.debug("Creating commit");
    repository.add().addFilepattern(".").call();
    repository.commit().setMessage(getCommitMessage(source)).call();
    log.debug("Pushing commit");
    if (backupSession.isDry()) return;
    repository
        .push()
        .setCredentialsProvider(credentialsProviderMapper.convert(remote.getCredentials()))
        .call();
  }

  private String getCommitMessage(final ConfigSource source) {
    return stringTemplateRenderer.applyTemplate(
        remote.getCommitMessagePattern(),
        remote.getVariables().withContext(Map.of("sourceName", source.getName())));
  }

  @Override
  public void destroy() {
    repository.close();
  }
}
