package co.uk.stefanpuia.backupr.remote;

import static java.util.Optional.ofNullable;

import co.uk.stefanpuia.backupr.config.model.remote.GitConfigRemote;
import co.uk.stefanpuia.backupr.config.model.source.ConfigSource;
import co.uk.stefanpuia.backupr.engine.BackupHelper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.SubmoduleConfig.FetchRecurseSubmodulesMode;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

@Slf4j
@AllArgsConstructor
public class GitRemoteHandler extends AbstractRemoteHandler {
  private static final String REMOTE_NAME = "origin";
  private final GitConfigRemote remote;
  private final BackupHelper backupHelper;

  @Override
  public void upload(final ConfigSource source, final Set<File> files) {
    try (final var repo = initRepository()) {
      appendFiles(repo, source, files);
      commitAndPush(repo);
    } catch (IOException | URISyntaxException | GitAPIException e) {
      throw new RemoteHandlerException(e);
    }
  }

  private Git initRepository() throws IOException, URISyntaxException, GitAPIException {
    final var repoDir = Files.createTempDirectory("").toFile();
    // repoDir.deleteOnExit();
    log.debug("Initializing empty repository at '{}'", repoDir);
    try (final var git = Git.init().setDirectory(repoDir).call()) {

      log.debug("Setting remote to '{}'", remote.originUri());
      git.remoteAdd().setName(REMOTE_NAME).setUri(remote.originUri()).call();

      log.debug("Fetching '{}' shallowly", remote.getBranch());
      git.fetch()
          .setCredentialsProvider(credentialsProvider())
          .setForceUpdate(true)
          .setRemote(REMOTE_NAME)
          .setRecurseSubmodules(FetchRecurseSubmodulesMode.NO)
          .setInitialBranch(remote.getBranch())
          .setRefSpecs(
              new RefSpec("refs/heads/" + remote.getBranch() + ":refs/heads/" + remote.getBranch()))
          .setDepth(1)
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

  private void commitAndPush(final Git git) throws GitAPIException {
    log.debug("Creating commit");
    git.add().addFilepattern(".").call();
    git.commit().setMessage("Automatic backup").call();
    log.debug("Pushing commit");
    if (isDryRun()) return;
    git.push().setCredentialsProvider(credentialsProvider()).call();
  }

  private CredentialsProvider credentialsProvider() {
    if (remote.getCredentials().isEmpty()) return CredentialsProvider.getDefault();

    // TODO:
    // if (remote.getCredentials().get().basic() != null) {
    //   return new UsernamePasswordCredentialsProvider(
    //       remote.getCredentials().get().basic().username(),
    //       ofNullable(remote.getCredentials().get().basic().password()).orElse(""));
    // }

    return CredentialsProvider.getDefault();
  }
}
