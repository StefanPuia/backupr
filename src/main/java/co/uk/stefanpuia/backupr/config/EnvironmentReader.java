package co.uk.stefanpuia.backupr.config;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class EnvironmentReader {

  public static final String ENV_CONFIG_LOCATION = "BACKUPR_CONFIG_LOCATION";

  public Optional<String> getConfigLocation() {
    return Optional.ofNullable(System.getenv(ENV_CONFIG_LOCATION))
        .filter(Predicate.not(String::isBlank))
        .map(String::trim)
        .map(
            envValue -> {
              log.debug(
                  "Config location found in environment '{}': '{}'", ENV_CONFIG_LOCATION, envValue);
              return envValue;
            });
  }
}
