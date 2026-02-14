package co.uk.stefanpuia.backupr.config.model.cleanup;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class KeepAllCleanup implements Cleanup {
  public static KeepAllCleanup create() {
    return ImmutableKeepAllCleanup.builder().build();
  }

  @Override
  @Value.Default
  public boolean isEnabled() {
    return true;
  }

  @Override
  public CleanupType getType() {
    return CleanupType.KEEP_ALL;
  }

  @Override
  public String toString() {
    return "Keep All";
  }
}
