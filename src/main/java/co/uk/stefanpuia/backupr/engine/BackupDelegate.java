package co.uk.stefanpuia.backupr.engine;

import co.uk.stefanpuia.backupr.config.ConfigFileProvider;
import co.uk.stefanpuia.backupr.config.exception.ConfigFileNotFoundException;
import co.uk.stefanpuia.backupr.config.reader.ConfigReader;
import co.uk.stefanpuia.backupr.engine.state.BackupStateLoader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class BackupDelegate {
  private final ConfigFileProvider configFileProvider;
  private final ConfigReader configReader;
  private final BackupEngine backupEngine;
  private final BackupStateLoader backupStateLoader;

  public String validateConfig(final String configPath) {
    try {
      final var resolvedConfigPath = configFileProvider.resolveConfigPath(configPath);
      configReader.readConfig(new FileInputStream(resolvedConfigPath));
      return resolvedConfigPath;
    } catch (FileNotFoundException e) {
      throw new ConfigFileNotFoundException(configPath);
    }
  }

  public void executeBackup(final String configPath) {
    final var readConfig =
        configReader.readConfig(configFileProvider.getConfigInputStream(configPath));
    final var config = backupStateLoader.initialiseState(readConfig);
    try {
      backupEngine.execute(config);
    } finally {
      backupStateLoader.writeState(config);
    }
  }
}
