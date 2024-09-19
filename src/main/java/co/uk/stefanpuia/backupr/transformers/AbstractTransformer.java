package co.uk.stefanpuia.backupr.transformers;

public abstract class AbstractTransformer implements Transformer {
  private boolean isDryRun = false;

  @Override
  public AbstractTransformer setDry(final boolean dry) {
    isDryRun = dry;
    return this;
  }

  protected boolean isDryRun() {
    return this.isDryRun;
  }
}
