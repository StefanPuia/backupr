package co.uk.stefanpuia.backupr.remote;

public abstract class AbstractRemoteHandler implements RemoteHandler {
  private boolean isDryRun = false;

  @Override
  public AbstractRemoteHandler setDry(final boolean dry) {
    isDryRun = dry;
    return this;
  }

  protected boolean isDryRun() {
    return this.isDryRun;
  }
}
