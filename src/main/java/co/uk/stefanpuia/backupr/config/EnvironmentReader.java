package co.uk.stefanpuia.backupr.config;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EnvironmentReader {

  public static final String ENV_CONFIG_LOCATION = "BACKUPR_CONFIG_LOCATION";

  public Optional<String> getConfigLocation() {
    return Optional.ofNullable(System.getenv(ENV_CONFIG_LOCATION))
        .filter(Predicate.not(String::isBlank));
  }
}
