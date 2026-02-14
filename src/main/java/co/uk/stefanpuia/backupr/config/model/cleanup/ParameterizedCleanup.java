package co.uk.stefanpuia.backupr.config.model.cleanup;

import co.uk.stefanpuia.backupr.config.model.ModelStyle;
import org.immutables.value.Value;

@ModelStyle
@Value.Immutable
public abstract class ParameterizedCleanup implements Cleanup {
  public abstract Integer getKeepCount();

  public abstract Integer getKeepDays();

  @Override
  public CleanupType getType() {
    return CleanupType.PARAMETERIZED;
  }

  @Override
  public String toString() {
    return "%sKeep last %d files, keep files for %d days"
        .formatted(getName() != null ? getName() + ": " : "", getKeepCount(), getKeepDays());
  }
}
