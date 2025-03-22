package co.uk.stefanpuia.backupr.engine.remote;

public class RemoteHandlerException extends RuntimeException {
  public RemoteHandlerException(String message) {
    super(message);
  }

  public RemoteHandlerException(final Throwable cause) {
    super(cause);
  }
}
