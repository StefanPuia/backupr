package co.uk.stefanpuia.backupr.engine.remote;

import co.uk.stefanpuia.backupr.config.model.remote.GitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.core.StringTemplateRenderer;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import co.uk.stefanpuia.backupr.engine.remote.mapper.JgitCredentialsProviderMapper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.SubmoduleConfig.FetchRecurseSubmodulesMode;
import org.eclipse.jgit.transport.RefSpec;

@Slf4j
@AllArgsConstructor
public class GitRemoteHandler extends AbstractRemoteHandler {
  private static final String REMOTE_NAME = "origin";
  private final GitConfigRemote remote;
  private final BackupHelper backupHelper;
  private final JgitCredentialsProviderMapper credentialsProviderMapper;
  private final StringTemplateRenderer stringTemplateRenderer;

  @Override
  public void upload(final ConfigSource source, final Set<File> files) {
    try (final var repo = initRepository()) {
      appendFiles(repo, source, files);
      commitAndPush(repo, source);
    } catch (IOException | URISyntaxException | GitAPIException e) {
      throw new RemoteHandlerException(e);
    }
  }

  private Git initRepository() throws IOException, URISyntaxException, GitAPIException {
    final var repoDir = Files.createTempDirectory("").toFile();
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

  private void appendFiles(final Git repo, final ConfigSource source, final Set<File> files)
      throws IOException {
    for (final var file : files) {
      final var targetPath =
          Path.of(
              repo.getRepository().getDirectory().getParent(),
              backupHelper.getRelativePath(source.getBasePath(), file));
      log.debug("Backing up '{}' to '{}'", file, targetPath);
      backupHelper.mkdirp(
          targetPath.getParent(),
          "Could not create parent directory: '%s'".formatted(targetPath.getParent()));
      Files.copy(file.toPath(), targetPath);
    }
  }

  private void commitAndPush(final Git git, final ConfigSource source) throws GitAPIException {
    log.debug("Creating commit");
    git.add().addFilepattern(".").call();
    git.commit().setMessage(getCommitMessage(source)).call();
    log.debug("Pushing commit");
    if (isDryRun()) return;
    git.push()
        .setCredentialsProvider(credentialsProviderMapper.convert(remote.getCredentials()))
        .call();
  }

  private String getCommitMessage(final ConfigSource source) {
    return stringTemplateRenderer.applyTemplate(
        remote.getCommitMessagePattern(),
        remote.getVariables().withContext(Map.of("sourceName", source.getName())));
  }
}
