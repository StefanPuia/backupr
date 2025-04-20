package co.uk.stefanpuia.backupr.engine.source;

public class SourceHandlerException extends RuntimeException {
  public SourceHandlerException(String message) {
    super(message);
  }

  public SourceHandlerException(final Throwable cause) {
    super(cause);
  }
}
