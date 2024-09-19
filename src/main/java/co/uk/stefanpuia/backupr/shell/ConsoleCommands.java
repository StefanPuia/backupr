package co.uk.stefanpuia.backupr.shell;

import co.uk.stefanpuia.backupr.engine.BackupDelegate;
import lombok.AllArgsConstructor;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;

@Command(group = "Backupr")
@AllArgsConstructor
public class ConsoleCommands {
  private static final char OPTION_PATH_SHORT = 'p';
  private static final String OPTION_PATH_LONG = "path";
  private static final String OPTION_PATH_DESCRIPTION =
      "Configuration file location (defaults to '{user home}/.backupr.json'";
  private static final char OPTION_VERBOSE_SHORT = 'v';
  private static final String OPTION_VERBOSE_LONG = "verbose";
  private static final String OPTION_VERBOSE_DESCRIPTION =
      "Print verbose output throughout the process.";

  private final BackupDelegate backupDelegate;

  @Command(description = "Start the backup process.")
  public String backup(
      final @Option(
              shortNames = 'd',
              longNames = "dry",
              description =
                  "Execute the backup process, scan for target files but skip uploading to any remotes.")
          boolean dry,
      final @Option(
              shortNames = OPTION_PATH_SHORT,
              longNames = OPTION_PATH_LONG,
              description = OPTION_PATH_DESCRIPTION) String configPath,
      final @Option(
              shortNames = OPTION_VERBOSE_SHORT,
              longNames = OPTION_VERBOSE_LONG,
              description = OPTION_VERBOSE_DESCRIPTION) boolean verbose) {
    backupDelegate.executeBackup(dry, configPath);
    return "Backup successful";
  }

  @Command(command = "validate", description = "Tries to read and validate the configuration file.")
  public String validateConfig(
      final @Option(
              shortNames = OPTION_PATH_SHORT,
              longNames = OPTION_PATH_LONG,
              description = OPTION_PATH_DESCRIPTION) String configPath,
      final @Option(
              shortNames = OPTION_VERBOSE_SHORT,
              longNames = OPTION_VERBOSE_LONG,
              description = OPTION_VERBOSE_DESCRIPTION) boolean verbose) {
    final var validConfigPath = backupDelegate.validateConfig(configPath);
    return "Configuration file at '%s' is valid.".formatted(validConfigPath);
  }
}
