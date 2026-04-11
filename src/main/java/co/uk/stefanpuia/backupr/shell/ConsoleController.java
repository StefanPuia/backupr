package co.uk.stefanpuia.backupr.shell;

import static co.uk.stefanpuia.backupr.shell.ShellExceptionResolver.SHELL_EXCEPTION_RESOLVER;

import co.uk.stefanpuia.backupr.config.JsonSchemaWriter;
import co.uk.stefanpuia.backupr.engine.BackupDelegate;
import co.uk.stefanpuia.backupr.engine.BackupSession;
import lombok.AllArgsConstructor;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ConsoleController {
  private static final char OPTION_CONFIG_SHORT = 'c';
  private static final String OPTION_CONFIG_LONG = "config";
  private static final String OPTION_CONFIG_DESCRIPTION =
      "Configuration file location (defaults to '{user home}/.backupr.json'";
  private static final char OPTION_VERBOSE_SHORT = 'v';
  private static final String OPTION_VERBOSE_LONG = "verbose";
  private static final String OPTION_VERBOSE_DESCRIPTION =
      "Print verbose output throughout the process.";

  private final BackupDelegate backupDelegate;
  private final JsonSchemaWriter schemaWriter;
  private final BackupSession backupSession;

  @Command(
      group = "backupr",
      name = "backup",
      description = "Start the backup process.",
      exitStatusExceptionMapper = SHELL_EXCEPTION_RESOLVER)
  public void backup(
      final @Option(
              shortName = 'd',
              longName = "dry",
              description =
                  "Execute the backup process, scan for target files but skip uploading to any"
                      + " remotes.") boolean dry,
      final @Option(
              shortName = OPTION_CONFIG_SHORT,
              longName = OPTION_CONFIG_LONG,
              description = OPTION_CONFIG_DESCRIPTION) String configPath,
      final @Option(
              shortName = OPTION_VERBOSE_SHORT,
              longName = OPTION_VERBOSE_LONG,
              description = OPTION_VERBOSE_DESCRIPTION) boolean verbose) {
    changeLogLevel(verbose);
    backupSession.setDry(dry);
    backupDelegate.executeBackup(configPath);
  }

  @Command(
      group = "backupr",
      name = "validate",
      description = "Tries to read and validate the configuration file.",
      exitStatusExceptionMapper = SHELL_EXCEPTION_RESOLVER)
  public String validateConfig(
      final @Option(
              shortName = OPTION_CONFIG_SHORT,
              longName = OPTION_CONFIG_LONG,
              description = OPTION_CONFIG_DESCRIPTION) String configPath,
      final @Option(
              shortName = OPTION_VERBOSE_SHORT,
              longName = OPTION_VERBOSE_LONG,
              description = OPTION_VERBOSE_DESCRIPTION) boolean verbose) {
    changeLogLevel(verbose);
    final var validConfigPath = backupDelegate.validateConfig(configPath);
    return "Configuration file at '%s' is valid.".formatted(validConfigPath);
  }

  @Command(
      group = "backupr",
      name = "schema",
      description = "Get the JSON schema for the configuration file.",
      exitStatusExceptionMapper = SHELL_EXCEPTION_RESOLVER)
  public String generateJSONSchema() {
    return schemaWriter.generate();
  }

  private void changeLogLevel(final boolean verbose) {
    if (verbose) {
      LoggingSystem.get(this.getClass().getClassLoader())
          .setLogLevel("co.uk.stefanpuia.backupr", LogLevel.DEBUG);
    }
  }
}
