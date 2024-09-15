package co.uk.stefanpuia.backupr.config;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EnvironmentReaderTest {
  @InjectMocks EnvironmentReader environmentReader;

  @Test
  void shouldReturnEmptyWhenNotSet() {
    // When
    final var location = environmentReader.getConfigLocation();

    // Then
    then(location).isEmpty();
  }
}
