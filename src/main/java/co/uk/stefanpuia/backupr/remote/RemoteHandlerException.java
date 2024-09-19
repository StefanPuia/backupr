package co.uk.stefanpuia.backupr.remote;

public class RemoteHandlerException extends RuntimeException {
  public RemoteHandlerException(String message) {
    super(message);
  }

  public RemoteHandlerException(final Throwable cause) {
    super(cause);
  }
}
